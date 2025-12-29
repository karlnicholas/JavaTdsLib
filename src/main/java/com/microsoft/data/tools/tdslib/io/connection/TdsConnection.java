package com.microsoft.data.tools.tdslib.io.connection;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Connection to the SQL Server.
 * (Translates C# IConnection)
 */
public interface TdsConnection extends AutoCloseable {

    /**
     * Connection options.
     */
    ConnectionOptions getOptions();

    /**
     * Starts the SSL/TLS by performing a handshake with the SQL Server.
     */
    void startTLS() throws IOException;

    /**
     * Sends data.
     */
    void sendData(ByteBuffer byteBuffer) throws IOException;

    /**
     * Receives data.
     */
    ByteBuffer receiveData() throws IOException;

    /**
     * Clear all incoming data.
     */
    void clearIncomingData() throws IOException;

    // AutoCloseable.close() throws Exception, we narrow it to IOException for cleaner usage
    @Override
    void close() throws IOException;
}