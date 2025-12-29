// Copyright (c) Microsoft Corporation.
// Licensed under the MIT License.

package com.microsoft.data.tools.tdslib.payloads.login7;

public final class TypeFlags {
    public enum OptionSqlType { Default, TSQL }
    public enum OptionOleDb { Off, On }
    public enum OptionAccessIntent { ReadWrite, ReadOnly }

    private static final int OptionSqlTypeBitIndex = 0x08;
    private static final int OptionOleDbBitIndex = 0x10;
    private static final int OptionAccesIntentBitIndex = 0x20;

    private byte value;

    public TypeFlags(byte value) { this.value = value; }
    public TypeFlags() { setSqlType(OptionSqlType.Default); setOleDb(OptionOleDb.Off); setAccessIntent(OptionAccessIntent.ReadWrite); }

    public byte getValue() { return value; }

    public OptionSqlType getSqlType() { return (value & OptionSqlTypeBitIndex) == OptionSqlTypeBitIndex ? OptionSqlType.TSQL : OptionSqlType.Default; }
    public void setSqlType(OptionSqlType v) { if (v == OptionSqlType.Default) value &= (byte)(255 - OptionSqlTypeBitIndex); else value |= OptionSqlTypeBitIndex; }

    public OptionOleDb getOleDb() { return (value & OptionOleDbBitIndex) == OptionOleDbBitIndex ? OptionOleDb.On : OptionOleDb.Off; }
    public void setOleDb(OptionOleDb v) { if (v == OptionOleDb.Off) value &= (byte)(255 - OptionOleDbBitIndex); else value |= OptionOleDbBitIndex; }

    public OptionAccessIntent getAccessIntent() { return (value & OptionAccesIntentBitIndex) == OptionAccesIntentBitIndex ? OptionAccessIntent.ReadOnly : OptionAccessIntent.ReadWrite; }
    public void setAccessIntent(OptionAccessIntent v) { if (v == OptionAccessIntent.ReadWrite) value &= (byte)(255 - OptionAccesIntentBitIndex); else value |= OptionAccesIntentBitIndex; }

    public byte toByte() { return value; }

    public static TypeFlags fromByte(byte b) { return new TypeFlags(b); }

    @Override
    public String toString() {
        return String.format("TypeFlags[0b%s(SqlType=%s, OleDb=%s, AccessIntent=%s)]",
                String.format("%8s", Integer.toBinaryString(value & 0xFF)).replace(' ', '0'), getSqlType(), getOleDb(), getAccessIntent());
    }
}
