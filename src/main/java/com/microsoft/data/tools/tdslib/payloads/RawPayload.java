// Copyright (c) Microsoft Corporation.
// Licensed under the MIT License.

package com.microsoft.data.tools.tdslib.payloads;

import java.nio.ByteBuffer;

public class RawPayload extends Payload {

    public RawPayload(ByteBuffer byteBuffer) {
        this.buffer = byteBuffer; 
    }

    @Override
    protected void buildBufferInternal() {
        // no-op
    }

    @Override
    public String toString() {
        return "RawPayload[Buffer=" + buffer + "]";
    }

}
