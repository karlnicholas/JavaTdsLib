package com.microsoft.data.tools.tdslib.messages;

import com.microsoft.data.tools.tdslib.packets.Packet;
import com.microsoft.data.tools.tdslib.packets.PacketType;
import com.microsoft.data.tools.tdslib.payloads.Payload;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * TDS Message.
 * A message contains both metadata such as the PacketType, ResetConnection and Ignore as well as the data payload.
 * A message is transmitted in several packets, the last packet has the Last bit set.
 */
public class Message {

    /**
     * The PacketType of the packets of this message.
     */
    private PacketType packetType;

    /**
     * Indicates if the connection should be reset.
     */
    private boolean resetConnection;

    /**
     * Indicates if the message should be ignored.
     */
    private boolean ignore;

    /**
     * The payload of the message.
     */
    private Payload payload;

    /**
     * Creates a new empty message.
     *
     * @param packetType      The packet type for the message.
     * @param resetConnection Indicate if the connection should be reset.
     */
    public Message(PacketType packetType, boolean resetConnection) {
        this.packetType = packetType;
        this.resetConnection = resetConnection;
    }

    /**
     * Overload for default resetConnection = false
     */
    public Message(PacketType packetType) {
        this(packetType, false);
    }

    // --- Getters and Setters ---

    public PacketType getPacketType() {
        return packetType;
    }

    public void setPacketType(PacketType packetType) {
        this.packetType = packetType;
    }

    public boolean isResetConnection() {
        return resetConnection;
    }

    public void setResetConnection(boolean resetConnection) {
        this.resetConnection = resetConnection;
    }

    public boolean isIgnore() {
        return ignore;
    }

    public void setIgnore(boolean ignore) {
        this.ignore = ignore;
    }

    public Payload getPayload() {
        return payload;
    }

    public void setPayload(Payload payload) {
        this.payload = payload;
    }

    /**
     * Get the Packets of this message.
     *
     * @param packetSize The packet size (usually 4096).
     * @return List containing all packets of the message.
     */
    public List<Packet> getPackets(int packetSize) {
        List<Packet> packets = new ArrayList<>();
        int packetId = 1;
        int dataIndex = 0;

        int dataSize = packetSize - Packet.HEADER_LENGTH;

        // Build the full payload buffer
        ByteBuffer payloadData = payload.buildBuffer();

        // Ensure we are reading from the start
        payloadData.rewind();

        // While remaining data is larger than what fits in one packet
        while (payloadData.limit() - dataIndex > dataSize) {

            // Create a slice for this chunk
            ByteBuffer packetData = sliceBuffer(payloadData, dataIndex, dataSize);

            Packet packet = new Packet(packetType);
            packet.setPacketId(packetId++);
            packet.setResetConnection(resetConnection);
            packet.setIgnore(ignore);
            packet.addData(packetData);

            dataIndex += dataSize;
            packets.add(packet);
        }

        // Handle the Last Packet
        ByteBuffer lastPacketData = sliceBuffer(payloadData, dataIndex, payloadData.limit() - dataIndex);

        Packet lastPacket = new Packet(packetType);
        lastPacket.setPacketId(packetId++);
        lastPacket.setResetConnection(resetConnection);
        lastPacket.setIgnore(ignore);
        lastPacket.setLast(true); // Vital: Marks end of message
        lastPacket.addData(lastPacketData);

        packets.add(lastPacket);

        return packets;
    }

    /**
     * Helper to slice a Java ByteBuffer without modifying the original's position.
     */
    private ByteBuffer sliceBuffer(ByteBuffer source, int offset, int length) {
        // duplicate() shares content but has independent position/limit
        ByteBuffer dup = source.duplicate();
        dup.position(offset);
        dup.limit(offset + length);
        return dup.slice();
    }

    @Override
    public String toString() {
        return "Message=[Type=" + packetType +
                ", Reset=" + resetConnection +
                ", Ignore=" + ignore +
                ", Payload=" + payload + "]";
    }
}