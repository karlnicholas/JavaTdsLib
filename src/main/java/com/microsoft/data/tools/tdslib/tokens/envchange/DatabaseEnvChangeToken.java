package com.microsoft.data.tools.tdslib.tokens.envchange;

public final class DatabaseEnvChangeToken extends EnvChangeToken<String> {
    public DatabaseEnvChangeToken(String oldValue, String newValue) { super(oldValue, newValue); }
    @Override public EnvChangeTokenSubType getSubType() { return EnvChangeTokenSubType.DATABASE; }
}

