package com.microsoft.data.tools.tdslib.payloads.prelogin;

public enum EncryptionType {
    OFF((byte) 0x00),
    ON((byte) 0x01),
    NOT_SUPPORTED((byte) 0x02),
    REQUIRED((byte) 0x03);

    private final byte value;

    EncryptionType(byte value) {
        this.value = value;
    }

    public byte getValue() {
        return value;
    }

    public static EncryptionType fromValue(byte value) {
        for (EncryptionType type : values()) {
            if (type.value == value) return type;
        }
        return OFF;
    }
}