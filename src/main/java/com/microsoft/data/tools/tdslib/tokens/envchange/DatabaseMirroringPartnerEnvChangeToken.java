package com.microsoft.data.tools.tdslib.tokens.envchange;

public record DatabaseMirroringPartnerEnvChangeToken(String oldValue, String newValue) implements EnvChangeToken<String> {
    @Override public EnvChangeTokenSubType getSubType() { return EnvChangeTokenSubType.DATABASE_MIRRORING_PARTNER; }
}
