package com.microsoft.data.tools.tdslib.tokens.envchange;

import java.nio.ByteBuffer;

public final class CommitTransactionEnvChangeToken extends EnvChangeToken<ByteBuffer> {
    public CommitTransactionEnvChangeToken(ByteBuffer oldValue, ByteBuffer newValue) { super(oldValue, newValue); }
    @Override public EnvChangeTokenSubType getSubType() { return EnvChangeTokenSubType.COMMIT_TRANSACTION; }
}
