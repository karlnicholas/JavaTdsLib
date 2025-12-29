package com.microsoft.data.tools.tdslib.tokens.fedauthinfo;

/**
 * Federate authentication information identifier.
 */
public enum FedAuthInfoId {
    /**
     * Identifier for STSUrl.
     */
    STS_URL((byte) 0x01),

    /**
     * Identifier for SPN.
     */
    SPN((byte) 0x02);

    private final byte value;

    FedAuthInfoId(byte value) {
        this.value = value;
    }

    public byte getValue() {
        return value;
    }

    public static FedAuthInfoId fromValue(byte value) {
        for (FedAuthInfoId id : values()) {
            if (id.value == value) {
                return id;
            }
        }
        return null;
    }
}