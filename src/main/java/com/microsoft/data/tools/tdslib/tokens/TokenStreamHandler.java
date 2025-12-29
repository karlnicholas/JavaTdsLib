package com.microsoft.data.tools.tdslib.tokens;

import com.microsoft.data.tools.tdslib.TdsClient;
import com.microsoft.data.tools.tdslib.io.connection.ConnectionOptions;
import com.microsoft.data.tools.tdslib.messages.Message;
import com.microsoft.data.tools.tdslib.tokens.done.DoneTokenParser;
import com.microsoft.data.tools.tdslib.tokens.doneinproc.DoneInProcTokenParser;
import com.microsoft.data.tools.tdslib.tokens.doneproc.DoneProcTokenParser;
import com.microsoft.data.tools.tdslib.tokens.envchange.EnvChangeTokenParser;
import com.microsoft.data.tools.tdslib.tokens.error.ErrorTokenParser;
import com.microsoft.data.tools.tdslib.tokens.featureextack.FeatureExtAckTokenParser;
import com.microsoft.data.tools.tdslib.tokens.fedauthinfo.FedAuthInfoTokenParser;
import com.microsoft.data.tools.tdslib.tokens.info.InfoTokenParser;
import com.microsoft.data.tools.tdslib.tokens.loginack.LoginAckTokenParser;
import com.microsoft.data.tools.tdslib.tokens.returnstatus.ReturnStatusTokenParser;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Token data stream handler.
 */
public final class TokenStreamHandler {

    private final TdsClient tdsClient;
    private final Map<TokenType, TokenParser> parsers;

    private ByteBuffer incomingTokenBuffer;

    public TokenStreamHandler(TdsClient tdsClient) {
        this.tdsClient = tdsClient;
        this.parsers = new HashMap<>();

        // Register all parsers
        parsers.put(TokenType.ENV_CHANGE, new EnvChangeTokenParser());
        parsers.put(TokenType.LOGIN_ACK, new LoginAckTokenParser());
        parsers.put(TokenType.FEATURE_EXT_ACK, new FeatureExtAckTokenParser());
        parsers.put(TokenType.DONE, new DoneTokenParser());
        parsers.put(TokenType.DONE_IN_PROC, new DoneInProcTokenParser());
        parsers.put(TokenType.DONE_PROC, new DoneProcTokenParser());
        parsers.put(TokenType.FED_AUTH_INFO, new FedAuthInfoTokenParser());
        parsers.put(TokenType.INFO, new InfoTokenParser());
        parsers.put(TokenType.ERROR, new ErrorTokenParser());
        parsers.put(TokenType.RETURN_STATUS, new ReturnStatusTokenParser());
    }

    public ConnectionOptions getOptions() {
        return tdsClient.getConnection().getOptions();
    }

    /**
     * Ensures we have 'size' bytes available in the buffer.
     */
    private void waitForData(int size) throws IOException {
        // Initialize if null
        if (incomingTokenBuffer == null) {
            Message message = tdsClient.getMessageHandler().receiveMessage();
            incomingTokenBuffer = message.getPayload().getBuffer();
        }

        // Fetch more if needed
        while (incomingTokenBuffer.remaining() < size) {
            Message message = tdsClient.getMessageHandler().receiveMessage();
            ByteBuffer newPayload = message.getPayload().getBuffer();
            incomingTokenBuffer = concat(incomingTokenBuffer, newPayload);
        }
    }

    private boolean dataAvailable() {
        return incomingTokenBuffer != null && incomingTokenBuffer.hasRemaining();
    }

    private void trimBuffer() {
        if (incomingTokenBuffer == null) return;

        if (!incomingTokenBuffer.hasRemaining()) {
            incomingTokenBuffer = null;
        } else {
            // Compact the buffer: move remaining bytes to the start
            // This prevents the buffer from growing indefinitely if we keep concatenating
            incomingTokenBuffer.compact();
            incomingTokenBuffer.flip(); // Prepare for reading
        }
    }

    private void clearBuffer() {
        incomingTokenBuffer = null;
    }

    /**
     * Helper to concatenate two buffers.
     */
    private ByteBuffer concat(ByteBuffer a, ByteBuffer b) {
        // Note: 'a' is ready for reading (position at start of data).
        // 'b' is ready for reading.
        ByteBuffer res = ByteBuffer.allocate(a.remaining() + b.remaining());
        res.order(ByteOrder.LITTLE_ENDIAN);
        res.put(a);
        res.put(b);
        res.flip();
        return res;
    }

