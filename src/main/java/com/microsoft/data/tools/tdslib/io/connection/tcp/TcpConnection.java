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
    private final ByteBuffer internalReadBuffer;

    public TcpConnection(TcpConnectionOptions options, TcpServerEndpoint serverEndpoint) throws IOException {
        if (serverEndpoint == null) throw new IllegalArgumentException("serverEndpoint cannot be null");
        if (options == null) throw new IllegalArgumentException("options cannot be null");

        this.endpoint = serverEndpoint;
        this.options = options;

        // 1. Open the Channel
        this.socketChannel = SocketChannel.open();

        // 2. Configure Options
        socketChannel.setOption(StandardSocketOptions.TCP_NODELAY, true);

        if (options.getReceiveTimeout() > 0) {
            socketChannel.socket().setSoTimeout(options.getReceiveTimeout());
        }

        if (options.getLocalEndpoint() != null) {
            socketChannel.bind(options.getLocalEndpoint());
        }

        // 3. Connect (Blocking mode to mimic C# NetworkStream behavior)
        socketChannel.configureBlocking(true);

        var remoteAddress = endpoint.toInetSocketAddress();

        try {
            if (options.getConnectTimeout() > 0) {
                socketChannel.socket().connect(remoteAddress, options.getConnectTimeout());
            } else {
                socketChannel.connect(remoteAddress);
            }
        } catch (IOException e) {
            try {
                socketChannel.close();
            } catch (IOException suppressed) {
                e.addSuppressed(suppressed);
            }
            throw e;
        }

        // 4. Initialize Buffer
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
        while (byteBuffer.hasRemaining()) {
            socketChannel.write(byteBuffer);
        }
    }

    @Override
    public ByteBuffer receiveData() throws IOException {
        internalReadBuffer.clear();

        int bytesRead = socketChannel.read(internalReadBuffer);

        if (bytesRead == -1) {
            throw new ConnectionClosedException("Connection closed unexpectedly. Read returned -1.");
        }

        if (bytesRead == 0) {
            return ByteBuffer.allocate(0);
        }

        internalReadBuffer.flip();

        // Return a fresh copy
        var result = ByteBuffer.allocate(bytesRead);
        result.put(internalReadBuffer);
        result.flip();

        return result;
    }

    @Override
    public void clearIncomingData() throws IOException {
        // Temporarily switch to non-blocking to drain socket
        boolean wasBlocking = socketChannel.isBlocking();
        try {
            socketChannel.configureBlocking(false);
            var dump = ByteBuffer.allocate(1024);
            while (socketChannel.read(dump) > 0) {
                dump.clear();
            }
        } finally {
            socketChannel.configureBlocking(wasBlocking);
        }
    }

    @Override
    public void startTLS() throws IOException {
        throw new UnsupportedOperationException("TLS Handshake logic requires PreLoginTlsChannel implementation.");
    }

    @Override
    public void close() throws IOException {
        if (socketChannel.isOpen()) {
            socketChannel.close();
        }
    }
}