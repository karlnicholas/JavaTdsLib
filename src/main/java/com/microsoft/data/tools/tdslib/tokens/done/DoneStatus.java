package com.microsoft.data.tools.tdslib.tokens.done;

/**
 * Done status for Done, DoneProc, DoneInProc.
 * Defined as int constants to support bitwise flags.
 */
public final class DoneStatus {

    private DoneStatus() {}

    /**
     * Final.
     */
    public static final int FINAL = 0x0000;

    /**
     * More.
     */
    public static final int MORE = 0x0001;

    /**
     * Error.
     */
    public static final int ERROR = 0x0002;

    /**
     * In Transaction.
     */
    public static final int IN_XACT = 0x0004;

    /**
     * Count.
     */
    public static final int COUNT = 0x0010;

    /**
     * Attention.
     */
    public static final int ATTN = 0x0020;

    /**
     * Server Error.
     */
    public static final int SERVER_ERROR = 0x0100;
}