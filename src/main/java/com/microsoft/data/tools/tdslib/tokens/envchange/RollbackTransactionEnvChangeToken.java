package com.microsoft.data.tools.tdslib.tokens.envchange;

import java.nio.ByteBuffer;

public final class RollbackTransactionEnvChangeToken extends EnvChangeToken<ByteBuffer> {
    public RollbackTransactionEnvChangeToken(ByteBuffer oldValue, ByteBuffer newValue) { super(oldValue, newValue); }
    @Override public EnvChangeTokenSubType getSubType() { return EnvChangeTokenSubType.ROLLBACK_TRANSACTION; }
}
