// Copyright (c) Microsoft Corporation.
// Licensed under the MIT License.

package com.microsoft.data.tools.tdslib.payloads.prelogin;

public final class MarsType {
    public static final int Off = 0x00;
    public static final int On = 0x01;

    private MarsType() {}

    public static String getString(int type) {
        switch (type) {
            case Off: return "Off";
            case On: return "On";
            default: return "Unknown";
        }
    }
}
