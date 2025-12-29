package com.microsoft.data.tools.tdslib.payloads.login7;

import java.util.Locale;
import java.util.TimeZone;

import com.microsoft.data.tools.tdslib.TdsConstants;
import com.microsoft.data.tools.tdslib.TdsVersion;

/**
 * Login7 options.
 */
public class Login7Options {

    /**
     * Tds protocol version.
     */
    private TdsVersion tdsVersion;

    /**
     * Packet size.
     * (C# uint -> Java long)
     */
    private long packetSize;

    /**
     * Client program version.
     * (C# uint -> Java long)
     */
    private long clientProgVer;

    /**
     * Process Id.
     * (C# uint -> Java long)
     */
    private long clientPid;

    /**
     * Connection Id.
     * (C# uint -> Java long)
     */
    private long connectionId;

    /**
     * Timezone offset from UTC in minutes.
     */
    private int clientTimeZone;

    /**
     * Culture info identifier (LCID).
     * (C# uint -> Java long)
     */
    private long clientLcid;

    /**
     * Create a new instance of this class with default values.
     */
    public Login7Options() {
        // Assume TdsVersion.V7_4 is defined in your enum
        this.tdsVersion = TdsVersion.V7_4;

        // Assume TdsConstants.DefaultPacketSize is defined
        this.packetSize = TdsConstants.DEFAULT_PACKET_SIZE;

        this.clientProgVer = 0;

        // Get Process ID (Requires Java 9+)
        try {
            this.clientPid = ProcessHandle.current().pid();
        } catch (Exception e) {
            // Fallback if security manager blocks access
            this.clientPid = 0;
        }

        this.connectionId = 0;

        // Calculate Timezone offset in minutes
        // C#: (int)TimeZoneInfo.Local.GetUtcOffset(DateTime.UtcNow).TotalMinutes;
        TimeZone tz = TimeZone.getDefault();
        // getOffset returns milliseconds, convert to minutes
        this.clientTimeZone = tz.getOffset(System.currentTimeMillis()) / (1000 * 60);

        // Get LCID
        // Java does not support Microsoft LCIDs natively.
        // We defaults to 1033 (en-US) or need a lookup table.
        this.clientLcid = getLcidFromLocale(Locale.getDefault());
    }

    /**
     * Helper to map Java Locale to Microsoft LCID.
     * This requires a lookup table for full support.
     */
    private long getLcidFromLocale(Locale locale) {
        if (Locale.US.equals(locale)) return 1033;
        if (Locale.UK.equals(locale)) return 2057;
        if (Locale.GERMANY.equals(locale)) return 1031;
        // ... add other mappings as needed
        return 1033; // Default to en-US
    }

    // Getters and Setters

    public TdsVersion getTdsVersion() {
        return tdsVersion;
    }

    public void setTdsVersion(TdsVersion tdsVersion) {
        this.tdsVersion = tdsVersion;
    }

    public long getPacketSize() {
        return packetSize;
    }

    public void setPacketSize(long packetSize) {
        this.packetSize = packetSize;
    }

    public long getClientProgVer() {
        return clientProgVer;
    }

    public void setClientProgVer(long clientProgVer) {
        this.clientProgVer = clientProgVer;
    }

    public long getClientPid() {
        return clientPid;
    }

    public void setClientPid(long clientPid) {
        this.clientPid = clientPid;
    }

    public long getConnectionId() {
        return connectionId;
    }

    public void setConnectionId(long connectionId) {
        this.connectionId = connectionId;
    }

    public int getClientTimeZone() {
        return clientTimeZone;
    }

    public void setClientTimeZone(int clientTimeZone) {
        this.clientTimeZone = clientTimeZone;
    }

    public long getClientLcid() {
        return clientLcid;
    }

    public void setClientLcid(long clientLcid) {
        this.clientLcid = clientLcid;
    }

    @Override
    public String toString() {
        return "Options[TdsVersion=" + tdsVersion +
                ", PacketSize=" + packetSize +
                ", ClientProgVer=" + clientProgVer +
                ", ClientPid=" + clientPid +
                ", ConnectionId=" + connectionId +
                ", ClientTimeZone=" + clientTimeZone +
                ", ClientLcid=" + clientLcid + "]";
    }
}