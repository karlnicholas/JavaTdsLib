package com.microsoft.data.tools.tdslib.tokens.featureextack;

import com.microsoft.data.tools.tdslib.tokens.Token;
import com.microsoft.data.tools.tdslib.tokens.TokenParser;
import com.microsoft.data.tools.tdslib.tokens.TokenStreamHandler;
import com.microsoft.data.tools.tdslib.tokens.TokenType;

import java.io.IOException;

public final class FeatureExtAckTokenParser implements TokenParser {

    @Override
    public Token parse(TokenType tokenType, TokenStreamHandler h) throws IOException {
        // Read Feature ID (1 byte)
        int byteFeatureId = h.readUInt8();

        FeatureId featureId = FeatureId.fromValue(byteFeatureId);
        if (featureId == null) {
            throw new IOException("Invalid FeatureId: " + Integer.toHexString(byteFeatureId));
        }

        if (featureId == FeatureId.TERMINATOR) {
            return new FeatureExtAckToken(featureId);
        }

        // Read Data Length (4 bytes)
        long dataLength = h.readUInt32LE();

        // Read Data
        return new FeatureExtAckToken(featureId, h.readBuffer((int) dataLength));
    }
}