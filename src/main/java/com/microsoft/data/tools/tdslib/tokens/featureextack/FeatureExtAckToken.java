package com.microsoft.data.tools.tdslib.tokens.featureextack;

import com.microsoft.data.tools.tdslib.tokens.Token;
import com.microsoft.data.tools.tdslib.tokens.TokenType;
import java.nio.ByteBuffer;

/**
 * Feature extension acknowledge token.
 */
public final class FeatureExtAckToken extends Token {

    private final FeatureId featureId;
    private final ByteBuffer buffer;

    /**
     * Creates a new instance of the token (Terminator).
     *
     * @param featureId Feature Id.
     */
    public FeatureExtAckToken(FeatureId featureId) {
        this(featureId, null);
    }

    /**
     * Creates a new instance of the token.
     *
     * @param featureId Feature Id.
     * @param buffer    Data buffer.
     */
    public FeatureExtAckToken(FeatureId featureId, ByteBuffer buffer) {
        this.featureId = featureId;
        this.buffer = buffer;
    }

    @Override
    public TokenType getType() {
        return TokenType.FEATURE_EXT_ACK;
    }

    public FeatureId getFeatureId() {
        return featureId;
    }

    public ByteBuffer getBuffer() {
        return buffer;
    }

    @Override
    public String toString() {
        return String.format("FeatureExtAckToken=[FeatureId=%s, Buffer=%s]", featureId, buffer);
    }
}