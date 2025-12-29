package com.microsoft.data.tools.tdslib.packets;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

// Placeholders for dependencies we haven't translated yet.
// You will likely overwrite these with the actual Enum files later.
import com.microsoft.data.tools.tdslib.packets.PacketType;
import com.microsoft.data.tools.tdslib.packets.PacketStatus;
import com.microsoft.data.tools.tdslib.packets.PacketOffset;

/**
 * Represents a Packet in the TDS protocol.
 */
public class Packet {

    // --- Constants ---

    /**
     * Header length.
     */
    public static final int HEADER_LENGTH = 8;

    /**
     * Default SPID value.
     */
    public static final int DEFAULT_SPID = 0;

    /**
     * Default Packet Id.
     */
    public static final byte DEFAULT_PACKET_ID = 1;

    /**
     * Default Window.
     */
    public static final byte DEFAULT_WINDOW = 0;

    // --- Fields ---

    /**
     * Buffer containing the packet (header + data).
     */
    private ByteBuffer buffer;

    // --- Properties / Getters & Setters ---

    public ByteBuffer getBuffer() {
        return buffer;
    }

    /**
     * Type of the packet.
     */
    public PacketType getType() {
        byte typeByte = buffer.get(PacketOffset.TYPE);
        return PacketType.fromValue(typeByte);
    }

    /**
     * Status of the packet.
     */
    public byte getStatus() {
        return buffer.get(PacketOffset.STATUS);
    }

    /**
     * Server Process Id.
     * Note: SPID is Big Endian in the header.
     */
    public int getSPId() {
        buffer.order(ByteOrder.BIG_ENDIAN);
        int spid = Short.toUnsignedInt(buffer.getShort(PacketOffset.SPID));
        buffer.order(ByteOrder.LITTLE_ENDIAN); // Reset to default TDS Little Endian
        return spid;
    }

    /**
     * Packet Id.
     */
    public byte getId() {
        return buffer.get(PacketOffset.PACKET_ID);
    }

    public void setId(byte id) {
        buffer.put(PacketOffset.PACKET_ID, id);
    }

    /**
     * Window value.
     */
    public byte getWindow() {
        return buffer.get(PacketOffset.WINDOW);
    }

    /**
     * Total length of the packet (header + data).
     * Note: Length is Big Endian in the header.
     */
    public int getLength() {
        buffer.order(ByteOrder.BIG_ENDIAN);
        int len = Short.toUnsignedInt(buffer.getShort(PacketOffset.LENGTH));
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        return len;
    }

    /**
     * Indicates if this packet is the last packet for a Message.
     */
    public boolean isLast() {
        return (getStatus() & PacketStatus.EOM) == PacketStatus.EOM;
    }

    public void setLast(boolean isLast) {
        byte status = buffer.get(PacketOffset.STATUS);
        if (isLast) {
            status |= PacketStatus.EOM;
        } else {
            status &= ~PacketStatus.EOM;
        }
        buffer.put(PacketOffset.STATUS, status);
    }

    /**
     * Indicates if the packet (and message) should be ignored.
     */
    public boolean isIgnore() {
        return (getStatus() & PacketStatus.IGNORE) == PacketStatus.IGNORE;
    }

    public void setIgnore(boolean ignore) {
        byte status = buffer.get(PacketOffset.STATUS);
        if (ignore) {
            status |= PacketStatus.IGNORE;
        } else {
            status &= ~PacketStatus.IGNORE;
        }
        buffer.put(PacketOffset.STATUS, status);
    }

    /**
     * Indicates if the connection should be reset.
     */
    public boolean isResetConnection() {
        return (getStatus() & PacketStatus.RESET_CONNECTION) == PacketStatus.RESET_CONNECTION;
    }

    public void setResetConnection(boolean reset) {
        byte status = buffer.get(PacketOffset.STATUS);
        if (reset) {
            status |= PacketStatus.RESET_CONNECTION;
        } else {
            status &= ~PacketStatus.RESET_CONNECTION;
        }
        buffer.put(PacketOffset.STATUS, status);
    }

    /**
     * Gets a copy of the data of this packet (excluding header).
     * May be an empty buffer if there is no data in the packet.
     */
    public ByteBuffer getData() {
        if (buffer.capacity() == HEADER_LENGTH) {
            return ByteBuffer.allocate(0);
        }
        // Slice creates a view starting at current position
        ByteBuffer dup = buffer.duplicate();
        dup.position(HEADER_LENGTH);
        return dup.slice();
    }

    // --- Constructors ---

    /**
     * Creates a new packet with the specified type.
     *
     * @param type The packet type.
     */
    public Packet(PacketType type) {
        this.buffer = ByteBuffer.allocate(HEADER_LENGTH);
        this.buffer.order(ByteOrder.LITTLE_ENDIAN); // Default TDS payload order

        this.buffer.put(PacketOffset.TYPE, type.getValue());
        this.buffer.put(PacketOffset.STATUS, PacketStatus.NORMAL); // Normal = 0x00

        // Header fields SPID and Length are Big Endian
        this.buffer.order(ByteOrder.BIG_ENDIAN);
        this.buffer.putShort(PacketOffset.SPID, (short) DEFAULT_SPID);
        this.buffer.order(ByteOrder.LITTLE_ENDIAN);

        this.buffer.put(PacketOffset.PACKET_ID, DEFAULT_PACKET_ID);
        this.buffer.put(PacketOffset.WINDOW, DEFAULT_WINDOW);

        updateLength();
    }

    /**
     * Creates a packet from a buffer.
     *
     * @param buffer The buffer.
     */
    public Packet(ByteBuffer buffer) {
        if (buffer == null) {
            throw new IllegalArgumentException("buffer cannot be null");
        }
        if (buffer.capacity() < HEADER_LENGTH) {
            throw new IllegalArgumentException("Buffer length must be greater than the packet header length");
        }

        // Validate Type
        byte typeByte = buffer.get(PacketOffset.TYPE);
        // Assuming PacketType.PRE_LOGIN is the highest value for validation check
        // if (typeByte > PacketType.PRE_LOGIN.getValue()) ...

        this.buffer = buffer;
        this.buffer.order(ByteOrder.LITTLE_ENDIAN);
    }

    // --- Methods ---

    private void updateLength() {
        // Length field is Big Endian
        this.buffer.order(ByteOrder.BIG_ENDIAN);
        this.buffer.putShort(PacketOffset.LENGTH, (short) buffer.capacity());
        this.buffer.order(ByteOrder.LITTLE_ENDIAN);
    }

    /**
     * Sets the Packet Id of this packet with an integer.
     * The value will be truncated (by modulo) to byte.MaxValue.
     */
    public void setPacketId(int packetId) {
        setId((byte) (packetId % 256));
    }

    /**
     * Adds data to this packet.
     * This requires re-allocating the buffer since standard ByteBuffers are fixed size.
     *
     * @param data The buffer to add to this packet.
     */
    public void addData(ByteBuffer data) {
        ByteBuffer newBuffer = ByteBuffer.allocate(this.buffer.capacity() + data.remaining());
        newBuffer.order(ByteOrder.LITTLE_ENDIAN);

        // Copy old buffer
        this.buffer.rewind();
        newBuffer.put(this.buffer);

        // Copy new data
        newBuffer.put(data);

        newBuffer.flip();
        this.buffer = newBuffer;

        updateLength();
    }

    @Override
    public String toString() {
        return String.format("Packet[Type=0x%02X, Status=0x%02X, Length=%d, SPID=0x%04X, PacketId=%d]",
                getType().getValue(), getStatus(), getLength(), getSPId(), getId());
    }
}