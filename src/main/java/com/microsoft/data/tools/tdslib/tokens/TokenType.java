package com.microsoft.data.tools.tdslib.tokens;

/**
 * Token type.
 */
public enum TokenType {

    /**
     * Column data format.
     */
    ALT_METADATA(0x88),

    /**
     * Row of data.
     */
    ALT_ROW(0xD3),

    /**
     * Column metadata.
     */
    COL_METADATA(0x81),

    /**
     * Column information in browse mode.
     */
    COL_INFO(0xA5),

    /**
     * Done.
     */
    DONE(0xFD),

    /**
     * Procedure done.
     */
    DONE_PROC(0xFE),

    /**
     * Done in procedure.
     */
    DONE_IN_PROC(0xFF),

    /**
     * Environment change.
     */
    ENV_CHANGE(0xE3),

    /**
     * Error.
     */
    ERROR(0xAA),

    /**
     * Feature extesion acknowledgment.
     */
    FEATURE_EXT_ACK(0xAE),

    /**
     * Federated authentication information.
     */
    FED_AUTH_INFO(0xEE),

    /**
     * Info.
     */
    INFO(0xAB),

    /**
     * Login acknowledgment.
     */
    LOGIN_ACK(0xAD),

    /**
     * Row with Null Bitmap Compression.
     */
    NBC_ROW(0xD2),

    /**
     * Offset.
     */
    OFFSET(0x78),

    /**
     * Order.
     */
    ORDER(0xA9),

    /**
     * Return status.
     */
    RETURN_STATUS(0x79),

    /**
     * Return value.
     */
    RETURN_VALUE(0xAC),

    /**
     * Complete Row.
     */
    ROW(0xD1),

    /**
     * SSPI.
     */
    SSPI(0xED),

    /**
     * Table name.
     */
    TAB_NAME(0xA4);

    private final int value;

    TokenType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static TokenType fromValue(int value) {
        for (TokenType t : values()) {
            if (t.value == value) {
                return t;
            }
        }
        throw new IllegalArgumentException("Unknown TokenType: " + Integer.toHexString(value));
    }
}