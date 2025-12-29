package com.microsoft.data.tools.tdslib.tokens.envchange;

public final class LanguageEnvChangeToken extends EnvChangeToken<String> {
    public LanguageEnvChangeToken(String oldValue, String newValue) { super(oldValue, newValue); }
    @Override public EnvChangeTokenSubType getSubType() { return EnvChangeTokenSubType.LANGUAGE; }
}
