package com.microsoft.data.tools.tdslib.tokens.envchange;

public record RoutingEnvChangeToken(RoutingInfo oldValue, RoutingInfo newValue) implements EnvChangeToken<RoutingInfo> {
    @Override public EnvChangeTokenSubType getSubType() { return EnvChangeTokenSubType.ROUTING; }
}
