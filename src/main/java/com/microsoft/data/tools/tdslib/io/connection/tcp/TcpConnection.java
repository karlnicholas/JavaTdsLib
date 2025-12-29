package com.microsoft.data.tools.tdslib.io.connection.tcp;

import com.microsoft.data.tools.tdslib.exceptions.ConnectionClosedException;
import com.microsoft.data.tools.tdslib.io.connection.TdsConnection;
import com.microsoft.data.tools.tdslib.io.connection.ConnectionOptions;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * Connection used by the TDS Client to communicate with the SQL Server via NIO.
 */
public final class TcpConnection implements TdsConnection {

    private final SocketChannel socketChannel;
    private final TcpServerEndpoint endpoint;
    private final TcpConnectionOptions options;

    // Internal buffer to avoid re-allocating on every receive
    private final ByteBuffer internalReadBuffer;

    /**
     * Creates a Tcp Connection to a SQL Server endpoint.
     *
     * @param options        Connection options.
     * @param serverEndpoint SQL Server endpoint.
     * @throws IOException If a problem occurs while setting up the socket.
     */
    public TcpConnection(TcpConnectionOptions options, TcpServerEndpoint serverEndpoint) throws IOException {
        if (serverEndpoint == null) throw new IllegalArgumentException("serverEndpoint cannot be null");
        if (options == null) throw new IllegalArgumentException("options cannot be null");

        this.endpoint = serverEndpoint;
        this.options = options;

        // 1. Open the Channel
        this.socketChannel = SocketChannel.open();

        // 2. Configure Options
        // Matches C# NoDelay = true
        this.socketChannel.setOption(StandardSocketOptions.TCP_NODELAY, true);

        // Receive Timeout
        if (options.getReceiveTimeout() > 0) {
            this.socketChannel.socket().setSoTimeout(options.getReceiveTimeout());
        }

        // Bind Local Endpoint if specified
        if (options.getLocalEndpoint() != null) {
            this.socketChannel.bind(options.getLocalEndpoint());
        }

        // 3. Connect
        // We use blocking mode to match the synchronous architecture of the C# NetworkStream
        this.socketChannel.configureBlocking(true);

        InetSocketAddress remoteAddress = endpoint.toInetSocketAddress();

        try {
            if (options.getConnectTimeout() > 0) {
                this.socketChannel.socket().connect(remoteAddress, options.getConnectTimeout());
            } else {
                this.socketChannel.connect(remoteAddress);
            }
        } catch (IOException e) {
            try {
                this.socketChannel.close();
            } catch (IOException suppressed) {
                e.addSuppressed(suppressed);
            }
            throw e;
        }

        // 4. Initialize Read Buffer
        this.internalReadBuffer = ByteBuffer.allocate(options.getPacketSize());
    }

    @Override
    public ConnectionOptions getOptions() {
        return options;
    }

    public InetSocketAddress getLocalEndpoint() {
        try {
            return (InetSocketAddress) socketChannel.getLocalAddress();
        } catch (IOException e) {
            return null;
        }
    }

    public InetSocketAddress getRemoteEndpoint() {
        try {
            return (InetSocketAddress) socketChannel.getRemoteAddress();
        } catch (IOException e) {
            return null;
        }
    }

    public boolean isConnected() {
        return socketChannel.isConnected();
    }

    @Override
    public void sendData(ByteBuffer byteBuffer) throws IOException {
        // Write the data to the channel
        // Ensure we handle partial writes (though blocking mode usually handles this)
        while (byteBuffer.hasRemaining()) {
            socketChannel.write(byteBuffer);
        }
    }

    @Override
    public ByteBuffer receiveData() throws IOException {
        internalReadBuffer.clear();

        // Read from socket
        int bytesRead = socketChannel.read(internalReadBuffer);

        if (bytesRead == -1) {
            throw new ConnectionClosedException("Connection closed unexpectedly. Read returned -1.");
        }

        if (bytesRead == 0) {
            // In blocking mode, 0 usually only happens on timeout or interruption.
            // Returning empty buffer to be safe.
            return ByteBuffer.allocate(0);
        }

        // Prepare buffer for reading by the caller
        internalReadBuffer.flip();

        // Return a fresh copy of the data
        ByteBuffer result = ByteBuffer.allocate(bytesRead);
        result.put(internalReadBuffer);
        result.flip();

        return result;
    }

    @Override
    public void clearIncomingData() throws IOException {
        // In blocking mode, we cannot easily peek "Available" bytes like C#.
        // We temporarily switch to non-blocking to drain the socket.
        boolean wasBlocking = socketChannel.isBlocking();
        try {
            socketChannel.configureBlocking(false);
            ByteBuffer dump = ByteBuffer.allocate(1024);
            while (socketChannel.read(dump) > 0) {
                dump.clear();
            }
        } finally {
            socketChannel.configureBlocking(wasBlocking);
        }
    }

    @Override
    public void startTLS() throws IOException {
        // As discussed, this requires the PreLoginTlsChannel wrapper
        throw new UnsupportedOperationException("TLS Handshake logic requires PreLoginTlsChannel implementation.");
    }

    @Override
    public void close() throws IOException {
        if (socketChannel.isOpen()) {
            socketChannel.close();
        }
    }
}