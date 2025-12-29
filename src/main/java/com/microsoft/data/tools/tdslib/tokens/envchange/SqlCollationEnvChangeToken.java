package com.microsoft.data.tools.tdslib.tokens.envchange;

import java.nio.ByteBuffer;

public final class SqlCollationEnvChangeToken extends EnvChangeToken<ByteBuffer> {
    public SqlCollationEnvChangeToken(ByteBuffer oldValue, ByteBuffer newValue) { super(oldValue, newValue); }
    @Override public EnvChangeTokenSubType getSubType() { return EnvChangeTokenSubType.SQL_COLLATION; }
}

