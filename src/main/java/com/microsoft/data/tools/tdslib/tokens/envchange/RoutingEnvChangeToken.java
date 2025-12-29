package com.microsoft.data.tools.tdslib.tokens.envchange;

public final class RoutingEnvChangeToken extends EnvChangeToken<RoutingInfo> {
    public RoutingEnvChangeToken(RoutingInfo oldValue, RoutingInfo newValue) { super(oldValue, newValue); }
    @Override public EnvChangeTokenSubType getSubType() { return EnvChangeTokenSubType.ROUTING; }
}