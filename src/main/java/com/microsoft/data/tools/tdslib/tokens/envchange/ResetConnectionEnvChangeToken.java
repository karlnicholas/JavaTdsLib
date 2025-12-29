package com.microsoft.data.tools.tdslib.tokens.envchange;

import java.nio.ByteBuffer;

public final class ResetConnectionEnvChangeToken extends EnvChangeToken<ByteBuffer> {
    public ResetConnectionEnvChangeToken(ByteBuffer oldValue, ByteBuffer newValue) { super(oldValue, newValue); }
    @Override public EnvChangeTokenSubType getSubType() { return EnvChangeTokenSubType.RESET_CONNECTION; }
}
