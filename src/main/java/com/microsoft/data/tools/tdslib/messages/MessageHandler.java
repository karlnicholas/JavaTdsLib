package com.microsoft.data.tools.tdslib.messages;

import com.microsoft.data.tools.tdslib.TdsClient; // Placeholder, see note below
import com.microsoft.data.tools.tdslib.packets.Packet;
import com.microsoft.data.tools.tdslib.packets.PacketOffset;
import com.microsoft.data.tools.tdslib.packets.PacketType;
import com.microsoft.data.tools.tdslib.payloads.Payload;
import com.microsoft.data.tools.tdslib.payloads.RawPayload;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * Message handler.
 * Handles fragmentation (sending) and re-assembly (receiving) of TDS messages.
 */
public class MessageHandler {

    private final TdsClient tdsClient;
    private ByteBuffer incomingMessageBuffer;

    public MessageHandler(TdsClient tdsClient) {
        this.tdsClient = tdsClient;
        // Initialize with empty buffer
        this.incomingMessageBuffer = ByteBuffer.allocate(0);
    }

    /**
     * Send messages to the SQL server.
     *
     * @param message           The message to send.
     * @throws IOException If an IO problem occurs.
     */
    public void sendMessage(Message message) throws IOException {
        // Get the packet size from the connection options
        int packetSize = tdsClient.getConnection().getOptions().getPacketSize();

        List<Packet> packets = message.getPackets(packetSize);
        for (Packet packet : packets) {
            tdsClient.getConnection().sendData(packet.getBuffer());
        }
    }

    /**
     * Receives a message from the SQL Server with a typed Payload generator.
     *
     * @param payloadGenerator The function to convert buffer to Payload.
     * @param <P>              The Payload type.
     * @return The received Message.
     * @throws IOException If an IO problem occurs.
     */
    public <P extends Payload> Message receiveMessage(Function<ByteBuffer, P> payloadGenerator) throws IOException {
        try {
            ReceivedData data = receiveMessageRaw();

            Message message = new Message(data.packetType);
            message.setPayload(payloadGenerator.apply(data.payloadBuffer));

            return message;

        } catch (Exception e) {
            // Reset buffer on error
            incomingMessageBuffer = ByteBuffer.allocate(0);
            throw new IOException("Error receiving message", e);
        }
    }

    /**
     * Receives a message from the SQL Server (Default RawPayload).
     *
     * @return The received Message.
     * @throws IOException If an IO problem occurs.
     */
    public Message receiveMessage() throws IOException {
        try {
            ReceivedData data = receiveMessageRaw();

            Message message = new Message(data.packetType);
            message.setPayload(new RawPayload(data.payloadBuffer));

            return message;

        } catch (Exception e) {
            incomingMessageBuffer = ByteBuffer.allocate(0);
            throw new IOException("Error receiving message", e);
        }
    }

    // Helper class to return multiple values (PacketType + Buffer)
    private static class ReceivedData {
        PacketType packetType;
        ByteBuffer payloadBuffer;

        public ReceivedData(PacketType packetType, ByteBuffer payloadBuffer) {
            this.packetType = packetType;
            this.payloadBuffer = payloadBuffer;
        }
    }

    private ReceivedData receiveMessageRaw() throws IOException {
        // If buffer is empty, read initial data
        if (incomingMessageBuffer.remaining() == 0) {
            ByteBuffer newData = tdsClient.getConnection().receiveData();
            incomingMessageBuffer = concatBuffers(incomingMessageBuffer, newData);
        } else {
            // In C# they append eagerly.
            // In Java, we might check if we actually need more data first to avoid blocking,
            // but for strict translation, we fetch more:
            ByteBuffer newData = tdsClient.getConnection().receiveData();
            incomingMessageBuffer = concatBuffers(incomingMessageBuffer, newData);
        }

        List<Packet> packetList = new ArrayList<>();

        while (true) {
            // 1. Ensure we have at least the Header (8 bytes)
            waitForData(Packet.HEADER_LENGTH);

            // 2. Read Packet Length from Header (Offset 2, 2 bytes, Big Endian)
            // We use duplicate to read without consuming the buffer position yet
            ByteBuffer headerView = incomingMessageBuffer.duplicate();
            headerView.order(ByteOrder.BIG_ENDIAN);
            int packetLength = Short.toUnsignedInt(headerView.getShort(PacketOffset.LENGTH));

            // 3. Ensure we have the full packet
            waitForData(packetLength);

            // 4. Slice the packet out of the buffer
            // Slice the specific packet data [0...packetLength]
            ByteBuffer packetBuffer = slice(incomingMessageBuffer, 0, packetLength);
            Packet packet = new Packet(packetBuffer);
            packetList.add(packet);

            // 5. Advance the buffer
            if (packetLength < incomingMessageBuffer.remaining()) {
                // Move buffer forward by slicing the remaining part
                incomingMessageBuffer.position(incomingMessageBuffer.position() + packetLength);
                incomingMessageBuffer = incomingMessageBuffer.slice();
            } else {
                // Consumed everything
                incomingMessageBuffer = ByteBuffer.allocate(0);
            }

            // 6. Check if this was the last packet
            if (packet.isLast()) {
                break;
            }
        }

        // Combine all packet data into one Payload buffer
        ByteBuffer completePayload = combinePacketData(packetList);

        PacketType type = packetList.isEmpty() ? PacketType.UNKNOWN : packetList.get(0).getType();
        return new ReceivedData(type, completePayload);
    }

    private void waitForData(int size) throws IOException {
        while (incomingMessageBuffer.remaining() < size) {
            ByteBuffer newData = tdsClient.getConnection().receiveData();
            incomingMessageBuffer = concatBuffers(incomingMessageBuffer, newData);
        }
    }

    // --- Buffer Helper Methods ---

    /**
     * Concatenates two buffers into a new one.
     */
    private ByteBuffer concatBuffers(ByteBuffer a, ByteBuffer b) {
        if (a == null && b == null) return ByteBuffer.allocate(0);
        if (a == null || a.remaining() == 0) return b;
        if (b == null || b.remaining() == 0) return a;

        ByteBuffer combined = ByteBuffer.allocate(a.remaining() + b.remaining());
        combined.order(ByteOrder.LITTLE_ENDIAN);
        combined.put(a);
        combined.put(b);
        combined.flip();
        return combined;
    }

    /**
     * Slices a portion of the buffer.
     */
    private ByteBuffer slice(ByteBuffer source, int offset, int length) {
        ByteBuffer dup = source.duplicate();
        dup.position(source.position() + offset); // Relative to current position
        dup.limit(dup.position() + length);
        return dup.slice().order(ByteOrder.LITTLE_ENDIAN);
    }

    /**
     * Extracts data from all packets and merges them into one buffer.
     */
    private ByteBuffer combinePacketData(List<Packet> packets) {
        int totalSize = 0;
        for (Packet p : packets) {
            totalSize += p.getData().remaining();
        }

        ByteBuffer result = ByteBuffer.allocate(totalSize);
        result.order(ByteOrder.LITTLE_ENDIAN);

        for (Packet p : packets) {
            result.put(p.getData());
        }
        result.flip();
        return result;
    }
}