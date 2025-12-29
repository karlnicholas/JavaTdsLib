package com.microsoft.data.tools.tdslib.tokens.envchange;

import java.nio.ByteBuffer;

public final class BeginTransactionEnvChangeToken extends EnvChangeToken<ByteBuffer> {
    public BeginTransactionEnvChangeToken(ByteBuffer oldValue, ByteBuffer newValue) { super(oldValue, newValue); }
    @Override public EnvChangeTokenSubType getSubType() { return EnvChangeTokenSubType.BEGIN_TRANSACTION; }
}
