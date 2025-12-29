package com.microsoft.data.tools.tdslib.payloads.login7.auth;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;

/**
 * Security token federated authentication.
 */
public final class SecurityTokenFedAuth extends FedAuth {

    private final String token;
    private final boolean echo;

    /**
     * Create a new instance of this class.
     *
     * @param token The access token.
     * @param echo  Echo flag.
     */
    public SecurityTokenFedAuth(String token, boolean echo) {
        if (token == null || token.isEmpty()) {
            throw new IllegalArgumentException("Token cannot be null or empty");
        }
        this.token = token;
        this.echo = echo;
    }

    /**
     * Overload for default echo = false.
     * * @param token The access token.
     */
    public SecurityTokenFedAuth(String token) {
        this(token, false);
    }

    public String getToken() {
        return token;
    }

    public boolean isEcho() {
        return echo;
    }

    @Override
    public ByteBuffer getBuffer() {
        // Convert token to UTF-16LE bytes
        byte[] tokenBytes = token.getBytes(StandardCharsets.UTF_16LE);
        int tokenLength = tokenBytes.length;

        // Calculate total size:
        // 1 byte  (FeatureId)
        // 4 bytes (Outer Length = TokenLength + 4 (inner len) + 1 (options))
        // 1 byte  (Options)
        // 4 bytes (Inner Token Length)
        // N bytes (Token Data)
        int headerSize = 10;
        int totalSize = headerSize + tokenLength;

        ByteBuffer buffer = ByteBuffer.allocate(totalSize);
        buffer.order(ByteOrder.LITTLE_ENDIAN);

        // 1. Feature Id
        buffer.put(FEATURE_ID);

        // 2. Outer Length
        // C# logic: tokenBuffer.Length + 4 + 1
        int outerLength = tokenLength + 5;
        buffer.putInt(outerLength);

        // 3. Options
        byte options = (byte) (LIBRARY_SECURITY_TOKEN | (echo ? FED_AUTH_ECHO_YES : FED_AUTH_ECHO_NO));
        buffer.put(options);

        // 4. Inner Token Length
        buffer.putInt(tokenLength);

        // 5. Token Data
        buffer.put(tokenBytes);

        buffer.flip();
        return buffer;
    }
}