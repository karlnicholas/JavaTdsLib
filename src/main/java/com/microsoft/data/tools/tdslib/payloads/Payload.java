// Copyright (c) Microsoft Corporation.
// Licensed under the MIT License.

package com.microsoft.data.tools.tdslib.payloads;

import java.nio.ByteBuffer;

public abstract class Payload {

    protected ByteBuffer buffer; 

    protected abstract void buildBufferInternal();

    public ByteBuffer buildBuffer() {
        buildBufferInternal();
        return buffer;
    }

    public ByteBuffer getBuffer() { return buffer; }

}
