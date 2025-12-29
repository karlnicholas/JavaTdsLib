package com.microsoft.data.tools.tdslib.payloads.prelogin;

import com.microsoft.data.tools.tdslib.payloads.Payload;
import com.microsoft.data.tools.tdslib.packets.PacketOffset;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

/**
 * PreLogin payload.
 */
public class PreLoginPayload extends Payload {

    private static final int TOKEN_HEADER_SIZE = 5;

    private final boolean encrypt;

    // Properties
    private EncryptionType encryption;
    private SqlVersion version;
    private byte instance;
    private long threadId; // unsigned int -> long
    private byte mars;
    private byte fedAuth;

    /**
     * Create a new instance of this class.
     *
     * @param encrypt True to enable encryption, false to disable.
     */
    public PreLoginPayload(boolean encrypt) {
        this.encrypt = encrypt;
        // In Java, we call buildBuffer() explicitly when needed,
        // but for compatibility with C# structure, we can pre-calc.
        buildBufferInternal();
        extractBufferData();
    }

    // Default constructor for standard usage
    public PreLoginPayload() {
        this(false);
    }

    /**
     * Create a new instance of this class from a raw buffer.
     *
     * @param buffer The raw payload buffer.
     */
    public PreLoginPayload(ByteBuffer buffer) {
        this.encrypt = false; // Not relevant when reading existing buffer
        setBuffer(buffer);
        extractBufferData();
    }

    // Getters
    public EncryptionType getEncryption() { return encryption; }
    public SqlVersion getVersion() { return version; }
    public byte getInstance() { return instance; }
    public long getThreadId() { return threadId; }
    public byte getMars() { return mars; }
    public byte getFedAuth() { return fedAuth; }

    @Override
    protected void buildBufferInternal() {
        List<PreLoginOption> optionList = new ArrayList<>();
        optionList.add(getVersionOption());
        optionList.add(getEncryptionOption());
        optionList.add(getInstanceOption());
        optionList.add(getThreadIdOption());
        optionList.add(getMarsOption());
        optionList.add(getFedAuthOption());

        // Calculate total length: (HeaderSize + DataLength) for each option + 1 byte Terminator
        int totalDataLength = 0;
        for (PreLoginOption opt : optionList) {
            totalDataLength += TOKEN_HEADER_SIZE + opt.byteBuffer.limit();
        }
        totalDataLength += 1; // Terminator

        ByteBuffer buffer = ByteBuffer.allocate(totalDataLength);
        buffer.order(ByteOrder.LITTLE_ENDIAN);

        int offsetOption = 0;
        int offsetData = (TOKEN_HEADER_SIZE * optionList.size()) + 1;

        // Write Header Pointers
        for (PreLoginOption option : optionList) {
            // [Token:1] [Offset:2] [Length:2]
            buffer.put(offsetOption, option.tokenType);

            // Write Big Endian Shorts for Offset/Length in PreLogin header (Protocol quirk)
            buffer.order(ByteOrder.BIG_ENDIAN);
            buffer.putShort(offsetOption + 1, (short) offsetData);
            buffer.putShort(offsetOption + 3, (short) option.byteBuffer.limit());
            buffer.order(ByteOrder.LITTLE_ENDIAN);

            // Write Actual Data at calculated offset
            // We must duplicate/slice to write without disturbing the main buffer's position
            ByteBuffer view = buffer.duplicate();
            view.order(ByteOrder.LITTLE_ENDIAN);
            view.position(offsetData);
            view.put(option.byteBuffer);

            offsetOption += TOKEN_HEADER_SIZE;
            offsetData += option.byteBuffer.limit();
        }

        buffer.put(offsetOption, TokenType.TERMINATOR);

        // Prepare final buffer
        buffer.position(0);
        buffer.limit(totalDataLength);
        setBuffer(buffer);
    }

