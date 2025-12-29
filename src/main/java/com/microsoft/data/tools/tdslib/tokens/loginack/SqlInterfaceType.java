package com.microsoft.data.tools.tdslib.tokens.loginack;

/**
 * SQL interface type.
 */
public enum SqlInterfaceType {
    /**
     * Default.
     */
    DEFAULT((byte) 0),

    /**
     * T-SQL.
     */
    TSQL((byte) 1);

    private final byte value;

    SqlInterfaceType(byte value) {
        this.value = value;
    }

    public byte getValue() {
        return value;
    }

    public static SqlInterfaceType fromValue(byte value) {
        for (SqlInterfaceType type : values()) {
            if (type.value == value) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown Sql Interface type: " + value);
    }
}