package com.microsoft.data.tools.tdslib.io.connection.tcp;

import java.net.InetSocketAddress;

/**
 * TCP server endpoint information.
 * (Java 17 Record implementation)
 */
public record TcpServerEndpoint(String hostname, int port) {

    public TcpServerEndpoint {
        if (hostname == null) {
            throw new IllegalArgumentException("hostname cannot be null");
        }
        if (port < 0 || port > 65535) {
            throw new IllegalArgumentException("Specified port is invalid, outside valid range [0-65535]");
        }
    }

    /**
     * Helper to create a Java InetSocketAddress.
     */
    public InetSocketAddress toInetSocketAddress() {
        return new InetSocketAddress(hostname, port);
    }
}