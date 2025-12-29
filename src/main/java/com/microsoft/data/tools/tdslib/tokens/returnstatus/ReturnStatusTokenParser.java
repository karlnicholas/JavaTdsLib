package com.microsoft.data.tools.tdslib.tokens.returnstatus;

import com.microsoft.data.tools.tdslib.tokens.Token;
import com.microsoft.data.tools.tdslib.tokens.TokenParser;
import com.microsoft.data.tools.tdslib.tokens.TokenStreamHandler;
import com.microsoft.data.tools.tdslib.tokens.TokenType;

import java.io.IOException;

public final class ReturnStatusTokenParser implements TokenParser {

    @Override
    public Token parse(TokenType tokenType, TokenStreamHandler h) throws IOException {
        // Read Int32 Little Endian
        int value = h.readInt32LE();
        return new ReturnStatusToken(value);
    }
}