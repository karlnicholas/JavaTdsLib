package com.microsoft.data.tools.tdslib.tokens.info;

import com.microsoft.data.tools.tdslib.tokens.Token;
import com.microsoft.data.tools.tdslib.tokens.TokenParser;
import com.microsoft.data.tools.tdslib.tokens.TokenStreamHandler;
import com.microsoft.data.tools.tdslib.tokens.TokenType;

import java.io.IOException;

public final class InfoTokenParser implements TokenParser {

    @Override
    public Token parse(TokenType tokenType, TokenStreamHandler h) throws IOException {
        // Read Length (UShort) - discard
        h.readUInt16LE();

        long number = h.readUInt32LE();
        int state = h.readUInt8();
        int severity = h.readUInt8();

        String message = h.readUsVarChar();
        String serverName = h.readBVarChar();
        String procName = h.readBVarChar();

        long lineNumber;

        // Version Check: Default to modern (V7.2+)
        // boolean isModern = h.getOptions().getTdsVersion().getValue() >= TdsVersion.V7_2.getValue();
        boolean isModern = true;

        if (isModern) {
            lineNumber = h.readUInt32LE();
        } else {
            lineNumber = h.readUInt16LE();
        }

        return new InfoToken(number, state, severity, message, serverName, procName, lineNumber);
    }
}