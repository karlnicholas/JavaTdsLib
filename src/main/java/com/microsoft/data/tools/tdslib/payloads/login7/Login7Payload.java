package com.microsoft.data.tools.tdslib.payloads.login7;

import com.microsoft.data.tools.tdslib.payloads.Payload;
import com.microsoft.data.tools.tdslib.payloads.login7.auth.FedAuth;
// Assuming these are simple data classes or Enums you have/will create:
// import com.microsoft.data.tools.tdslib.payloads.login7.OptionFlags1;
// import com.microsoft.data.tools.tdslib.payloads.login7.OptionFlags2;
// import com.microsoft.data.tools.tdslib.payloads.login7.OptionFlags3;
// import com.microsoft.data.tools.tdslib.payloads.login7.TypeFlags;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Random;

/**
 * Login7 payload.
 */
public final class Login7Payload extends Payload {

    private static final byte FEATURE_EXTENSION_TERMINATOR = (byte) 0xFF;
    private static final int CLIENT_ID_SIZE = 6;
    private static final int FIXED_HEADER_SIZE = 94;

    public Login7Options options;

    // In C# these are likely Enums or Structs.
    // Representing as Object or Byte placeholders for now.
    public Object optionFlags1;
    public Object optionFlags2;
    public Object optionFlags3;
    public Object typeFlags;

    public String username;
    public String password;
    public String serverName;
    public String appName;
    public String hostname;
    public String libraryName;
    public String language;
    public String database;

    // Mac Address / ClientID
    public byte[] clientId;

    public byte[] sspi;
    public String attachDbFile;
    public String changePassword;
    public FedAuth fedAuth;

    public Login7Payload() {
        this(null);
    }

    public Login7Payload(Login7Options options) {
        this.options = (options != null) ? options : new Login7Options();

        // Default initializations
        // In a real implementation, you would instantiate your Flag classes here
        this.libraryName = "TdsLib-Java"; // Updated library name

        // We do not call buildBufferInternal() in constructor in Java
        // usually, but we can if we want to pre-calculate.
        // For this pattern, we'll let the user call buildBuffer().
    }

