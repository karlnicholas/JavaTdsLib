package com.microsoft.data.tools.tdslib.tokens.loginack;

import com.microsoft.data.tools.tdslib.TdsVersion;
import com.microsoft.data.tools.tdslib.tokens.Token;
import com.microsoft.data.tools.tdslib.tokens.TokenParser;
import com.microsoft.data.tools.tdslib.tokens.TokenStreamHandler;
import com.microsoft.data.tools.tdslib.tokens.TokenType;

import java.io.IOException;

public final class LoginAckTokenParser implements TokenParser {

    @Override
    public Token parse(TokenType tokenType, TokenStreamHandler h) throws IOException {
        // Read Length (UShort) - discard
        h.readUInt16LE();

        // Interface Type (1 byte)
        byte typeByte = (byte) h.readUInt8();
        SqlInterfaceType interfaceType = SqlInterfaceType.fromValue(typeByte);

        // TDS Version (UInt32 Big Endian)
        long versionLong = h.readUInt32BE();
        TdsVersion tdsVersion = TdsVersion.fromValue((int) versionLong);
        if (tdsVersion == null) {
            // Fallback or throw? C# throws.
            // But we might want to be lenient if it's a newer version than we know.
            // For now, let's just default to the raw int if we can't map it, or throw.
            throw new IOException("Unknown Tds Version: " + Long.toHexString(versionLong));
        }

        // Program Name (BVarChar)
        String progName = h.readBVarChar();

        // Program Version (4 bytes: Major, Minor, BuildHi, BuildLo)
        byte major = (byte) h.readUInt8();
        byte minor = (byte) h.readUInt8();
        byte buildHi = (byte) h.readUInt8();
        byte buildLow = (byte) h.readUInt8();
        ProgVersion progVersion = new ProgVersion(major, minor, buildHi, buildLow);

        return new LoginAckToken(interfaceType, tdsVersion, progName, progVersion);
    }
}