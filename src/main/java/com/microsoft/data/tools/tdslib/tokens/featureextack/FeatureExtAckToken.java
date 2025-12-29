package com.microsoft.data.tools.tdslib.tokens.featureextack;

import com.microsoft.data.tools.tdslib.tokens.Token;
import com.microsoft.data.tools.tdslib.tokens.TokenType;
import java.nio.ByteBuffer;

/**
 * Feature extension acknowledge token.
 */
public record FeatureExtAckToken(FeatureId featureId, ByteBuffer buffer) implements Token {

    /**
     * Creates a new instance of the token (Terminator or no data).
     *
     * @param featureId Feature Id.
     */
    public FeatureExtAckToken(FeatureId featureId) {
        this(featureId, null);
    }

    @Override
    public TokenType getType() {
        return TokenType.FEATURE_EXT_ACK;
    }
}