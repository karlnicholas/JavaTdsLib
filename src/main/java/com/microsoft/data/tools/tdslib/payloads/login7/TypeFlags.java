package com.microsoft.data.tools.tdslib.payloads.login7;

/**
 * Type flags.
 * Handles bit-masking for SQL Type, OLE DB, and Access Intent.
 */
public final class TypeFlags {

    // Bit Masks
    private static final int OPTION_SQL_TYPE_BIT_INDEX = 0x08;
    private static final int OPTION_OLE_DB_BIT_INDEX = 0x10;
    private static final int OPTION_ACCESS_INTENT_BIT_INDEX = 0x20;

    // Use int internally to avoid signed byte issues
    private int value;

    // --- Enums ---

    /**
     * SQL interface type.
     */
    public enum OptionSqlType {
        DEFAULT,
        TSQL
    }

    /**
     * OLE DB.
     */
    public enum OptionOleDb {
        OFF,
        ON
    }

    /**
     * Access intent.
     */
    public enum OptionAccessIntent {
        READ_WRITE,
        READ_ONLY
    }

    // --- Constructors ---

    /**
     * Create a new instance with default values.
     */
    public TypeFlags() {
        this.value = 0;
        setSqlType(OptionSqlType.DEFAULT);
        setOleDb(OptionOleDb.OFF);
        setAccessIntent(OptionAccessIntent.READ_WRITE);
    }

    /**
     * Create a new instance from a raw byte value.
     */
    public TypeFlags(byte value) {
        this.value = value & 0xFF;
    }

    // --- Properties ---

    public byte getValue() {
        return (byte) value;
    }

    public byte byteValue() {
        return (byte) value;
    }

    public OptionSqlType getSqlType() {
        if ((value & OPTION_SQL_TYPE_BIT_INDEX) == OPTION_SQL_TYPE_BIT_INDEX) {
            return OptionSqlType.TSQL;
        }
        return OptionSqlType.DEFAULT;
    }

    public void setSqlType(OptionSqlType sqlType) {
        if (sqlType == OptionSqlType.DEFAULT) {
            value &= ~OPTION_SQL_TYPE_BIT_INDEX;
        } else {
            value |= OPTION_SQL_TYPE_BIT_INDEX;
        }
    }

    public OptionOleDb getOleDb() {
        if ((value & OPTION_OLE_DB_BIT_INDEX) == OPTION_OLE_DB_BIT_INDEX) {
            return OptionOleDb.ON;
        }
        return OptionOleDb.OFF;
    }

    public void setOleDb(OptionOleDb oleDb) {
        if (oleDb == OptionOleDb.OFF) {
            value &= ~OPTION_OLE_DB_BIT_INDEX;
        } else {
            value |= OPTION_OLE_DB_BIT_INDEX;
        }
    }

    public OptionAccessIntent getAccessIntent() {
        if ((value & OPTION_ACCESS_INTENT_BIT_INDEX) == OPTION_ACCESS_INTENT_BIT_INDEX) {
            return OptionAccessIntent.READ_ONLY;
        }
        return OptionAccessIntent.READ_WRITE;
    }

    public void setAccessIntent(OptionAccessIntent accessIntent) {
        if (accessIntent == OptionAccessIntent.READ_WRITE) {
            value &= ~OPTION_ACCESS_INTENT_BIT_INDEX;
        } else {
            value |= OPTION_ACCESS_INTENT_BIT_INDEX;
        }
    }

    @Override
    public String toString() {
        String binary = String.format("%8s", Integer.toBinaryString(value)).replace(' ', '0');
        return "TypeFlags[0b" + binary +
                "(SqlType=" + getSqlType() +
                ", OleDb=" + getOleDb() +
                ", AccessIntent=" + getAccessIntent() + ")]";
    }
}