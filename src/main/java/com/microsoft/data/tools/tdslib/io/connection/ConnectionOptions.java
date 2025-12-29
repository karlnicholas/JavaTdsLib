package com.microsoft.data.tools.tdslib.io.connection;

import com.microsoft.data.tools.tdslib.TdsConstants;

/**
 * Base connection options.
 */
public class ConnectionOptions {

    private int packetSize;

    public ConnectionOptions() {
        // Default packet size from constants
        this.packetSize = TdsConstants.DEFAULT_PACKET_SIZE;
    }

    /**
     * Gets or sets the packet size in bytes.
     */
    public int getPacketSize() {
        return packetSize;
    }

    public void setPacketSize(int packetSize) {
        this.packetSize = packetSize;
    }
}