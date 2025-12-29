package com.microsoft.data.tools.tdslib;

/**
 * TDS Protocol versions.
 */
public enum TdsVersion {

    /**
     * Version 7.1
     */
    V7_1(0x71000001L),

    /**
     * Version 7.2
     */
    V7_2(0x72090002L),

    /**
     * Version 7.3.A
     */
    V7_3_A(0x730A0003L),

    /**
     * Version 7.3.B
     */
    V7_3_B(0x730B0003L),

    /**
     * Version 7.4
     */
    V7_4(0x74000004L);

    private final long versionValue;

    TdsVersion(long versionValue) {
        this.versionValue = versionValue;
    }

    /**
     * Gets the unsigned integer value of the version.
     * @return The TDS version value.
     */
    public long getVersionValue() {
        return versionValue;
    }

    /**
     * Helper to find a version from a long value.
     */
    public static TdsVersion fromValue(long value) {
        for (TdsVersion v : values()) {
            if (v.versionValue == value) {
                return v;
            }
        }
        throw new IllegalArgumentException("Unknown TDS Version: " + value);
    }
}