    @Override
    protected void buildBufferInternal() {
        try {
            // 1. Fixed Header Buffer (94 bytes)
            ByteBuffer header = ByteBuffer.allocate(FIXED_HEADER_SIZE);
            header.order(ByteOrder.LITTLE_ENDIAN);

            // 2. Variable Data Stream
            ByteArrayOutputStream varData = new ByteArrayOutputStream();

            // Pointers
            int offset = 4; // Start writing fields after length (first 4 bytes are total packet size)
            int dataOffset = FIXED_HEADER_SIZE; // Variable data starts after header

            // --- Write Fixed Header Options ---
            // Note: C# WriteUInt32LE writes at specific offset.
            // Java ByteBuffer.putInt(index, value) does the same.

            // We use 'long' for unsigned int values to prevent overflow
            header.putInt(offset, (int) options.getTdsVersion().getVersionValue()); offset += 4;
            header.putInt(offset, (int) options.getPacketSize()); offset += 4;
            header.putInt(offset, (int) options.getClientProgVer()); offset += 4;
            header.putInt(offset, (int) options.getClientPid()); offset += 4;
            header.putInt(offset, (int) options.getConnectionId()); offset += 4;

            // Flags (Assuming accessors return byte or int)
            header.put(offset, getByteFromFlag(optionFlags1)); offset += 1;
            header.put(offset, getByteFromFlag(optionFlags2)); offset += 1;
            header.put(offset, getByteFromFlag(typeFlags)); offset += 1;
            header.put(offset, getByteFromFlag(optionFlags3)); offset += 1;

            header.putInt(offset, options.getClientTimeZone()); offset += 4;
            header.putInt(offset, (int) options.getClientLcid()); offset += 4;

            // --- Write Variable Data ---
            // Helper to write String fields
            // Returns the new offset position in the header
            offset = writeStringField(header, varData, hostname, offset, dataOffset);
            dataOffset += (hostname == null) ? 0 : hostname.getBytes(StandardCharsets.UTF_16LE).length;

            offset = writeStringField(header, varData, username, offset, dataOffset);
            dataOffset += (username == null) ? 0 : username.getBytes(StandardCharsets.UTF_16LE).length;

            // Password (Scrambled)
            offset = writeScrambledPassword(header, varData, password, offset, dataOffset);
            dataOffset += (password == null) ? 0 : password.length() * 2; // UTF-16 bytes

            offset = writeStringField(header, varData, appName, offset, dataOffset);
            dataOffset += (appName == null) ? 0 : appName.getBytes(StandardCharsets.UTF_16LE).length;

            offset = writeStringField(header, varData, serverName, offset, dataOffset);
            dataOffset += (serverName == null) ? 0 : serverName.getBytes(StandardCharsets.UTF_16LE).length;

            // Extensions (Complex handling)
            // Writing Pointer for Extensions
            header.putShort(offset, (short) dataOffset); offset += 2; // Pointer
            header.putShort(offset, (short) 4); offset += 2; // Length (sizeof uint)

            // Write Extension Data
            byte[] extensionsBytes = getExtensionsBytes();

            // Extension Offset block (the C# code adds a 4-byte offset block before the actual extension data)
            int extensionDataStart = dataOffset + 4; // 4 bytes for the offset itself
            ByteBuffer extOffsetBuf = ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN);
            extOffsetBuf.putInt(extensionDataStart);
            varData.write(extOffsetBuf.array());

            varData.write(extensionsBytes);
            dataOffset += 4 + extensionsBytes.length;

            // Library Name
            offset = writeStringField(header, varData, libraryName, offset, dataOffset);
            dataOffset += (libraryName == null) ? 0 : libraryName.getBytes(StandardCharsets.UTF_16LE).length;

            // Language
            offset = writeStringField(header, varData, language, offset, dataOffset);
            dataOffset += (language == null) ? 0 : language.getBytes(StandardCharsets.UTF_16LE).length;

            // Database
            offset = writeStringField(header, varData, database, offset, dataOffset);
            dataOffset += (database == null) ? 0 : database.getBytes(StandardCharsets.UTF_16LE).length;

            // Client ID (MAC Address)
            if (clientId == null) {
                clientId = generateRandomPhysicalAddress();
            }
            // ClientID is written directly into the fixed header at current offset
            for (int i = 0; i < CLIENT_ID_SIZE; i++) {
                if (i < clientId.length) header.put(offset + i, clientId[i]);
            }
            offset += 6;

            // SSPI
            header.putShort(offset, (short) dataOffset); offset += 2;
            if (sspi != null && sspi.length > 0) {
                int len = sspi.length;
                header.putShort(offset, (short) (len > 65535 ? 65535 : len));
                varData.write(sspi);
                dataOffset += len;
            } else {
                header.putShort(offset, (short) 0);
            }
            offset += 2; // Increment offset for the length field we just wrote

            // AttachDB
            offset = writeStringField(header, varData, attachDbFile, offset, dataOffset);
            dataOffset += (attachDbFile == null) ? 0 : attachDbFile.getBytes(StandardCharsets.UTF_16LE).length;

            // Change Password
            offset = writeScrambledPassword(header, varData, changePassword, offset, dataOffset);
            dataOffset += (changePassword == null) ? 0 : changePassword.length() * 2;

            // SSPI Long (Length > 65535)
            if (sspi != null && sspi.length > 65535) {
                header.putInt(offset, sspi.length);
            } else {
                header.putInt(offset, 0);
            }
            offset += 4;

            // --- Final Assembly ---
            byte[] variableBytes = varData.toByteArray();
            int totalLength = FIXED_HEADER_SIZE + variableBytes.length;

            ByteBuffer finalBuffer = ByteBuffer.allocate(totalLength);
            finalBuffer.order(ByteOrder.LITTLE_ENDIAN);

            // Write total length at the very start of the header
            header.putInt(0, totalLength);

            finalBuffer.put(header.array());
            finalBuffer.put(variableBytes);

            finalBuffer.flip();
            this.setBuffer(finalBuffer);

        } catch (IOException e) {
            throw new RuntimeException("Error building Login7 buffer", e);
        }
    }

    /**
     * Helper to write string pointers and data.
     * TDS Header Format: [Offset (2 bytes)] [Length in CHARS (2 bytes)]
     */
    private int writeStringField(ByteBuffer header, ByteArrayOutputStream varData, String value, int headerOffset, int currentDataOffset) throws IOException {
        // Pointer to data
        header.putShort(headerOffset, (short) currentDataOffset);
        headerOffset += 2;

        if (value == null || value.isEmpty()) {
            header.putShort(headerOffset, (short) 0);
        } else {
            byte[] bytes = value.getBytes(StandardCharsets.UTF_16LE);
            varData.write(bytes);
            // TDS Length is in Characters (UTF-16 code units), not bytes
            header.putShort(headerOffset, (short) value.length());
        }
        headerOffset += 2; // Move past length field
        return headerOffset;
    }

    private int writeScrambledPassword(ByteBuffer header, ByteArrayOutputStream varData, String pwd, int headerOffset, int currentDataOffset) throws IOException {
        header.putShort(headerOffset, (short) currentDataOffset);
        headerOffset += 2;

        if (pwd == null || pwd.isEmpty()) {
            header.putShort(headerOffset, (short) 0);
        } else {
            byte[] bytes = pwd.getBytes(StandardCharsets.UTF_16LE);
            byte[] scrambled = scramblePassword(bytes);
            varData.write(scrambled);
            header.putShort(headerOffset, (short) pwd.length());
        }
        headerOffset += 2;
        return headerOffset;
    }

    private byte[] scramblePassword(byte[] byteArray) {
        byte[] result = new byte[byteArray.length];
        for (int i = 0; i < byteArray.length; i++) {
            byte b = byteArray[i];
            // Java shift is signed. We must treat as unsigned for the swap logic.
            int unsignedB = b & 0xFF;
            int swapped = ((unsignedB >> 4) | (unsignedB << 4)) & 0xFF;
            result[i] = (byte) (swapped ^ 0xA5);
        }
        return result;
    }

    private byte[] generateRandomPhysicalAddress() {
        byte[] addr = new byte[CLIENT_ID_SIZE];
        new SecureRandom().nextBytes(addr);
        return addr;
    }

    private byte[] getExtensionsBytes() throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        if (fedAuth != null) {
            // Assuming FedAuth has a buildBuffer() or getBuffer() returning ByteBuffer
            // bos.write(fedAuth.getBuffer().array());
        }
        bos.write(FEATURE_EXTENSION_TERMINATOR);
        return bos.toByteArray();
    }

    private byte getByteFromFlag(Object flag) {
        // Placeholder: Return 0 or cast the flag object to byte/int
        if (flag instanceof Number) {
            return ((Number) flag).byteValue();
        }
        return 0;
    }

    @Override
    public String toString() {
        return "Login7Payload[Host=" + hostname + ", User=" + username + "]";
    }
}