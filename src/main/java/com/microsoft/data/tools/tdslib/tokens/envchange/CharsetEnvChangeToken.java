package com.microsoft.data.tools.tdslib.tokens.envchange;

public record CharsetEnvChangeToken(String oldValue, String newValue) implements EnvChangeToken<String> {
    @Override public EnvChangeTokenSubType getSubType() { return EnvChangeTokenSubType.CHARACTER_SET; }
}
