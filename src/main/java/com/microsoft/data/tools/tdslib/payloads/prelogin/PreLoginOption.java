// Copyright (c) Microsoft Corporation.
// Licensed under the MIT License.

package com.microsoft.data.tools.tdslib.payloads.prelogin;

import java.nio.ByteBuffer;

public class PreLoginOption {
    private int tokenType;
    private ByteBuffer byteBuffer; 

    public int getTokenType() { return tokenType; }
    public void setTokenType(int tokenType) { this.tokenType = tokenType; }

    public ByteBuffer getByteBuffer() { return byteBuffer; }
    public void setByteBuffer(ByteBuffer byteBuffer) { this.byteBuffer = byteBuffer; }
}
