// Copyright (c) Microsoft Corporation.
// Licensed under the MIT License.

package com.microsoft.data.tools.tdslib;

public enum TdsVersion {
    V7_1(0x71000001L),
    V7_2(0x72090002L),
    V7_3_A(0x730A0003L),
    V7_3_B(0x730B0003L),
    V7_4(0x74000004L);

    private final long value;
    TdsVersion(long value) { this.value = value; }
    public long value() { return value; }
}
