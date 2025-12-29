package com.microsoft.data.tools.tdslib.tokens.envchange;

public class RoutingInfo {
    private final byte protocol;
    private final int port;
    private final String server;

    public RoutingInfo(byte protocol, int port, String server) {
        this.protocol = protocol;
        this.port = port;
        this.server = server;
    }

    public byte getProtocol() { return protocol; }
    public int getPort() { return port; }
    public String getServer() { return server; }

    @Override
    public String toString() {
        return String.format("RoutingInfo[Protocol=%d, Server=%s, Port=%d]", protocol, server, port);
    }
}