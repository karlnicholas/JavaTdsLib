package com.microsoft.data.tools.tdslib.tokens.error;

import com.microsoft.data.tools.tdslib.tokens.Token;
import com.microsoft.data.tools.tdslib.tokens.TokenParser;
import com.microsoft.data.tools.tdslib.tokens.TokenStreamHandler;
import com.microsoft.data.tools.tdslib.tokens.TokenType;

import java.io.IOException;

public final class ErrorTokenParser implements TokenParser {

    @Override
    public Token parse(TokenType tokenType, TokenStreamHandler h) throws IOException {
        // Read Length (UShort) - total length of the token data
        // The C# code discards this (_ = ...), so we read and ignore it too.
        h.readUInt16LE();

        long number = h.readUInt32LE();
        int state = h.readUInt8();
        int severity = h.readUInt8();

        String message = h.readUsVarChar();
        String serverName = h.readBVarChar();
        String procName = h.readBVarChar();

        long lineNumber;

        // Version check logic.
        // Defaulting to modern behavior (>= 7.2) which uses 4 bytes for line number.
        // Legacy versions (SQL 2000) used 2 bytes.
        boolean isModern = true; // h.getOptions().getTdsVersion() >= V7_2

        if (isModern) {
            lineNumber = h.readUInt32LE();
        } else {
            lineNumber = h.readUInt16LE();
        }

        return new ErrorToken(number, state, severity, message, serverName, procName, lineNumber);
    }
}