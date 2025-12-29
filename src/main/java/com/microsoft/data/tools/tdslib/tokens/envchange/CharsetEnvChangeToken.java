package com.microsoft.data.tools.tdslib.tokens.envchange;

public final class CharsetEnvChangeToken extends EnvChangeToken<String> {
    public CharsetEnvChangeToken(String oldValue, String newValue) { super(oldValue, newValue); }
    @Override public EnvChangeTokenSubType getSubType() { return EnvChangeTokenSubType.CHARACTER_SET; }
}
