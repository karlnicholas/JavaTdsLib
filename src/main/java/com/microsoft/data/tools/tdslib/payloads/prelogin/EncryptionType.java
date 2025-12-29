// Copyright (c) Microsoft Corporation.
// Licensed under the MIT License.

package com.microsoft.data.tools.tdslib.payloads.prelogin;

public enum EncryptionType {
    Off(0x00),
    On(0x01),
    NotSupported(0x02),
    Required(0x03);

    private final int value;
    EncryptionType(int value) { this.value = value; }
    public int value() { return value; }
    public static EncryptionType fromByte(int b) {
        for (EncryptionType e : values()) if (e.value == b) return e;
        throw new IllegalArgumentException(String.format("Invalid encryption type: 0x%02X", b));
    }
}
