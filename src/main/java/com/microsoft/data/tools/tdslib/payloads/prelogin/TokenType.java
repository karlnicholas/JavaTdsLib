// Copyright (c) Microsoft Corporation.
// Licensed under the MIT License.

package com.microsoft.data.tools.tdslib.payloads.prelogin;

public final class TokenType {
    public static final int Version = 0x00;
    public static final int Encryption = 0x01;
    public static final int InstOpt = 0x02;
    public static final int ThreadId = 0x03;
    public static final int Mars = 0x04;
    public static final int FedAuthRequired = 0x06;
    public static final int Terminator = 0xff;
}