    /**
     * Receive a token.
     */
    public Token receiveToken() throws IOException {
        int typeByte = readUInt8();

        TokenType tokenType;
        try {
            tokenType = TokenType.fromValue(typeByte);
        } catch (IllegalArgumentException e) {
            throw new IOException("Unsupported Token type: 0x" + Integer.toHexString(typeByte));
        }

        if (!parsers.containsKey(tokenType)) {
            throw new IOException("No parser registered for Token type: " + tokenType);
        }

        Token token = parsers.get(tokenType).parse(tokenType, this);

        // C# logic calls TrimBuffer after every token parse
        trimBuffer();

        return token;
    }

    /**
     * Receives tokens until the end of data or the receiver exits.
     */
    public void receiveTokens(Consumer<TokenEvent> funcTokenReceiver) throws IOException {
        TokenEvent tokenEvent = new TokenEvent();

        try {
            do {
                tokenEvent.setToken(receiveToken());
                funcTokenReceiver.accept(tokenEvent);

                if (tokenEvent.isExit()) {
                    break;
                }
            } while (dataAvailable());
        } finally {
            clearBuffer();
        }
    }

    // ==================================================================================
    //  PRIMITIVE READERS
    // ==================================================================================

    public byte readInt8() throws IOException {
        waitForData(1);
        return incomingTokenBuffer.get();
    }

    public int readUInt8() throws IOException {
        waitForData(1);
        return Byte.toUnsignedInt(incomingTokenBuffer.get());
    }

    public short readInt16LE() throws IOException {
        waitForData(2);
        incomingTokenBuffer.order(ByteOrder.LITTLE_ENDIAN);
        return incomingTokenBuffer.getShort();
    }

    public short readInt16BE() throws IOException {
        waitForData(2);
        incomingTokenBuffer.order(ByteOrder.BIG_ENDIAN);
        return incomingTokenBuffer.getShort();
    }

    public int readUInt16LE() throws IOException {
        waitForData(2);
        incomingTokenBuffer.order(ByteOrder.LITTLE_ENDIAN);
        return Short.toUnsignedInt(incomingTokenBuffer.getShort());
    }

    public int readUInt16BE() throws IOException {
        waitForData(2);
        incomingTokenBuffer.order(ByteOrder.BIG_ENDIAN);
        return Short.toUnsignedInt(incomingTokenBuffer.getShort());
    }

    public int readInt32LE() throws IOException {
        waitForData(4);
        incomingTokenBuffer.order(ByteOrder.LITTLE_ENDIAN);
        return incomingTokenBuffer.getInt();
    }

    public int readInt32BE() throws IOException {
        waitForData(4);
        incomingTokenBuffer.order(ByteOrder.BIG_ENDIAN);
        return incomingTokenBuffer.getInt();
    }

    public long readUInt32LE() throws IOException {
        waitForData(4);
        incomingTokenBuffer.order(ByteOrder.LITTLE_ENDIAN);
        return Integer.toUnsignedLong(incomingTokenBuffer.getInt());
    }

    public long readUInt32BE() throws IOException {
        waitForData(4);
        incomingTokenBuffer.order(ByteOrder.BIG_ENDIAN);
        return Integer.toUnsignedLong(incomingTokenBuffer.getInt());
    }

    public long readInt64LE() throws IOException {
        waitForData(8);
        incomingTokenBuffer.order(ByteOrder.LITTLE_ENDIAN);
        return incomingTokenBuffer.getLong();
    }

    public long readInt64BE() throws IOException {
        waitForData(8);
        incomingTokenBuffer.order(ByteOrder.BIG_ENDIAN);
        return incomingTokenBuffer.getLong();
    }

    public BigInteger readUInt64LE() throws IOException {
        waitForData(8);
        // Java doesn't have native ulong. We read 8 bytes, reverse them (for LE), make Positive BigInt
        byte[] bytes = new byte[8];
        incomingTokenBuffer.get(bytes);
        // Reverse for Little Endian
        reverse(bytes);
        return new BigInteger(1, bytes);
    }

    public BigInteger readUInt64BE() throws IOException {
        waitForData(8);
        byte[] bytes = new byte[8];
        incomingTokenBuffer.get(bytes);
        // Already BE
        return new BigInteger(1, bytes);
    }

    public float readFloatLE() throws IOException {
        waitForData(4);
        incomingTokenBuffer.order(ByteOrder.LITTLE_ENDIAN);
        return incomingTokenBuffer.getFloat();
    }

