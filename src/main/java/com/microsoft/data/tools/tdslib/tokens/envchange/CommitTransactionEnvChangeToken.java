package com.microsoft.data.tools.tdslib.tokens.envchange;

import java.nio.ByteBuffer;

public record CommitTransactionEnvChangeToken(ByteBuffer oldValue, ByteBuffer newValue) implements EnvChangeToken<ByteBuffer> {
    @Override public EnvChangeTokenSubType getSubType() { return EnvChangeTokenSubType.COMMIT_TRANSACTION; }
}
