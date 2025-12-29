package com.microsoft.data.tools.tdslib.payloads.prelogin;

public final class MarsType {
    public static final byte OFF = 0x00;
    public static final byte ON = 0x01;

    public static String getString(byte type) {
        if (type == OFF) return "Off";
        if (type == ON) return "On";
        return "Unknown";
    }
}