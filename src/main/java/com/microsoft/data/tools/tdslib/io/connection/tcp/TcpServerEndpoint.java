package com.microsoft.data.tools.tdslib.io.connection.tcp;

import java.net.InetSocketAddress;
import java.util.Objects;

/**
 * TCP server endpoint information.
 */
public final class TcpServerEndpoint {

    private final String hostname;
    private final int port;

    /**
     * Create a new endpoint with a hostname and port.
     *
     * @param hostname The server endpoint hostname.
     * @param port     The server endpoint port.
     */
    public TcpServerEndpoint(String hostname, int port) {
        if (hostname == null) {
            throw new IllegalArgumentException("hostname cannot be null");
        }
        // Validating port range [0-65535]
        if (port < 0 || port > 65535) {
            throw new IllegalArgumentException("Specified port is invalid, outside valid range [0-65535]");
        }

        this.hostname = hostname;
        this.port = port;
    }

    public String getHostname() {
        return hostname;
    }

    public int getPort() {
        return port;
    }

    /**
     * Helper to create a Java InetSocketAddress.
     */
    public InetSocketAddress toInetSocketAddress() {
        return new InetSocketAddress(hostname, port);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TcpServerEndpoint that = (TcpServerEndpoint) o;
        return port == that.port && Objects.equals(hostname, that.hostname);
    }

    @Override
    public int hashCode() {
        return Objects.hash(hostname, port);
    }

    @Override
    public String toString() {
        return String.format("TcpServerEndpoint[Hostname=%s, Port=%d]", hostname, port);
    }
}