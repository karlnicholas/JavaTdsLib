package com.microsoft.data.tools.tdslib.tokens.envchange;

import com.microsoft.data.tools.tdslib.tokens.Token;
import com.microsoft.data.tools.tdslib.tokens.TokenParser;
import com.microsoft.data.tools.tdslib.tokens.TokenStreamHandler;
import com.microsoft.data.tools.tdslib.tokens.TokenType;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public final class EnvChangeTokenParser implements TokenParser {

    @FunctionalInterface
    private interface SubTypeParser {
        Token parse(TokenStreamHandler handler) throws IOException;
    }

    private static final Map<EnvChangeTokenSubType, SubTypeParser> subTypeParsers = new HashMap<>();

    static {
        // Database
        subTypeParsers.put(EnvChangeTokenSubType.DATABASE, h -> {
            String newVal = h.readBVarChar();
            String oldVal = h.readBVarChar();
            return new DatabaseEnvChangeToken(oldVal, newVal);
        });

        // Packet Size (String to Int parsing)
        subTypeParsers.put(EnvChangeTokenSubType.PACKET_SIZE, h -> {
            String newValStr = h.readBVarChar();
            String oldValStr = h.readBVarChar();
            int newVal = newValStr.isEmpty() ? 0 : Integer.parseInt(newValStr);
            int oldVal = oldValStr.isEmpty() ? 0 : Integer.parseInt(oldValStr);
            return new PacketSizeEnvChangeToken(oldVal, newVal);
        });

        // Character Set
        subTypeParsers.put(EnvChangeTokenSubType.CHARACTER_SET, h -> {
            String newVal = h.readBVarChar();
            String oldVal = h.readBVarChar();
            return new CharsetEnvChangeToken(oldVal, newVal);
        });

        // Database Mirroring
        subTypeParsers.put(EnvChangeTokenSubType.DATABASE_MIRRORING_PARTNER, h -> {
            String newVal = h.readBVarChar();
            String oldVal = h.readBVarChar();
            return new DatabaseMirroringPartnerEnvChangeToken(oldVal, newVal);
        });

        // Language
        subTypeParsers.put(EnvChangeTokenSubType.LANGUAGE, h -> {
            String newVal = h.readBVarChar();
            String oldVal = h.readBVarChar();
            return new LanguageEnvChangeToken(oldVal, newVal);
        });

        // SQL Collation (Bytes)
        subTypeParsers.put(EnvChangeTokenSubType.SQL_COLLATION, h -> {
            ByteBuffer newVal = h.readBVarByte();
            ByteBuffer oldVal = h.readBVarByte();
            return new SqlCollationEnvChangeToken(oldVal, newVal);
        });

        // Begin Transaction
        subTypeParsers.put(EnvChangeTokenSubType.BEGIN_TRANSACTION, h -> {
            ByteBuffer newVal = h.readBVarByte();
            ByteBuffer oldVal = h.readBVarByte();
            return new BeginTransactionEnvChangeToken(oldVal, newVal);
        });

        // Commit Transaction
        subTypeParsers.put(EnvChangeTokenSubType.COMMIT_TRANSACTION, h -> {
            ByteBuffer newVal = h.readBVarByte();
            ByteBuffer oldVal = h.readBVarByte();
            return new CommitTransactionEnvChangeToken(oldVal, newVal);
        });

        // Rollback Transaction
        subTypeParsers.put(EnvChangeTokenSubType.ROLLBACK_TRANSACTION, h -> {
            ByteBuffer newVal = h.readBVarByte();
            ByteBuffer oldVal = h.readBVarByte();
            return new RollbackTransactionEnvChangeToken(oldVal, newVal);
        });

        // Reset Connection
        subTypeParsers.put(EnvChangeTokenSubType.RESET_CONNECTION, h -> {
            ByteBuffer newVal = h.readBVarByte();
            ByteBuffer oldVal = h.readBVarByte();
            return new ResetConnectionEnvChangeToken(oldVal, newVal);
        });

        // Routing (Complex)
        subTypeParsers.put(EnvChangeTokenSubType.ROUTING, EnvChangeTokenParser::parseRoutingEnvChange);
    }

    @Override
    public Token parse(TokenType tokenType, TokenStreamHandler tokenStreamHandler) throws IOException {
        // Read Length (UShort) - total length of this token data
        int length = tokenStreamHandler.readUInt16LE();

        // Read SubType (Byte)
        int subTypeByte = tokenStreamHandler.readUInt8();
        EnvChangeTokenSubType subType = EnvChangeTokenSubType.fromValue(subTypeByte);

        if (subType == null || !subTypeParsers.containsKey(subType)) {
            // If unknown, we must skip the rest of the data based on 'length' minus the byte we just read
            // But 'length' included the subType byte? Usually in TDS, length follows the token header.
            // Actually, for EnvChange: [Token][Length][SubType][Data...]
            // So we can consume the remaining data if we don't know how to parse it.
            int remaining = length - 1;
            if (remaining > 0) {
                tokenStreamHandler.readBuffer(remaining);
            }
            throw new IOException("Unsupported EnvChange Token SubType: " + subTypeByte);
        }

        return subTypeParsers.get(subType).parse(tokenStreamHandler);
    }

    private static Token parseRoutingEnvChange(TokenStreamHandler h) throws IOException {
        // Read NEW value
        int length = h.readUInt16LE();
        ByteBuffer buffer = h.readBuffer(length); // Read raw bytes for parsing
        buffer.order(ByteOrder.LITTLE_ENDIAN);

        // Parse Buffer
        byte protocol = buffer.get();
        int port = Short.toUnsignedInt(buffer.getShort(1));
        int serverLen = Short.toUnsignedInt(buffer.getShort(3));

        // Extract String (Unicode)
        byte[] serverBytes = new byte[serverLen * 2];
        // Slice from offset 5
        buffer.position(5);
        buffer.get(serverBytes);
        String server = new String(serverBytes, StandardCharsets.UTF_16LE);

        RoutingInfo newInfo = new RoutingInfo(protocol, port, server);

        // Read OLD value
        length = h.readUInt16LE();
        RoutingInfo oldInfo = null;

        if (length > 0) {
            buffer = h.readBuffer(length);
            buffer.order(ByteOrder.LITTLE_ENDIAN);

            protocol = buffer.get();
            port = Short.toUnsignedInt(buffer.getShort(1));
            serverLen = Short.toUnsignedInt(buffer.getShort(3));

            serverBytes = new byte[serverLen * 2];
            buffer.position(5);
            buffer.get(serverBytes);
            server = new String(serverBytes, StandardCharsets.UTF_16LE);

            oldInfo = new RoutingInfo(protocol, port, server);
        }

        return new RoutingEnvChangeToken(oldInfo, newInfo);
    }
}