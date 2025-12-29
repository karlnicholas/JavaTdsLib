// Copyright (c) Microsoft Corporation.
// Licensed under the MIT License.

package com.microsoft.data.tools.tdslib.payloads.login7.auth;

import com.microsoft.data.tools.tdslib.buffer.ByteBufferUtils;

import java.nio.ByteBuffer;

public final class ADALFedAuth extends FedAuth {

    private final boolean echo;
    private final ADALWorkflow workflow;

    public ADALFedAuth(ADALWorkflow workflow, boolean echo) {
        this.workflow = workflow;
        this.echo = echo;
    }

    @Override
    public ByteBuffer getBuffer() {
        ByteBuffer buffer = ByteBuffer.allocate(7);
        int offset = ByteBufferUtils.writeUInt8(buffer, FeatureId);
        offset = ByteBufferUtils.writeUInt32LE(buffer, 2, offset);
        byte options = (byte)(LibraryADAL | (echo ? FedAuthEchoYes : FedAuthEchoNo));
        offset = ByteBufferUtils.writeUInt8(buffer, options, offset);
        ByteBufferUtils.writeUInt8(buffer, workflow.value(), offset);
        return buffer;
    }
}
