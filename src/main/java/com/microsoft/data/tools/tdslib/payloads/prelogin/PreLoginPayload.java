// Copyright (c) Microsoft Corporation.
// Licensed under the MIT License.

package com.microsoft.data.tools.tdslib.payloads.prelogin;

import com.microsoft.data.tools.tdslib.buffer.ByteBufferUtils;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class PreLoginPayload extends com.microsoft.data.tools.tdslib.payloads.Payload {

    private static final int TokenHeaderSize = 5; 

    private final boolean encrypt;

    private int encryption; // store raw value
    private SqlVersion version;
    private int instance;
    private long threadId;
    private int mars;
    private int fedAuth;

    public PreLoginPayload() { this(false); }

    public PreLoginPayload(boolean encrypt) {
        this.encrypt = encrypt;
        buildBufferInternal();
        extractBufferData();
    }

    public PreLoginPayload(ByteBuffer buffer) {
        this.encrypt = false;
        this.buffer = buffer;
        extractBufferData();
    }

    @Override
    protected void buildBufferInternal() {
        List<PreLoginOption> optionList = new ArrayList<>();
        optionList.add(getVersionOption());
        optionList.add(getEncryptionOption());
        optionList.add(getInstanceOption());
        optionList.add(getThreadIdOption());
        optionList.add(getMarsOption());
        optionList.add(getFedAuthOption());

        int dataLength = optionList.stream().mapToInt(b -> TokenHeaderSize + ByteBufferUtils.length(b.getByteBuffer())).sum() + 1;
        buffer = ByteBuffer.allocate(dataLength);
        int offsetOption = 0;
        int offsetData = TokenHeaderSize * optionList.size() + 1;

        for (PreLoginOption option : optionList) {
            ByteBufferUtils.writeUInt8(buffer, option.getTokenType(), offsetOption);
            ByteBufferUtils.writeUInt16BE(buffer, offsetData, offsetOption + 1);
            ByteBufferUtils.writeUInt16BE(buffer, ByteBufferUtils.length(option.getByteBuffer()), offsetOption + 3);
            ByteBufferUtils.write(buffer, option.getByteBuffer(), offsetData);

            offsetOption += TokenHeaderSize;
            offsetData += ByteBufferUtils.length(option.getByteBuffer());
        }

        ByteBufferUtils.writeUInt8(buffer, TokenType.Terminator, offsetOption); 
    }

    private PreLoginOption getVersionOption() {
        ByteBuffer bb = ByteBuffer.allocate(6);
        int offset = 0;
        SqlVersion v = getLibraryVersion();
        offset = ByteBufferUtils.writeUInt8(bb, v.getMajor(), offset);
        offset = ByteBufferUtils.writeUInt8(bb, v.getMinor(), offset);
        offset = ByteBufferUtils.writeUInt8(bb, (v.getPatch() & 0xFF00) >> 8, offset); // match original behavior
        offset = ByteBufferUtils.writeUInt8(bb, v.getPatch() & 0xFF, offset);
        offset = ByteBufferUtils.writeUInt8(bb, v.getTrivial(), offset);
        ByteBufferUtils.writeUInt16BE(bb, v.getSubBuild(), offset);
        PreLoginOption p = new PreLoginOption();
        p.setTokenType(TokenType.Version);
        p.setByteBuffer(bb);
        return p;
    } 

    private SqlVersion getLibraryVersion() {
        Package p = this.getClass().getPackage();
        if (p != null && p.getImplementationVersion() != null) {
            String ver = p.getImplementationVersion();
            String[] parts = ver.split("\\.");
            try {
                SqlVersion sv = new SqlVersion();
                sv.setMajor(Integer.parseInt(parts[0]));
                sv.setMinor(parts.length > 1 ? Integer.parseInt(parts[1]) : 0);
                sv.setPatch(parts.length > 2 ? Integer.parseInt(parts[2]) : 0);
                sv.setTrivial(parts.length > 3 ? Integer.parseInt(parts[3]) : 0);
                sv.setSubBuild(0);
                return sv;
            } catch (Exception ignored) { }
        }
        SqlVersion sv = new SqlVersion(); sv.setMajor(0); sv.setMinor(0); sv.setPatch(1); sv.setTrivial(0); sv.setSubBuild(0); return sv;
    }

    private PreLoginOption getEncryptionOption() {
        ByteBuffer bb = ByteBuffer.allocate(1);
        if (encrypt) {
            ByteBufferUtils.writeUInt8(bb, EncryptionType.On.value(), 0);
        } else {
            ByteBufferUtils.writeUInt8(bb, EncryptionType.NotSupported.value(), 0);
        }
        PreLoginOption p = new PreLoginOption(); p.setTokenType(TokenType.Encryption); p.setByteBuffer(bb); return p;
    }

    private PreLoginOption getInstanceOption() {
        ByteBuffer bb = ByteBuffer.allocate(1); ByteBufferUtils.writeUInt8(bb, 0x00, 0);
        PreLoginOption p = new PreLoginOption(); p.setTokenType(TokenType.InstOpt); p.setByteBuffer(bb); return p;
    }

    private PreLoginOption getThreadIdOption() {
        ByteBuffer bb = ByteBuffer.allocate(4); ByteBufferUtils.writeUInt32BE(bb, 0x00, 0);
        PreLoginOption p = new PreLoginOption(); p.setTokenType(TokenType.ThreadId); p.setByteBuffer(bb); return p;
    }

    private PreLoginOption getMarsOption() {
        ByteBuffer bb = ByteBuffer.allocate(1); ByteBufferUtils.writeUInt8(bb, MarsType.Off, 0);
        PreLoginOption p = new PreLoginOption(); p.setTokenType(TokenType.Mars); p.setByteBuffer(bb); return p;
    }

    private PreLoginOption getFedAuthOption() {
        ByteBuffer bb = ByteBuffer.allocate(1); ByteBufferUtils.writeUInt8(bb, 0x01, 0);
        PreLoginOption p = new PreLoginOption(); p.setTokenType(TokenType.FedAuthRequired); p.setByteBuffer(bb); return p;
    } 

    private void extractBufferData() {
        int offset = 0;
        while (ByteBufferUtils.readUInt8(buffer, offset) != TokenType.Terminator) {
            int token = ByteBufferUtils.readUInt8(buffer, offset);
            int dataOffset = ByteBufferUtils.readUInt16BE(buffer, offset + 1);
            int dataLength = ByteBufferUtils.readUInt16BE(buffer, offset + 3);

            if (dataLength > 0) {
                switch (token) {
                    case TokenType.Version: parseVersion(dataOffset); break;
                    case TokenType.Encryption: parseEncryption(dataOffset); break;
                    case TokenType.InstOpt: parseInstance(dataOffset); break;
                    case TokenType.ThreadId: parseThreadId(dataOffset); break;
                    case TokenType.Mars: parseMars(dataOffset); break;
                    case TokenType.FedAuthRequired: parseFedAuth(dataOffset); break;
                    default: throw new IllegalStateException(String.format("PreLogin payload is malformed at offset: %d, Token type: %02X", offset, token));
                }
            }

            offset += TokenHeaderSize;
        }
    }

    private void parseVersion(int dataOffset) {
        SqlVersion v = new SqlVersion();
        v.setMajor(ByteBufferUtils.readUInt8(buffer, dataOffset));
        v.setMinor(ByteBufferUtils.readUInt8(buffer, dataOffset + 1));
        v.setPatch(ByteBufferUtils.readUInt8(buffer, dataOffset + 2));
        v.setTrivial(ByteBufferUtils.readUInt8(buffer, dataOffset + 3));
        v.setSubBuild(ByteBufferUtils.readUInt16BE(buffer, dataOffset + 4));
        this.version = v;
    }

    private void parseEncryption(int dataOffset) {
        int encryptionValue = ByteBufferUtils.readUInt8(buffer, dataOffset);
        this.encryption = encryptionValue; // validate by attempting to map
        EncryptionType.fromByte(encryptionValue);
    }

    private void parseInstance(int dataOffset) { this.instance = ByteBufferUtils.readUInt8(buffer, dataOffset); }
    private void parseThreadId(int dataOffset) { this.threadId = ByteBufferUtils.readUInt32BE(buffer, dataOffset); }
    private void parseMars(int dataOffset) { this.mars = ByteBufferUtils.readUInt8(buffer, dataOffset); }
    private void parseFedAuth(int dataOffset) { this.fedAuth = ByteBufferUtils.readUInt8(buffer, dataOffset); }

    @Override
    public String toString() {
        return String.format("PreLoginPayload[Version=%s, Encryption=0x%02X, Instance=0x%02X, ThreadId=0x%08X, Mars=0x%02X(%s)]",
                version, encryption, instance, threadId, mars, MarsType.getString(mars));
    }

}
