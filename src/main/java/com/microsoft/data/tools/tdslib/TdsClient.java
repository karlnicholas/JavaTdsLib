package com.microsoft.data.tools.tdslib;

import com.microsoft.data.tools.tdslib.io.connection.TdsConnection;
import com.microsoft.data.tools.tdslib.io.connection.tcp.TcpConnection;
import com.microsoft.data.tools.tdslib.io.connection.tcp.TcpConnectionOptions;
import com.microsoft.data.tools.tdslib.io.connection.tcp.TcpServerEndpoint;
import com.microsoft.data.tools.tdslib.messages.MessageHandler;
import com.microsoft.data.tools.tdslib.tokens.TokenStreamHandler;

import java.io.IOException;

/**
 * TDS Client.
 * Orchestrates the Connection, Message Handling, and Token Streams.
 */
public final class TdsClient implements AutoCloseable {

    private TdsConnection connection;
    private final MessageHandler messageHandler;
    private final TokenStreamHandler tokenStreamHandler;

    /**
     * Creates a new TDS Client and establishes a Tcp connection to the endpoint specified by the TcpServerEndpoint using default connection options.
     *
     * @param serverEndpoint The database server endpoint.
     * @throws IOException If any IO error occurs.
     */
    public TdsClient(TcpServerEndpoint serverEndpoint) throws IOException {
        this(new TcpConnectionOptions(), serverEndpoint);
    }

    /**
     * Creates a new TDS Client and establishes a Tcp connection using the specified options.
     *
     * @param options        The connection options.
     * @param serverEndpoint The database server endpoint.
     * @throws IOException If any IO error occurs.
     */
    public TdsClient(TcpConnectionOptions options, TcpServerEndpoint serverEndpoint) throws IOException {
        this(new TcpConnection(options, serverEndpoint));
    }

    /**
     * Creates a new TDS Client with an existing connection.
     *
     * @param connection Underlying connection to use.
     */
    public TdsClient(TdsConnection connection) {
        if (connection == null) {
            throw new IllegalArgumentException("connection cannot be null");
        }
        this.connection = connection;

        // Initialize Handlers
        this.messageHandler = new MessageHandler(this);
        this.tokenStreamHandler = new TokenStreamHandler(this);
    }

    /**
     * Underlying connection used to communicate with the SQL Server.
     */
    public TdsConnection getConnection() {
        return connection;
    }

    /**
     * The MessageHandler of this client.
     */
    public MessageHandler getMessageHandler() {
        return messageHandler;
    }

    /**
     * The TokenStreamHandler of this client.
     */
    public TokenStreamHandler getTokenStreamHandler() {
        return tokenStreamHandler;
    }

    /**
     * Performs the TLS handshake between the client and the database server.
     */
    public void performTlsHandshake() throws IOException {
        connection.startTLS();
    }

    /**
     * Closes the connection to the actual database server and re-establishes a Tcp connection to a new database server endpoint.
     */
    public void reEstablishConnection(TcpConnectionOptions options, TcpServerEndpoint serverEndpoint) throws IOException {
        try {
            connection.close();
        } catch (Exception ignored) {
            // Best effort close
        }
        this.connection = new TcpConnection(options, serverEndpoint);
    }

    /**
     * Closes the connection to the actual database server and re-establishes a connection to a SQL Server.
     */
    public void reEstablishConnection(TdsConnection connection) {
        if (connection == null) {
            throw new IllegalArgumentException("connection cannot be null");
        }
        try {
            this.connection.close();
        } catch (Exception ignored) {
            // Best effort close
        }
        this.connection = connection;
    }

    /**
     * Disposes resources from this TDS client and underlying components.
     */
    @Override
    public void close() throws Exception {
        if (connection != null) {
            connection.close();
        }
    }
}