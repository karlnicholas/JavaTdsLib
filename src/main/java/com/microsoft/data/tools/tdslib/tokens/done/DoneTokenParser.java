package com.microsoft.data.tools.tdslib.tokens.done;

import com.microsoft.data.tools.tdslib.TdsVersion;
import com.microsoft.data.tools.tdslib.tokens.Token;
import com.microsoft.data.tools.tdslib.tokens.TokenParser;
import com.microsoft.data.tools.tdslib.tokens.TokenStreamHandler;
import com.microsoft.data.tools.tdslib.tokens.TokenType;

import java.io.IOException;

public final class DoneTokenParser implements TokenParser {

    @Override
    public Token parse(TokenType tokenType, TokenStreamHandler tokenStreamHandler) throws IOException {
        // Read Status (UShort)
        int status = tokenStreamHandler.readUInt16LE();

        // Read Current Command (UShort)
        int currentCommand = tokenStreamHandler.readUInt16LE();

        long rowCount;

        // C# logic: if (Options.TdsVersion > V7_2) ...
        // Java translation assumes connection uses modern TDS version (7.4) by default.
        // If you need to support ancient SQL Server 2000, you would uncomment the version check logic below.

        // boolean isModern = tokenStreamHandler.getOptions().getTdsVersion().getVersionValue() > TdsVersion.V7_2.getVersionValue();
        boolean isModern = true; // Defaulting to modern for this translation

        if (isModern) {
            rowCount = tokenStreamHandler.readInt64LE(); // C# ReadUInt64LE (Java long handles bits same way)
        } else {
            rowCount = tokenStreamHandler.readUInt32LE();
        }

        return new DoneToken(status, currentCommand, rowCount);
    }
}