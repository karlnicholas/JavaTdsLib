package com.microsoft.data.tools.tdslib.tokens.featureextack;

import com.microsoft.data.tools.tdslib.tokens.Token;
import com.microsoft.data.tools.tdslib.tokens.TokenParser;
import com.microsoft.data.tools.tdslib.tokens.TokenStreamHandler;
import com.microsoft.data.tools.tdslib.tokens.TokenType;

import java.io.IOException;

public final class FeatureExtAckTokenParser implements TokenParser {

    @Override
    public Token parse(TokenType tokenType, TokenStreamHandler tokenStreamHandler) throws IOException {
        // Read FeatureId (1 byte)
        int byteFeatureId = tokenStreamHandler.readUInt8();

        FeatureId featureId = FeatureId.fromValue(byteFeatureId);

        if (featureId == null) {
            throw new IOException(String.format("Invalid FeatureId: 0x%02X", byteFeatureId));
        }

        // If Terminator, no data follows
        if (featureId == FeatureId.TERMINATOR) {
            return new FeatureExtAckToken(featureId);
        }

        // Read Data Length (4 bytes, unsigned)
        long dataLength = tokenStreamHandler.readUInt32LE();

        // Read Buffer
        return new FeatureExtAckToken(featureId, tokenStreamHandler.readBuffer((int) dataLength));
    }
}