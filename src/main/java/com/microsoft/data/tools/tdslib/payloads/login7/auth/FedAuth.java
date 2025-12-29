package com.microsoft.data.tools.tdslib.payloads.login7.auth;

import java.nio.ByteBuffer;

/**
 * Federate authentication feature extension.
 */
public abstract class FedAuth {

    /**
     * Feature Id.
     */
    protected static final byte FEATURE_ID = 0x02;

    /**
     * Security token.
     */
    protected static final byte LIBRARY_SECURITY_TOKEN = 0x02;

    /**
     * Active Directory Authentication Library.
     */
    protected static final byte LIBRARY_ADAL = 0x04;

    /**
     * Enable echo.
     */
    protected static final byte FED_AUTH_ECHO_YES = 0x01;

    /**
     * Disable echo.
     */
    protected static final byte FED_AUTH_ECHO_NO = 0x00;

    protected FedAuth() {
    }

    /**
     * Gets the buffer for this authentication feature.
     * <p>
     * Note: Changed from C# 'internal' to Java 'public' to allow access
     * from the parent Login7Payload package.
     * * @return The ByteBuffer containing the auth data.
     */
    public abstract ByteBuffer getBuffer();

}