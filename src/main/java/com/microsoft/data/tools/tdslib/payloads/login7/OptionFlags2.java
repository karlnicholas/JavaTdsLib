package com.microsoft.data.tools.tdslib.payloads.login7;

/**
 * Option flags 2.
 * Handles bit-masking for initialization and security settings.
 */
public final class OptionFlags2 {

    // Bit Masks
    private static final int OPTION_INIT_LANG_BIT_INDEX = 0x01;
    private static final int OPTION_ODBC_BIT_INDEX = 0x02;
    // Bits 0x04 and 0x08 are reserved/unused in this spec
    private static final int OPTION_USER_BIT_INDEX_SERVER = 0x10;
    private static final int OPTION_USER_BIT_INDEX_REM_USER = 0x20;
    private static final int OPTION_USER_BIT_INDEX_SQL_REPL = 0x40;
    private static final int OPTION_INTEGRATED_SECURITY_BIT_INDEX = 0x80;

    // Use int internally to avoid signed byte issues
    private int value;

    // --- Enums ---

    public enum OptionInitLang {
        WARN,
        FATAL
    }

    public enum OptionOdbc {
        OFF,
        ON
    }

    public enum OptionUser {
        NORMAL,
        SERVER,
        REM_USER,
        SQL_REPL
    }

    public enum OptionIntegratedSecurity {
        OFF,
        ON
    }

    // --- Constructors ---

    /**
     * Create a new instance with default values.
     */
    public OptionFlags2() {
        this.value = 0;
        setInitLang(OptionInitLang.WARN);
        setOdbc(OptionOdbc.OFF);
        setUser(OptionUser.NORMAL);
        setIntegratedSecurity(OptionIntegratedSecurity.OFF);
    }

    /**
     * Create a new instance from a raw byte value.
     */
    public OptionFlags2(byte value) {
        this.value = value & 0xFF;
    }

    // --- Properties ---

    public byte getValue() {
        return (byte) value;
    }

    public byte byteValue() {
        return (byte) value;
    }

    public OptionInitLang getInitLang() {
        if ((value & OPTION_INIT_LANG_BIT_INDEX) == OPTION_INIT_LANG_BIT_INDEX) {
            return OptionInitLang.FATAL;
        }
        return OptionInitLang.WARN;
    }

    public void setInitLang(OptionInitLang initLang) {
        if (initLang == OptionInitLang.WARN) {
            value &= ~OPTION_INIT_LANG_BIT_INDEX;
        } else {
            value |= OPTION_INIT_LANG_BIT_INDEX;
        }
    }

    public OptionOdbc getOdbc() {
        if ((value & OPTION_ODBC_BIT_INDEX) == OPTION_ODBC_BIT_INDEX) {
            return OptionOdbc.ON;
        }
        return OptionOdbc.OFF;
    }

    public void setOdbc(OptionOdbc odbc) {
        // FIXED: The C# source had a bug here using OptionInitLangBitIndex
        if (odbc == OptionOdbc.OFF) {
            value &= ~OPTION_ODBC_BIT_INDEX;
        } else {
            value |= OPTION_ODBC_BIT_INDEX;
        }
    }

    public OptionUser getUser() {
        if ((value & OPTION_USER_BIT_INDEX_SERVER) == OPTION_USER_BIT_INDEX_SERVER) {
            return OptionUser.SERVER;
        }
        if ((value & OPTION_USER_BIT_INDEX_REM_USER) == OPTION_USER_BIT_INDEX_REM_USER) {
            return OptionUser.REM_USER;
        }
        if ((value & OPTION_USER_BIT_INDEX_SQL_REPL) == OPTION_USER_BIT_INDEX_SQL_REPL) {
            return OptionUser.SQL_REPL;
        }
        return OptionUser.NORMAL;
    }

    public void setUser(OptionUser user) {
        // Clear all User bits first
        value &= ~OPTION_USER_BIT_INDEX_SERVER;
        value &= ~OPTION_USER_BIT_INDEX_REM_USER;
        value &= ~OPTION_USER_BIT_INDEX_SQL_REPL;

        switch (user) {
            case SERVER:
                value |= OPTION_USER_BIT_INDEX_SERVER;
                break;
            case REM_USER:
                value |= OPTION_USER_BIT_INDEX_REM_USER;
                break;
            case SQL_REPL:
                value |= OPTION_USER_BIT_INDEX_SQL_REPL;
                break;
            case NORMAL:
            default:
                // Already cleared
                break;
        }
    }

    public OptionIntegratedSecurity getIntegratedSecurity() {
        if ((value & OPTION_INTEGRATED_SECURITY_BIT_INDEX) == OPTION_INTEGRATED_SECURITY_BIT_INDEX) {
            return OptionIntegratedSecurity.ON;
        }
        return OptionIntegratedSecurity.OFF;
    }

    public void setIntegratedSecurity(OptionIntegratedSecurity integratedSecurity) {
        if (integratedSecurity == OptionIntegratedSecurity.OFF) {
            value &= ~OPTION_INTEGRATED_SECURITY_BIT_INDEX;
        } else {
            value |= OPTION_INTEGRATED_SECURITY_BIT_INDEX;
        }
    }

    @Override
    public String toString() {
        String binary = String.format("%8s", Integer.toBinaryString(value)).replace(' ', '0');
        return "OptionFlags2[0b" + binary +
                "(InitLang=" + getInitLang() +
                ", ODBC=" + getOdbc() +
                ", User=" + getUser() +
                ", IntegratedSecurity=" + getIntegratedSecurity() + ")]";
    }
}