    public float readFloatBE() throws IOException {
        waitForData(4);
        incomingTokenBuffer.order(ByteOrder.BIG_ENDIAN);
        return incomingTokenBuffer.getFloat();
    }

    public double readDoubleLE() throws IOException {
        waitForData(8);
        incomingTokenBuffer.order(ByteOrder.LITTLE_ENDIAN);
        return incomingTokenBuffer.getDouble();
    }

    public double readDoubleBE() throws IOException {
        waitForData(8);
        incomingTokenBuffer.order(ByteOrder.BIG_ENDIAN);
        return incomingTokenBuffer.getDouble();
    }

    public int readUInt24LE() throws IOException {
        waitForData(3);
        int low = readUInt16LE();
        int high = readUInt8();
        return low | (high << 16);
    }

    public long readUInt40LE() throws IOException {
        waitForData(5);
        long low = readUInt32LE();
        long high = readUInt8();
        return low | (high << 32);
    }

    /**
     * Reads a 64-bit Unsigned Numeric (Little Endian).
     * C# logic reads two UInt32LE and combines them.
     */
    public BigInteger readUNumeric64LE() throws IOException {
        waitForData(8);
        long low = readUInt32LE();
        long high = readUInt32LE();

        // low | (high << 32)
        return BigInteger.valueOf(low)
                .or(BigInteger.valueOf(high).shiftLeft(32));
    }

    /**
     * Reads a 96-bit Unsigned Numeric (Little Endian).
     * C# logic reads three UInt32LE.
     */
    public BigInteger readUNumeric96LE() throws IOException {
        waitForData(12);
        long dword1 = readUInt32LE();
        long dword2 = readUInt32LE();
        long dword3 = readUInt32LE();

        return BigInteger.valueOf(dword3).shiftLeft(64)
                .or(BigInteger.valueOf(dword2).shiftLeft(32))
                .or(BigInteger.valueOf(dword1));
    }

    /**
     * Reads a 128-bit Unsigned Numeric (Little Endian).
     * C# logic reads four UInt32LE.
     */
    public BigInteger readUNumeric128LE() throws IOException {
        waitForData(16);
        long dword1 = readUInt32LE();
        long dword2 = readUInt32LE();
        long dword3 = readUInt32LE();
        long dword4 = readUInt32LE();

        return BigInteger.valueOf(dword4).shiftLeft(96)
                .or(BigInteger.valueOf(dword3).shiftLeft(64))
                .or(BigInteger.valueOf(dword2).shiftLeft(32))
                .or(BigInteger.valueOf(dword1));
    }

    public ByteBuffer readBuffer(int length) throws IOException {
        waitForData(length);
        // Slice the next 'length' bytes
        ByteBuffer slice = incomingTokenBuffer.duplicate();
        slice.order(ByteOrder.LITTLE_ENDIAN);
        slice.limit(slice.position() + length);

        // Advance original buffer
        incomingTokenBuffer.position(incomingTokenBuffer.position() + length);

        return slice;
    }

    // ==================================================================================
    //  VARIABLE LENGTH READERS
    // ==================================================================================

    /**
     * Reads a Unicode string prefixed by a 1-byte length.
     */
    public String readBVarChar() throws IOException {
        int charCount = readUInt8();
        int byteLen = charCount * 2;

        waitForData(byteLen);
        byte[] bytes = new byte[byteLen];
        incomingTokenBuffer.get(bytes);

        return new String(bytes, StandardCharsets.UTF_16LE);
    }

    /**
     * Reads a Unicode string prefixed by a 2-byte (UShort) length.
     */
    public String readUsVarChar() throws IOException {
        int charCount = readUInt16LE();
        int byteLen = charCount * 2;

        waitForData(byteLen);
        byte[] bytes = new byte[byteLen];
        incomingTokenBuffer.get(bytes);

        return new String(bytes, StandardCharsets.UTF_16LE);
    }

    /**
     * Reads a byte array prefixed by a 1-byte length.
     */
    public ByteBuffer readBVarByte() throws IOException {
        int length = readUInt8();
        return readBuffer(length);
    }

    /**
     * Reads a byte array prefixed by a 2-byte (UShort) length.
     */
    public ByteBuffer readUsVarByte() throws IOException {
        int length = readUInt16LE();
        return readBuffer(length);
    }

    // Helper
    private void reverse(byte[] array) {
        if (array == null) return;
        int i = 0;
        int j = array.length - 1;
        byte tmp;
        while (j > i) {
            tmp = array[j];
            array[j] = array[i];
            array[i] = tmp;
            j--;
            i++;
        }
    }
}