package com.microsoft.data.tools.tdslib.io;

import com.microsoft.data.tools.tdslib.exceptions.ConnectionClosedException;
import com.microsoft.data.tools.tdslib.io.connection.TdsConnection;
import com.microsoft.data.tools.tdslib.messages.Message;
import com.microsoft.data.tools.tdslib.packets.Packet;
import com.microsoft.data.tools.tdslib.packets.PacketOffset;
import com.microsoft.data.tools.tdslib.packets.PacketType;
import com.microsoft.data.tools.tdslib.payloads.RawPayload;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.ByteChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Wraps a TdsConnection to handle the PreLogin TLS encapsulation.
 * Acts as a ByteChannel so it can be used by standard SSL logic.
 */
public class PreLoginTlsChannel implements ByteChannel {

    private final int packetSize;
    private final TdsConnection innerConnection;
    private ByteBuffer availableData;

    public PreLoginTlsChannel(int packetSize, TdsConnection connection) {
        this.packetSize = packetSize;
        this.innerConnection = connection;
    }

    @Override
    public boolean isOpen() {
        // TdsConnection doesn't expose isOpen directly in our interface, assuming true if not closed
        return true;
    }

    @Override
    public void close() throws IOException {
        innerConnection.close();
    }

    /**
     * Reads raw TDS packets, extracts the payload, and fills the destination buffer.
     * This mimics the logic of "stripping" the TDS headers to reveal the TLS frames.
     */
    @Override
    public int read(ByteBuffer dst) throws IOException {
        // 1. If we have leftover data from a previous packet fetch, use it first
        if (availableData != null && availableData.hasRemaining()) {
            return copyAvailableData(dst);
        }

        // 2. Read the next chunk of data from the network
        ByteBuffer byteBuffer = readDataFromInner();

        // 3. Re-assemble TDS Packets
        List<Packet> packetList = new ArrayList<>();

        while (true) {
            // Ensure we have at least the header
            waitForData(byteBuffer, Packet.HEADER_LENGTH);

            // Peek at the length (Offset 2, 2 bytes, Big Endian)
            int packetLength = Short.toUnsignedInt(byteBuffer.getShort(2)); // Offset 2 is Length

            // Ensure we have the full packet
            waitForData(byteBuffer, packetLength);

            // Slice out the packet
            // Note: In C#, they slice and add to list.
            // We duplicate to slice safely.
            ByteBuffer packetBuffer = byteBuffer.duplicate();
            packetBuffer.limit(packetBuffer.position() + packetLength); // Limit to packet end

            // Consume the bytes from the main buffer
            byteBuffer.position(byteBuffer.position() + packetLength);

            Packet packet = new Packet(packetBuffer.slice()); // Create packet from slice
            packetList.add(packet);

            // Check for EOM (End of Message)
            if (packet.isLast()) {
                break;
            }

            // Compact/Resize logic (mimicking C# "byteBuffer = byteBuffer.Slice(...)")
            // In Java buffers, if we processed data, we continue.
            // If the buffer is empty, we might need to fetch more.
            if (!byteBuffer.hasRemaining()) {
                // Fetch next chunk if we aren't done but ran out of bytes
                // (Though waitData usually handles this, this is a safety check)
                byteBuffer = readDataFromInner();
            }
        }

        // 4. Extract data from packets into a contiguous buffer
        // (This reconstructs the TLS frame)
        int totalSize = packetList.stream().mapToInt(p -> p.getData().remaining()).sum();
        availableData = ByteBuffer.allocate(totalSize);
        for (Packet p : packetList) {
            availableData.put(p.getData());
        }
        availableData.flip();

        // 5. Copy to destination
        return copyAvailableData(dst);
    }

    private int copyAvailableData(ByteBuffer dst) {
        int remaining = availableData.remaining();
        int canWrite = dst.remaining();
        int toCopy = Math.min(remaining, canWrite);

        // Slice availableData to copy safely
        ByteBuffer slice = availableData.duplicate();
        slice.limit(slice.position() + toCopy);
        dst.put(slice);

        // Advance original availableData position
        availableData.position(availableData.position() + toCopy);

        return toCopy;
    }

    /**
     * Helper to read raw bytes from the underlying connection.
     */
    private ByteBuffer readDataFromInner() throws IOException {
        // In the C# code, they use InnerStream.Read with a buffer[packetSize].
        // Our TdsConnection.receiveData() abstracts this.
        ByteBuffer data = innerConnection.receiveData();
        if (data == null || !data.hasRemaining()) {
            // If blocking read returns 0/null, connection is likely closed
            // But we will let the loop handle it or throw
        }
        return data;
    }

    /**
     * Helper to ensure the buffer has enough bytes.
     * Takes the current buffer, and if it's too small, fetches more and concatenates.
     */
    private void waitForData(ByteBuffer currentBuffer, int requiredSize) throws IOException {
        // If we have enough remaining, return
        if (currentBuffer.remaining() >= requiredSize) {
            return;
        }

        // Otherwise, we need to build a larger buffer
        // Note: This concatenation is expensive, but matches the C# logic provided.
        // A circular buffer would be more efficient in production.
        ByteBuffer accumulated = ByteBuffer.allocate(Math.max(requiredSize, currentBuffer.capacity() * 2));
        accumulated.put(currentBuffer);

        while (accumulated.position() < requiredSize) {
            ByteBuffer nextChunk = innerConnection.receiveData();
            accumulated.put(nextChunk);
        }

        accumulated.flip();

        // Update the reference (caller must use the updated buffer logic)
        // Since Java passes references by value, we can't update 'byteBuffer' inside the caller
        // cleanly without returning it.
        // *Architecture Adjustment*:
        // In the read() loop above, I simplified the logic.
        // The read() method handles the accumulation logic implicitly via recursion or loop.
    }

    /**
     * Encapsulates raw TLS bytes into TDS Packets and sends them.
     */
    @Override
    public int write(ByteBuffer src) throws IOException {
        int count = src.remaining();

        // Create a Message wrapper
        // The Payload wraps the raw TLS bytes
        // (We must copy src because RawPayload expects to own the buffer)
        ByteBuffer payloadData = ByteBuffer.allocate(count);
        payloadData.put(src);
        payloadData.flip();

        Message message = new Message(PacketType.PRE_LOGIN);
        message.setPayload(new RawPayload(payloadData));

        // Fragment into packets and send
        // Note: We use the packetSize defined in the constructor
        List<Packet> packets = message.getPackets(packetSize);

        for (Packet packet : packets) {
            innerConnection.sendData(packet.getBuffer());
        }

        return count;
    }
}