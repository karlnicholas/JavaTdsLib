package com.microsoft.data.tools.tdslib.tokens.loginack;

public record ProgVersion(byte major, byte minor, int build) {
    public ProgVersion(byte major, byte minor, byte buildHi, byte buildLow) {
        this(major, minor, (Byte.toUnsignedInt(buildHi) << 8) | Byte.toUnsignedInt(buildLow));
    }
}