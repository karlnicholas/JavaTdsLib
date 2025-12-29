package com.microsoft.data.tools.tdslib.payloads.login7;

/**
 * Option flags 1.
 * Handles bit-masking for connection settings.
 */
public final class OptionFlags1 {

    // Bit Masks
    private static final int OPTION_ENDIAN_BIT_INDEX = 0x01;
    private static final int OPTION_CHARSET_BIT_INDEX = 0x02;
    private static final int OPTION_FLOAT_BIT_INDEX_VAX = 0x04;
    private static final int OPTION_FLOAT_BIT_INDEX_ND5000 = 0x08;
    private static final int OPTION_BCP_DUMPLOAD_BIT_INDEX = 0x10;
    private static final int OPTION_USE_DB_BIT_INDEX = 0x20;
    private static final int OPTION_INIT_DB_BIT_INDEX = 0x40;
    private static final int OPTION_LANG_WARN_BIT_INDEX = 0x80;

    // We use int internally to avoid Java signed byte issues during bitwise math
    private int value;

    // --- Enums ---

    public enum OptionEndian {
        LITTLE_ENDIAN,
        BIG_ENDIAN
    }

    public enum OptionCharset {
        ASCII,
        EBCDIC
    }

    public enum OptionFloat {
        IEEE,
        VAX,
        ND5000
    }

    public enum OptionBcpDumpload {
        ON,
        OFF
    }

    public enum OptionUseDb {
        ON,
        OFF
    }

    public enum OptionInitDb {
        WARN,
        FATAL
    }

    public enum OptionLangWarn {
        ON,
        OFF
    }

    // --- Constructors ---

    /**
     * Create a new instance of this class with a default value.
     */
    public OptionFlags1() {
        this.value = 0;
        setEndian(OptionEndian.LITTLE_ENDIAN);
        setCharset(OptionCharset.ASCII);
        setFloat(OptionFloat.IEEE);
        setBcpDumpload(OptionBcpDumpload.OFF);
        setUseDb(OptionUseDb.OFF);
        setLangWarn(OptionLangWarn.ON);
        setInitDb(OptionInitDb.WARN);
    }

    /**
     * Create a new instance of this class from a raw value.
     *
     * @param value Raw byte value.
     */
    public OptionFlags1(byte value) {
        this.value = value & 0xFF; // Convert to unsigned int
    }

    // --- Properties ---

    public byte getValue() {
        return (byte) value;
    }

    // Emulate C# explicit operator byte(OptionFlags1)
    public byte byteValue() {
        return (byte) value;
    }

    public OptionEndian getEndian() {
        if ((value & OPTION_ENDIAN_BIT_INDEX) == OPTION_ENDIAN_BIT_INDEX) {
            return OptionEndian.BIG_ENDIAN;
        }
        return OptionEndian.LITTLE_ENDIAN;
    }

    public void setEndian(OptionEndian endian) {
        if (endian == OptionEndian.LITTLE_ENDIAN) {
            value &= ~OPTION_ENDIAN_BIT_INDEX;
        } else {
            value |= OPTION_ENDIAN_BIT_INDEX;
        }
    }

    public OptionCharset getCharset() {
        if ((value & OPTION_CHARSET_BIT_INDEX) == OPTION_CHARSET_BIT_INDEX) {
            return OptionCharset.EBCDIC;
        }
        return OptionCharset.ASCII;
    }

    public void setCharset(OptionCharset charset) {
        if (charset == OptionCharset.ASCII) {
            value &= ~OPTION_CHARSET_BIT_INDEX;
        } else {
            value |= OPTION_CHARSET_BIT_INDEX;
        }
    }

    public OptionFloat getFloat() {
        if ((value & OPTION_FLOAT_BIT_INDEX_VAX) == OPTION_FLOAT_BIT_INDEX_VAX) {
            return OptionFloat.VAX;
        } else if ((value & OPTION_FLOAT_BIT_INDEX_ND5000) == OPTION_FLOAT_BIT_INDEX_ND5000) {
            return OptionFloat.ND5000;
        }
        return OptionFloat.IEEE;
    }

    public void setFloat(OptionFloat floatType) {
        // Clear both bits first
        value &= ~OPTION_FLOAT_BIT_INDEX_VAX;
        value &= ~OPTION_FLOAT_BIT_INDEX_ND5000;

        if (floatType == OptionFloat.VAX) {
            value |= OPTION_FLOAT_BIT_INDEX_VAX;
        } else if (floatType == OptionFloat.ND5000) {
            value |= OPTION_FLOAT_BIT_INDEX_ND5000;
        }
        // IEEE is default (00), so we do nothing else
    }

    public OptionBcpDumpload getBcpDumpload() {
        if ((value & OPTION_BCP_DUMPLOAD_BIT_INDEX) == OPTION_BCP_DUMPLOAD_BIT_INDEX) {
            return OptionBcpDumpload.OFF;
        }
        return OptionBcpDumpload.ON;
    }

    public void setBcpDumpload(OptionBcpDumpload bcp) {
        if (bcp == OptionBcpDumpload.ON) {
            value &= ~OPTION_BCP_DUMPLOAD_BIT_INDEX;
        } else {
            value |= OPTION_BCP_DUMPLOAD_BIT_INDEX;
        }
    }

    public OptionUseDb getUseDb() {
        if ((value & OPTION_USE_DB_BIT_INDEX) == OPTION_USE_DB_BIT_INDEX) {
            return OptionUseDb.OFF;
        }
        return OptionUseDb.ON;
    }

    public void setUseDb(OptionUseDb useDb) {
        if (useDb == OptionUseDb.ON) {
            value &= ~OPTION_USE_DB_BIT_INDEX;
        } else {
            value |= OPTION_USE_DB_BIT_INDEX;
        }
    }

    public OptionInitDb getInitDb() {
        if ((value & OPTION_INIT_DB_BIT_INDEX) == OPTION_INIT_DB_BIT_INDEX) {
            return OptionInitDb.FATAL;
        }
        return OptionInitDb.WARN;
    }

    public void setInitDb(OptionInitDb initDb) {
        if (initDb == OptionInitDb.WARN) {
            value &= ~OPTION_INIT_DB_BIT_INDEX;
        } else {
            value |= OPTION_INIT_DB_BIT_INDEX;
        }
    }

    public OptionLangWarn getLangWarn() {
        if ((value & OPTION_LANG_WARN_BIT_INDEX) == OPTION_LANG_WARN_BIT_INDEX) {
            return OptionLangWarn.ON;
        }
        return OptionLangWarn.OFF;
    }

    public void setLangWarn(OptionLangWarn langWarn) {
        if (langWarn == OptionLangWarn.OFF) {
            value &= ~OPTION_LANG_WARN_BIT_INDEX;
        } else {
            value |= OPTION_LANG_WARN_BIT_INDEX;
        }
    }

    @Override
    public String toString() {
        // Java toBinaryString doesn't pad, so we add leading zeros manually if needed
        String binary = String.format("%8s", Integer.toBinaryString(value)).replace(' ', '0');

        return "OptionFlags1[0b" + binary +
                "(Endian=" + getEndian() +
                ", Charset=" + getCharset() +
                ", Float=" + getFloat() +
                ", BcpDumpload=" + getBcpDumpload() +
                ", UseDb=" + getUseDb() +
                ", InitDb=" + getInitDb() +
                ", LangWarn=" + getLangWarn() + ")]";
    }
}