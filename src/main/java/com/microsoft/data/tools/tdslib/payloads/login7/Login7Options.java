// Copyright (c) Microsoft Corporation.
// Licensed under the MIT License.

package com.microsoft.data.tools.tdslib.payloads.login7;

import com.microsoft.data.tools.tdslib.TdsConstants;
import com.microsoft.data.tools.tdslib.TdsVersion;

import java.time.Instant;
import java.time.ZoneId;

public class Login7Options {

    private TdsVersion tdsVersion;
    private int packetSize;
    private long clientProgVer;
    private long clientPid;
    private long connectionId;
    private int clientTimeZone;
    private long clientLcid;

    public Login7Options() {
        tdsVersion = TdsVersion.V7_4;
        packetSize = TdsConstants.DEFAULT_PACKET_SIZE;
        clientProgVer = 0;
        clientPid = ProcessHandle.current().pid();
        connectionId = 0;
        clientTimeZone = ZoneId.systemDefault().getRules().getOffset(Instant.now()).getTotalSeconds() / 60;
        clientLcid = 0; // Java does not have LCID; left as 0
    }

    public TdsVersion getTdsVersion() { return tdsVersion; }
    public void setTdsVersion(TdsVersion v) { this.tdsVersion = v; }

    public int getPacketSize() { return packetSize; }
    public void setPacketSize(int packetSize) { this.packetSize = packetSize; }

    public long getClientProgVer() { return clientProgVer; }
    public void setClientProgVer(long clientProgVer) { this.clientProgVer = clientProgVer; }

    public long getClientPid() { return clientPid; }
    public void setClientPid(long clientPid) { this.clientPid = clientPid; }

    public long getConnectionId() { return connectionId; }
    public void setConnectionId(long connectionId) { this.connectionId = connectionId; }

    public int getClientTimeZone() { return clientTimeZone; }
    public void setClientTimeZone(int clientTimeZone) { this.clientTimeZone = clientTimeZone; }

    public long getClientLcid() { return clientLcid; }
    public void setClientLcid(long clientLcid) { this.clientLcid = clientLcid; }

    @Override
    public String toString() {
        return String.format("Options[TdsVersion=%s, PacketSize=%d, ClientProgVer=%d, ClientPid=%d, ConnectionId=%d, ClientTimeZone=%d, ClientLcid=%d]",
                tdsVersion, packetSize, clientProgVer, clientPid, connectionId, clientTimeZone, clientLcid);
    }
}
