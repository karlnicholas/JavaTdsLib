package com.microsoft.data.tools.tdslib.tokens.envchange;

public final class PacketSizeEnvChangeToken extends EnvChangeToken<Integer> {
    public PacketSizeEnvChangeToken(int oldValue, int newValue) { super(oldValue, newValue); }
    @Override public EnvChangeTokenSubType getSubType() { return EnvChangeTokenSubType.PACKET_SIZE; }
}