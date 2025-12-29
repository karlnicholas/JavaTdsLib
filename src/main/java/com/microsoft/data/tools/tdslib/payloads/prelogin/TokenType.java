package com.microsoft.data.tools.tdslib.payloads.prelogin;

public final class TokenType {
    public static final byte VERSION = 0x00;
    public static final byte ENCRYPTION = 0x01;
    public static final byte INST_OPT = 0x02;
    public static final byte THREAD_ID = 0x03;
    public static final byte MARS = 0x04;
    public static final byte FED_AUTH_REQUIRED = 0x06;
    public static final byte TERMINATOR = (byte) 0xFF;
}