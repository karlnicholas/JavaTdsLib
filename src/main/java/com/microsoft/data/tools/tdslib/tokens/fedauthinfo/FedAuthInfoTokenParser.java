package com.microsoft.data.tools.tdslib.tokens.fedauthinfo;

import com.microsoft.data.tools.tdslib.tokens.Token;
import com.microsoft.data.tools.tdslib.tokens.TokenParser;
import com.microsoft.data.tools.tdslib.tokens.TokenStreamHandler;
import com.microsoft.data.tools.tdslib.tokens.TokenType;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;

public final class FedAuthInfoTokenParser implements TokenParser {

    @Override
    public Token parse(TokenType tokenType, TokenStreamHandler h) throws IOException {
        // 1. Read Total Token Length
        long tokenLength = h.readUInt32LE();

        // 2. Read the whole token data into a buffer
        ByteBuffer buffer = h.readBuffer((int) tokenLength);
        buffer.order(ByteOrder.LITTLE_ENDIAN);

        // 3. Parse Count of IDs
        // Note: Java ByteBuffer maintains a position.
        // We will read sequentially for the headers.
        long countOfIds = Integer.toUnsignedLong(buffer.getInt());

        String spn = null;
        String stsUrl = null;

        for (int i = 0; i < countOfIds; i++) {
            // Read ID (1 byte)
            byte fedAuthInfoIdByte = buffer.get();

            // Read Data Length (4 bytes)
            long dataLength = Integer.toUnsignedLong(buffer.getInt());

            // Read Data Offset (4 bytes) - Relative to start of Token Data
            int dataOffset = buffer.getInt();

            FedAuthInfoId id = FedAuthInfoId.fromValue(fedAuthInfoIdByte);

            if (id != null) {
                String value = extractString(buffer, dataOffset, (int) dataLength);

                if (id == FedAuthInfoId.SPN) {
                    spn = value;
                } else if (id == FedAuthInfoId.STS_URL) {
                    stsUrl = value;
                }
            }
        }

        return new FedAuthInfoToken(spn, stsUrl);
    }

    /**
     * Helper to extract a String from a specific offset without disturbing the main buffer cursor.
     */
    private String extractString(ByteBuffer source, int offset, int length) {
        if (length == 0) return "";

        byte[] bytes = new byte[length];

        // Use absolute positioning if available or duplicate
        ByteBuffer view = source.duplicate();
        view.position(offset);
        view.get(bytes);

        return new String(bytes, StandardCharsets.UTF_16LE);
    }
}