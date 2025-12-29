package com.microsoft.data.tools.tdslib.tokens.envchange;

public record LanguageEnvChangeToken(String oldValue, String newValue) implements EnvChangeToken<String> {
    @Override public EnvChangeTokenSubType getSubType() { return EnvChangeTokenSubType.LANGUAGE; }
}
