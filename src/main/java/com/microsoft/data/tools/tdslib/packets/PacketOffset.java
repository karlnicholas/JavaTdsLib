package com.microsoft.data.tools.tdslib.packets;

/**
 * Offsets for fields within the TDS Packet Header.
 */
public final class PacketOffset {

    // Private constructor to prevent instantiation
    private PacketOffset() {}

    public static final int TYPE = 0;
    public static final int STATUS = 1;
    public static final int LENGTH = 2;
    public static final int SPID = 4;
    public static final int PACKET_ID = 6;
    public static final int WINDOW = 7;
}