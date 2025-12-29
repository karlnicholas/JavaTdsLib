package com.microsoft.data.tools.tdslib.exceptions;

import java.io.IOException;

/**
 * Exception that is thrown when a connection is unexpectedly closed.
 */
public class ConnectionClosedException extends IOException {

    private static final long serialVersionUID = 1L;

    /**
     * Creates a new instance with a default message.
     */
    public ConnectionClosedException() {
        super("Connection unexpectedly closed");
    }

    /**
     * Creates a new instance with a custom message.
     *
     * @param message Exception message.
     */
    public ConnectionClosedException(String message) {
        super(message);
    }

    /**
     * Creates a new instance with a custom message and an inner exception.
     *
     * @param message        Exception message.
     * @param innerException Inner exception.
     */
    public ConnectionClosedException(String message, Throwable innerException) {
        super(message, innerException);
    }
}