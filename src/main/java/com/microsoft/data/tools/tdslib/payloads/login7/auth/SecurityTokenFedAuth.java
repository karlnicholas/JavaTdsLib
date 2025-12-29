// Copyright (c) Microsoft Corporation.
// Licensed under the MIT License.

package com.microsoft.data.tools.tdslib.payloads.login7.auth;

import com.microsoft.data.tools.tdslib.buffer.ByteBufferUtils;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public final class SecurityTokenFedAuth extends FedAuth {
    private final String token;
    private final boolean echo;

    public SecurityTokenFedAuth(String token, boolean echo) {
        if (token == null || token.isEmpty()) throw new IllegalArgumentException("token");
        this.token = token;
        this.echo = echo;
    }

    @Override
    public ByteBuffer getBuffer() {
        byte[] tokenBytes;
        try { tokenBytes = token.getBytes("UTF-16LE"); } catch (Exception e) { tokenBytes = token.getBytes(StandardCharsets.UTF_16); }
        ByteBuffer tokenBuffer = ByteBuffer.wrap(tokenBytes);
        ByteBuffer buffer = ByteBuffer.allocate(10);

        int offset = 0;
        offset = ByteBufferUtils.writeUInt8(buffer, FeatureId, offset);
        offset = ByteBufferUtils.writeUInt32LE(buffer, ByteBufferUtils.length(tokenBuffer) + 4 + 1, offset);
        byte options = (byte)(LibrarySecurityToken | (echo ? FedAuthEchoYes : FedAuthEchoNo));
        offset = ByteBufferUtils.writeUInt8(buffer, options, offset);
        ByteBufferUtils.writeInt32LE(buffer, ByteBufferUtils.length(tokenBuffer), offset);

        List<ByteBuffer> parts = new ArrayList<>();
        parts.add(buffer);
        parts.add(tokenBuffer);
        return ByteBufferUtils.concat(parts);
    }
}
