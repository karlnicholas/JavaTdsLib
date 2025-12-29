package com.microsoft.data.tools.tdslib.tokens.loginack;

/**
 * Program version.
 */
public final class ProgVersion {

    private final byte major;
    private final byte minor;
    private final int build; // ushort in C# -> int in Java

    /**
     * Create a new instance.
     *
     * @param major Major.
     * @param minor Minor.
     * @param build Build revision.
     */
    public ProgVersion(byte major, byte minor, int build) {
        this.major = major;
        this.minor = minor;
        this.build = build;
    }

    /**
     * Create a new instance from high/low bytes.
     *
     * @param major    Major.
     * @param minor    Minor.
     * @param buildHi  Build high byte.
     * @param buildLow Build low byte.
     */
    public ProgVersion(byte major, byte minor, byte buildHi, byte buildLow) {
        this(major, minor, (Byte.toUnsignedInt(buildHi) << 8) | Byte.toUnsignedInt(buildLow));
    }

    public byte getMajor() {
        return major;
    }

    public byte getMinor() {
        return minor;
    }

    public int getBuild() {
        return build;
    }

    @Override
    public String toString() {
        return String.format("ProgVersion[Major=%d, Minor=%d, Build=%d]", major, minor, build);
    }
}