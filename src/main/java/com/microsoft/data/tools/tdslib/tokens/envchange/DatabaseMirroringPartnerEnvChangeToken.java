package com.microsoft.data.tools.tdslib.tokens.envchange;

public final class DatabaseMirroringPartnerEnvChangeToken extends EnvChangeToken<String> {
    public DatabaseMirroringPartnerEnvChangeToken(String oldValue, String newValue) { super(oldValue, newValue); }
    @Override public EnvChangeTokenSubType getSubType() { return EnvChangeTokenSubType.DATABASE_MIRRORING_PARTNER; }
}
