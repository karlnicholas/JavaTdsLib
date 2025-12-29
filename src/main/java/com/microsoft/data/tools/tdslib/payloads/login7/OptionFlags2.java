// Copyright (c) Microsoft Corporation.
// Licensed under the MIT License.

package com.microsoft.data.tools.tdslib.payloads.login7;

public final class OptionFlags2 {
    public enum OptionInitLang { Warn, Fatal }
    public enum OptionOdbc { Off, On }
    public enum OptionUser { Normal, Server, RemUser, SqlRepl }
    public enum OptionIntegratedSecurity { Off, On }

    private static final int OptionInitLangBitIndex = 0x01;
    private static final int OptionOdbcBitIndex = 0x02;
    private static final int OptionUserBitIndexServer = 0x10;
    private static final int OptionUserBitIndexRemUser = 0x20;
    private static final int OptionUserBitIndexSqlRepl = 0x40;
    private static final int OptionIntegratedSecurityBitIndex = 0x80;

    private byte value;

    public OptionFlags2(byte value) { this.value = value; }
    public OptionFlags2() { setInitLang(OptionInitLang.Warn); setODBC(OptionOdbc.Off); setUser(OptionUser.Normal); setIntegratedSecurity(OptionIntegratedSecurity.Off); }

    public byte getValue() { return value; }

    public OptionInitLang getInitLang() { return (value & OptionInitLangBitIndex) == OptionInitLangBitIndex ? OptionInitLang.Fatal : OptionInitLang.Warn; }
    public void setInitLang(OptionInitLang v) { if (v == OptionInitLang.Warn) value &= (byte)(255 - OptionInitLangBitIndex); else value |= OptionInitLangBitIndex; }

    public OptionOdbc getODBC() { return (value & OptionOdbcBitIndex) == OptionOdbcBitIndex ? OptionOdbc.On : OptionOdbc.Off; }
    public void setODBC(OptionOdbc v) { if (v == OptionOdbc.Off) value &= (byte)(255 - OptionOdbcBitIndex); else value |= OptionOdbcBitIndex; }

    public OptionUser getUser() {
        if ((value & OptionUserBitIndexServer) == OptionUserBitIndexServer) return OptionUser.Server;
        if ((value & OptionUserBitIndexRemUser) == OptionUserBitIndexRemUser) return OptionUser.RemUser;
        if ((value & OptionUserBitIndexSqlRepl) == OptionUserBitIndexSqlRepl) return OptionUser.SqlRepl;
        return OptionUser.Normal;
    }
    public void setUser(OptionUser v) {
        if (v == OptionUser.Normal) { value &= (byte)(255 - OptionUserBitIndexServer); value &= (byte)(255 - OptionUserBitIndexRemUser); value &= (byte)(255 - OptionUserBitIndexSqlRepl); }
        else if (v == OptionUser.Server) { value |= OptionUserBitIndexServer; value &= (byte)(255 - OptionUserBitIndexRemUser); value &= (byte)(255 - OptionUserBitIndexSqlRepl); }
        else if (v == OptionUser.RemUser) { value &= (byte)(255 - OptionUserBitIndexServer); value |= OptionUserBitIndexRemUser; value &= (byte)(255 - OptionUserBitIndexSqlRepl); }
        else { value &= (byte)(255 - OptionUserBitIndexServer); value &= (byte)(255 - OptionUserBitIndexRemUser); value |= OptionUserBitIndexSqlRepl; }
    }

    public OptionIntegratedSecurity getIntegratedSecurity() { return (value & OptionIntegratedSecurityBitIndex) == OptionIntegratedSecurityBitIndex ? OptionIntegratedSecurity.On : OptionIntegratedSecurity.Off; }
    public void setIntegratedSecurity(OptionIntegratedSecurity v) { if (v == OptionIntegratedSecurity.Off) value &= (byte)(255 - OptionIntegratedSecurityBitIndex); else value |= OptionIntegratedSecurityBitIndex; }

    public byte toByte() { return value; }

    public static OptionFlags2 fromByte(byte b) { return new OptionFlags2(b); }

    @Override
    public String toString() {
        return String.format("OptionFlags2[0b%s(InitLang=%s, ODBC=%s, User=%s, IntegratedSecurity=%s)]",
                String.format("%8s", Integer.toBinaryString(value & 0xFF)).replace(' ', '0'), getInitLang(), getODBC(), getUser(), getIntegratedSecurity());
    }
}
