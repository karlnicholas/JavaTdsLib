package com.microsoft.data.tools.tdslib.tokens.doneinproc;

import com.microsoft.data.tools.tdslib.tokens.Token;
import com.microsoft.data.tools.tdslib.tokens.TokenParser;
import com.microsoft.data.tools.tdslib.tokens.TokenStreamHandler;
import com.microsoft.data.tools.tdslib.tokens.TokenType;

import java.io.IOException;

public final class DoneInProcTokenParser implements TokenParser {

    @Override
    public Token parse(TokenType tokenType, TokenStreamHandler tokenStreamHandler) throws IOException {
        // Read Status (UShort)
        int status = tokenStreamHandler.readUInt16LE();

        // Read Current Command (UShort)
        int currentCommand = tokenStreamHandler.readUInt16LE();

        long rowCount;

        // Version check logic (defaulting to modern V7.4 behavior for translation)
        // boolean isModern = tokenStreamHandler.getOptions().getTdsVersion().getVersionValue() > TdsVersion.V7_2.getVersionValue();
        boolean isModern = true;

        if (isModern) {
            rowCount = tokenStreamHandler.readInt64LE();
        } else {
            rowCount = tokenStreamHandler.readUInt32LE();
        }

        return new DoneInProcToken(status, currentCommand, rowCount);
    }
}