    private void extractBufferData() {
        if (getBuffer() == null) return;

        ByteBuffer buf = getBuffer().duplicate();
        buf.order(ByteOrder.LITTLE_ENDIAN);

        int offset = 0;

        // Loop until Terminator (0xFF)
        while ((buf.get(offset) & 0xFF) != (TokenType.TERMINATOR & 0xFF)) {
            byte token = buf.get(offset);

            // Read Big Endian headers
            buf.order(ByteOrder.BIG_ENDIAN);
            int dataOffset = Short.toUnsignedInt(buf.getShort(offset + 1));
            int dataLength = Short.toUnsignedInt(buf.getShort(offset + 3));
            buf.order(ByteOrder.LITTLE_ENDIAN);

            if (dataLength > 0) {
                switch (token) {
                    case TokenType.VERSION:
                        parseVersion(buf, dataOffset);
                        break;
                    case TokenType.ENCRYPTION:
                        parseEncryption(buf, dataOffset);
                        break;
                    case TokenType.INST_OPT:
                        parseInstance(buf, dataOffset);
                        break;
                    case TokenType.THREAD_ID:
                        parseThreadId(buf, dataOffset);
                        break;
                    case TokenType.MARS:
                        parseMars(buf, dataOffset);
                        break;
                    case TokenType.FED_AUTH_REQUIRED:
                        parseFedAuth(buf, dataOffset);
                        break;
                    default:
                        // Unknown token, skip safely
                        break;
                }
            }
            offset += TOKEN_HEADER_SIZE;
        }
    }

    // --- Parsing Helpers ---

    private void parseVersion(ByteBuffer buf, int offset) {
        // [Major] [Minor] [Patch] [Trivial] [SubBuild:2]
        byte major = buf.get(offset);
        byte minor = buf.get(offset + 1);
        byte patch = buf.get(offset + 2);
        byte trivial = buf.get(offset + 3);

        buf.order(ByteOrder.BIG_ENDIAN);
        int subBuild = Short.toUnsignedInt(buf.getShort(offset + 4));
        buf.order(ByteOrder.LITTLE_ENDIAN);

        this.version = new SqlVersion(major, minor, patch, trivial, subBuild);
    }

    private void parseEncryption(ByteBuffer buf, int offset) {
        byte val = buf.get(offset);
        this.encryption = EncryptionType.fromValue(val);
    }

    private void parseInstance(ByteBuffer buf, int offset) {
        this.instance = buf.get(offset);
    }

    private void parseThreadId(ByteBuffer buf, int offset) {
        buf.order(ByteOrder.BIG_ENDIAN);
        this.threadId = Integer.toUnsignedLong(buf.getInt(offset));
        buf.order(ByteOrder.LITTLE_ENDIAN);
    }

    private void parseMars(ByteBuffer buf, int offset) {
        this.mars = buf.get(offset);
    }

    private void parseFedAuth(ByteBuffer buf, int offset) {
        this.fedAuth = buf.get(offset);
    }

    // --- Building Helpers ---

    private static class PreLoginOption {
        byte tokenType;
        ByteBuffer byteBuffer;

        PreLoginOption(byte tokenType, ByteBuffer byteBuffer) {
            this.tokenType = tokenType;
            this.byteBuffer = byteBuffer;
        }
    }

    private PreLoginOption getVersionOption() {
        ByteBuffer b = ByteBuffer.allocate(6);
        // Default Version 1.0.1.0
        // Major, Minor, Build(Hi), Build(Lo), Rev, Rev(Hi)
        b.put((byte) 1);
        b.put((byte) 0);
        b.put((byte) 0); // Build Hi
        b.put((byte) 1); // Build Lo
        b.put((byte) 0); // Rev
        b.put((byte) 0); // Rev Hi
        b.flip();
        return new PreLoginOption(TokenType.VERSION, b);
    }

    private PreLoginOption getEncryptionOption() {
        ByteBuffer b = ByteBuffer.allocate(1);
        b.put(encrypt ? EncryptionType.ON.getValue() : EncryptionType.NOT_SUPPORTED.getValue());
        b.flip();
        return new PreLoginOption(TokenType.ENCRYPTION, b);
    }

    private PreLoginOption getInstanceOption() {
        ByteBuffer b = ByteBuffer.allocate(1);
        b.put((byte) 0x00);
        b.flip();
        return new PreLoginOption(TokenType.INST_OPT, b);
    }

    private PreLoginOption getThreadIdOption() {
        ByteBuffer b = ByteBuffer.allocate(4);
        b.order(ByteOrder.BIG_ENDIAN);
        b.putInt(0); // Thread ID 0
        b.flip();
        return new PreLoginOption(TokenType.THREAD_ID, b);
    }

    private PreLoginOption getMarsOption() {
        ByteBuffer b = ByteBuffer.allocate(1);
        b.put(MarsType.OFF);
        b.flip();
        return new PreLoginOption(TokenType.MARS, b);
    }

    private PreLoginOption getFedAuthOption() {
        ByteBuffer b = ByteBuffer.allocate(1);
        b.put((byte) 0x01);
        b.flip();
        return new PreLoginOption(TokenType.FED_AUTH_REQUIRED, b);
    }

    @Override
    public String toString() {
        return "PreLoginPayload[Version=" + version + ", Encryption=" + encryption + "]";
    }
}