// Copyright (c) Microsoft Corporation.
// Licensed under the MIT License.

package com.microsoft.data.tools.tdslib.payloads.login7;

public final class OptionFlags1 {
    public enum OptionEndian { LittleEndian, BigEndian }
    public enum OptionCharset { Ascii, Ebcdic }
    public enum OptionFloat { IEEE, VAX, ND5000 }
    public enum OptionBcpDumpload { On, Off }
    public enum OptionUseDb { On, Off }
    public enum OptionInitDb { Warn, Fatal }
    public enum OptionLangWarn { On, Off }

    private static final int OptionEndianBitIndex = 0x01;
    private static final int OptionCharsetBitIndex = 0x02;
    private static final int OptionFloatBitIndexVax = 0x04;
    private static final int OptionFloatBitIndexND5000 = 0x08;
    private static final int OptionBcpDumploadBitIndex = 0x10;
    private static final int OptionUseDbBitIndex = 0x20;
    private static final int OptionIndexDbBitIndex = 0x40;
    private static final int OptionLangWarnBitIndex = 0x80;

    private byte value;

    public OptionFlags1(byte value) { this.value = value; }
    public OptionFlags1() {
        setEndian(OptionEndian.LittleEndian);
        setCharset(OptionCharset.Ascii);
        setFloat(OptionFloat.IEEE);
        setBcpDumpload(OptionBcpDumpload.Off);
        setUseDb(OptionUseDb.Off);
        setLangWarn(OptionLangWarn.On);
        setInitDb(OptionInitDb.Warn);
    }

    public byte getValue() { return value; }

    public OptionEndian getEndian() { return (value & OptionEndianBitIndex) == OptionEndianBitIndex ? OptionEndian.BigEndian : OptionEndian.LittleEndian; }
    public void setEndian(OptionEndian v) { if (v == OptionEndian.LittleEndian) value &= (byte)(255 - OptionEndianBitIndex); else value |= OptionEndianBitIndex; }

    public OptionCharset getCharset() { return (value & OptionCharsetBitIndex) == OptionCharsetBitIndex ? OptionCharset.Ebcdic : OptionCharset.Ascii; }
    public void setCharset(OptionCharset v) { if (v == OptionCharset.Ascii) value &= (byte)(255 - OptionCharsetBitIndex); else value |= OptionCharsetBitIndex; }

    public OptionFloat getFloat() {
        if ((value & OptionFloatBitIndexVax) == OptionFloatBitIndexVax) return OptionFloat.VAX;
        if ((value & OptionFloatBitIndexND5000) == OptionFloatBitIndexND5000) return OptionFloat.ND5000;
        return OptionFloat.IEEE;
    }
    public void setFloat(OptionFloat v) {
        if (v == OptionFloat.IEEE) { value &= (byte)(255 - OptionFloatBitIndexVax); value &= (byte)(255 - OptionFloatBitIndexND5000); }
        else if (v == OptionFloat.VAX) { value |= OptionFloatBitIndexVax; value &= (byte)(255 - OptionFloatBitIndexND5000); }
        else { value &= (byte)(255 - OptionFloatBitIndexVax); value |= OptionFloatBitIndexND5000; }
    }

    public OptionBcpDumpload getBcpDumpload() { return (value & OptionBcpDumploadBitIndex) == OptionBcpDumploadBitIndex ? OptionBcpDumpload.Off : OptionBcpDumpload.On; }
    public void setBcpDumpload(OptionBcpDumpload v) { if (v == OptionBcpDumpload.On) value &= (byte)(255 - OptionBcpDumploadBitIndex); else value |= OptionBcpDumploadBitIndex; }

    public OptionUseDb getUseDb() { return (value & OptionUseDbBitIndex) == OptionUseDbBitIndex ? OptionUseDb.Off : OptionUseDb.On; }
    public void setUseDb(OptionUseDb v) { if (v == OptionUseDb.On) value &= (byte)(255 - OptionUseDbBitIndex); else value |= OptionUseDbBitIndex; }

    public OptionInitDb getInitDb() { return (value & OptionIndexDbBitIndex) == OptionIndexDbBitIndex ? OptionInitDb.Fatal : OptionInitDb.Warn; }
    public void setInitDb(OptionInitDb v) { if (v == OptionInitDb.Warn) value &= (byte)(255 - OptionIndexDbBitIndex); else value |= OptionIndexDbBitIndex; }

    public OptionLangWarn getLangWarn() { return (value & OptionLangWarnBitIndex) == OptionLangWarnBitIndex ? OptionLangWarn.On : OptionLangWarn.Off; }
    public void setLangWarn(OptionLangWarn v) { if (v == OptionLangWarn.Off) value &= (byte)(255 - OptionLangWarnBitIndex); else value |= OptionLangWarnBitIndex; }

    public byte toByte() { return value; }

    public static OptionFlags1 fromByte(byte b) { return new OptionFlags1(b); }

    @Override
    public String toString() {
        return String.format("OptionFlags1[0b%s(Endian=%s, Charset=%s, Float=%s, BcpDumpload=%s, UseDb=%s, InitDb=%s, LangWarn=%s)]",
                String.format("%8s", Integer.toBinaryString(value & 0xFF)).replace(' ', '0'), getEndian(), getCharset(), getFloat(), getBcpDumpload(), getUseDb(), getInitDb(), getLangWarn());
    }
}
