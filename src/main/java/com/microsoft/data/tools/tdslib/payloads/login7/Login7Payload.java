// Copyright (c) Microsoft Corporation.
// Licensed under the MIT License.

package com.microsoft.data.tools.tdslib.payloads.login7;

import com.microsoft.data.tools.tdslib.buffer.ByteBufferUtils;
import com.microsoft.data.tools.tdslib.payloads.Payload;
import com.microsoft.data.tools.tdslib.payloads.login7.auth.FedAuth;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public final class Login7Payload extends Payload {

    private static final byte FeatureExtensionTerminator = (byte)0xFF;
    private static final int ClientIdSize = 6;

    private Login7Options options;
    private OptionFlags1 optionFlags1;
    private OptionFlags2 optionFlags2;
    private OptionFlags3 optionFlags3;
    private TypeFlags typeFlags;

    private String username;
    private String password;
    private String serverName;
    private String appName;
    private String hostname;
    private String libraryName;
    private String language;
    private String database;

    private ByteBuffer clientId;
    private ByteBuffer sspi;
    private String attachDbFile;
    private String changePassword;

    private FedAuth fedAuth;

    public Login7Payload() { this(null); }

    public Login7Payload(Login7Options options) {
        this.options = options == null ? new Login7Options() : options;
        this.optionFlags1 = new OptionFlags1();
        this.optionFlags2 = new OptionFlags2();
        this.optionFlags3 = new OptionFlags3();
        this.typeFlags = new TypeFlags();
        this.libraryName = "TdsLib";

        buildBufferInternal();
    }

    @Override
    protected void buildBufferInternal() {
        List<ByteBuffer> buffers = new ArrayList<>();
        ByteBuffer buffer = ByteBuffer.allocate(94);
        buffers.add(buffer);

        int offset = 4;
        int dataOffset = ByteBufferUtils.length(buffer); 

        // Options
        offset = ByteBufferUtils.writeUInt32LE(buffer, options.getTdsVersion().value(), offset);
        offset = ByteBufferUtils.writeUInt32LE(buffer, options.getPacketSize(), offset);
        offset = ByteBufferUtils.writeUInt32LE(buffer, options.getClientProgVer(), offset);
        offset = ByteBufferUtils.writeUInt32LE(buffer, options.getClientPid(), offset);
        offset = ByteBufferUtils.writeUInt32LE(buffer, options.getConnectionId(), offset);
        offset = ByteBufferUtils.writeUInt8(buffer, optionFlags1.toByte(), offset);
        offset = ByteBufferUtils.writeUInt8(buffer, optionFlags2.toByte(), offset);
        offset = ByteBufferUtils.writeUInt8(buffer, typeFlags.toByte(), offset);
        offset = ByteBufferUtils.writeUInt8(buffer, optionFlags3.toByte(), offset);

        offset = ByteBufferUtils.writeInt32LE(buffer, options.getClientTimeZone(), offset);
        offset = ByteBufferUtils.writeUInt32LE(buffer, options.getClientLcid(), offset); 

        // Hostname
        offset = ByteBufferUtils.writeUInt16LE(buffer, dataOffset, offset);
        if (hostname == null || hostname.isEmpty()) {
            offset = ByteBufferUtils.writeUInt16LE(buffer, 0, offset);
        } else {
            byte[] hostnameBytes;
            try { hostnameBytes = hostname.getBytes("UTF-16LE"); } catch (Exception e) { hostnameBytes = hostname.getBytes(StandardCharsets.UTF_16); }
            ByteBuffer hostnameBuffer = ByteBuffer.wrap(hostnameBytes);
            buffers.add(hostnameBuffer);
            offset = ByteBufferUtils.writeUInt16LE(buffer, hostname.length(), offset);
            dataOffset += ByteBufferUtils.length(hostnameBuffer);
        } 

        // Username
        offset = ByteBufferUtils.writeUInt16LE(buffer, dataOffset, offset);
        if (username == null || username.isEmpty()) {
            offset = ByteBufferUtils.writeUInt16LE(buffer, 0, offset);
        } else {
            byte[] usernameBytes;
            try { usernameBytes = username.getBytes("UTF-16LE"); } catch (Exception e) { usernameBytes = username.getBytes(StandardCharsets.UTF_16); }
            ByteBuffer usernameBuffer = ByteBuffer.wrap(usernameBytes);
            buffers.add(usernameBuffer);
            offset = ByteBufferUtils.writeUInt16LE(buffer, username.length(), offset);
            dataOffset += ByteBufferUtils.length(usernameBuffer);
        } 

        // Password
        offset = ByteBufferUtils.writeUInt16LE(buffer, dataOffset, offset);
        if (password == null || password.isEmpty()) {
            offset = ByteBufferUtils.writeUInt16LE(buffer, 0, offset);
        } else {
            byte[] passwordBytes;
            try { passwordBytes = password.getBytes("UTF-16LE"); } catch (Exception e) { passwordBytes = password.getBytes(StandardCharsets.UTF_16); }
            byte[] scrambled = scramblePassword(passwordBytes);
            ByteBuffer passwordBuffer = ByteBuffer.wrap(scrambled);
            buffers.add(passwordBuffer);
            offset = ByteBufferUtils.writeUInt16LE(buffer, password.length(), offset);
            dataOffset += ByteBufferUtils.length(passwordBuffer);
        }

        // AppName
        offset = ByteBufferUtils.writeUInt16LE(buffer, dataOffset, offset);
        if (appName == null || appName.isEmpty()) {
            offset = ByteBufferUtils.writeUInt16LE(buffer, 0, offset);
        } else {
            byte[] appBytes;
            try { appBytes = appName.getBytes("UTF-16LE"); } catch (Exception e) { appBytes = appName.getBytes(StandardCharsets.UTF_16); }
            ByteBuffer appNameBuffer = ByteBuffer.wrap(appBytes);
            buffers.add(appNameBuffer);
            offset = ByteBufferUtils.writeUInt16LE(buffer, appName.length(), offset);
            dataOffset += ByteBufferUtils.length(appNameBuffer);
        }

        // ServerName
        offset = ByteBufferUtils.writeUInt16LE(buffer, dataOffset, offset);
        if (serverName == null || serverName.isEmpty()) {
            offset = ByteBufferUtils.writeUInt16LE(buffer, 0, offset);
        } else {
            byte[] serverBytes;
            try { serverBytes = serverName.getBytes("UTF-16LE"); } catch (Exception e) { serverBytes = serverName.getBytes(StandardCharsets.UTF_16); }
            ByteBuffer serverNameBuffer = ByteBuffer.wrap(serverBytes);
            buffers.add(serverNameBuffer);
            offset = ByteBufferUtils.writeUInt16LE(buffer, serverName.length(), offset);
            dataOffset += ByteBufferUtils.length(serverNameBuffer);
        } 

        // Extensions
        offset = ByteBufferUtils.writeUInt16LE(buffer, dataOffset, offset);
        ByteBuffer extensionsBuffer = getExtensionsBuffer();
        offset = ByteBufferUtils.writeInt16LE(buffer, 4, offset);

        dataOffset += 4;
        ByteBuffer extensionOffsetBuffer = ByteBuffer.allocate(4);
        ByteBufferUtils.writeUInt32LE(extensionOffsetBuffer, dataOffset, 0);

        dataOffset += ByteBufferUtils.length(extensionsBuffer);

        buffers.add(extensionOffsetBuffer);
        buffers.add(extensionsBuffer); 

        // Library Name
        offset = ByteBufferUtils.writeUInt16LE(buffer, dataOffset, offset);
        if (libraryName == null || libraryName.isEmpty()) {
            offset = ByteBufferUtils.writeInt16LE(buffer, 0, offset);
        } else {
            byte[] libBytes;
            try { libBytes = libraryName.getBytes("UTF-16LE"); } catch (Exception e) { libBytes = libraryName.getBytes(StandardCharsets.UTF_16); }
            ByteBuffer libraryNameBuffer = ByteBuffer.wrap(libBytes);
            buffers.add(libraryNameBuffer);
            offset = ByteBufferUtils.writeUInt16LE(buffer, libraryName.length(), offset);
            dataOffset += ByteBufferUtils.length(libraryNameBuffer);
        }

        // Language
        offset = ByteBufferUtils.writeUInt16LE(buffer, dataOffset, offset);
        if (language == null || language.isEmpty()) {
            offset = ByteBufferUtils.writeInt16LE(buffer, 0, offset);
        } else {
            byte[] languageBytes;
            try { languageBytes = language.getBytes("UTF-16LE"); } catch (Exception e) { languageBytes = language.getBytes(StandardCharsets.UTF_16); }
            ByteBuffer languageBuffer = ByteBuffer.wrap(languageBytes);
            buffers.add(languageBuffer);
            offset = ByteBufferUtils.writeUInt16LE(buffer, language.length(), offset);
            dataOffset += ByteBufferUtils.length(languageBuffer);
        }

        // Database
        offset = ByteBufferUtils.writeUInt16LE(buffer, dataOffset, offset);
        if (database == null || database.isEmpty()) {
            offset = ByteBufferUtils.writeInt16LE(buffer, 0, offset);
        } else {
            byte[] databaseBytes;
            try { databaseBytes = database.getBytes("UTF-16LE"); } catch (Exception e) { databaseBytes = database.getBytes(StandardCharsets.UTF_16); }
            ByteBuffer databaseBuffer = ByteBuffer.wrap(databaseBytes);
            buffers.add(databaseBuffer);
            offset = ByteBufferUtils.writeUInt16LE(buffer, database.length(), offset);
            dataOffset += ByteBufferUtils.length(databaseBuffer);
        } 

        // Client ID
        if (clientId == null) clientId = generateRandomPhysicalAddress();
        ByteBufferUtils.write(buffer, clientId, offset, 0, ClientIdSize);
        offset += ClientIdSize;

        // SSPI
        offset = ByteBufferUtils.writeUInt16LE(buffer, dataOffset, offset);
        if (sspi != null) {
            if (ByteBufferUtils.length(sspi) > 0xFFFF) {
                offset = ByteBufferUtils.writeUInt16LE(buffer, 0xFFFF, offset);
            } else {
                offset = ByteBufferUtils.writeUInt16LE(buffer, ByteBufferUtils.length(sspi), offset);
            }
            buffers.add(sspi);
        } else {
            offset = ByteBufferUtils.writeUInt16LE(buffer, 0, offset);
        } 

        // AttachDB
        offset = ByteBufferUtils.writeUInt16LE(buffer, dataOffset, offset);
        if (attachDbFile == null || attachDbFile.isEmpty()) {
            offset = ByteBufferUtils.writeInt16LE(buffer, 0, offset);
        } else {
            byte[] attachBytes;
            try { attachBytes = attachDbFile.getBytes("UTF-16LE"); } catch (Exception e) { attachBytes = attachDbFile.getBytes(StandardCharsets.UTF_16); }
            ByteBuffer attachDbFileBuffer = ByteBuffer.wrap(attachBytes);
            buffers.add(attachDbFileBuffer);
            offset = ByteBufferUtils.writeUInt16LE(buffer, attachDbFile.length(), offset);
            dataOffset += ByteBufferUtils.length(attachDbFileBuffer);
        }

        // Change Password
        offset = ByteBufferUtils.writeUInt16LE(buffer, dataOffset, offset);
        if (changePassword == null || changePassword.isEmpty()) {
            offset = ByteBufferUtils.writeInt16LE(buffer, 0, offset);
        } else {
            byte[] changeBytes;
            try { changeBytes = changePassword.getBytes("UTF-16LE"); } catch (Exception e) { changeBytes = changePassword.getBytes(StandardCharsets.UTF_16); }
            byte[] scrambled = scramblePassword(changeBytes);
            ByteBuffer changePasswordBuffer = ByteBuffer.wrap(scrambled);
            buffers.add(changePasswordBuffer);
            offset = ByteBufferUtils.writeUInt16LE(buffer, changePassword.length(), offset);
            dataOffset += ByteBufferUtils.length(changePasswordBuffer);
        } 

        // SSPI Long
        if (sspi != null && ByteBufferUtils.length(sspi) > 0xFFFF) {
            ByteBufferUtils.writeUInt32LE(buffer, ByteBufferUtils.length(sspi), offset);
        } else {
            ByteBufferUtils.writeUInt32LE(buffer, 0, offset);
        }

        ByteBuffer data = ByteBufferUtils.concat(buffers);
        ByteBufferUtils.writeInt32LE(data, ByteBufferUtils.length(data), 0);
        this.buffer = data; 
    }

    private ByteBuffer generateRandomPhysicalAddress() {
        byte[] addr = new byte[ClientIdSize];
        new Random().nextBytes(addr);
        return ByteBuffer.wrap(addr);
    } 

    private byte[] scramblePassword(byte[] byteArray) {
        for (int i = 0; i < byteArray.length; i++) {
            byte b = byteArray[i];
            b = (byte)((b >> 4) | (b << 4));
            b ^= (byte)0xA5;
            byteArray[i] = b;
        }
        return byteArray;
    }

    private ByteBuffer getExtensionsBuffer() {
        List<ByteBuffer> buffers = new ArrayList<>();
        if (fedAuth != null) buffers.add(fedAuth.getBuffer());
        buffers.add(ByteBuffer.wrap(new byte[] { FeatureExtensionTerminator }));
        return ByteBufferUtils.concat(buffers);
    }

    @Override
    public String toString() {
        return String.format("Login7Payload[Options=%s, Flags1=%s, Flags2=%s, TypeFlags=%s, Flags1=%s, Hostname=%s, Username=%s, Password=%s, AppName=%s, ServerName=%s, LibraryName=%s, Language=%s, Database=%s, ClientId=%s, SSPI=%s, AttachDbFile=%s, ChangePassword=%s]",
                options, optionFlags1, optionFlags2, typeFlags, optionFlags1, hostname, username, password, appName, serverName, libraryName, language, database, clientId, sspi, attachDbFile, changePassword);
    }

    // setters/getters omitted for brevity; can be added as needed
}
