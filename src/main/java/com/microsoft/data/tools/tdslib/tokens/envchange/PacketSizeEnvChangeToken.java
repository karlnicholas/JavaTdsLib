package com.microsoft.data.tools.tdslib.tokens.envchange;

public record PacketSizeEnvChangeToken(Integer oldValue, Integer newValue) implements EnvChangeToken<Integer> {
    @Override public EnvChangeTokenSubType getSubType() { return EnvChangeTokenSubType.PACKET_SIZE; }
}
