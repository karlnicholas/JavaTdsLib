package com.microsoft.data.tools.tdslib.tokens;

import java.io.IOException;

public interface TokenParser {
    Token parse(TokenType type, TokenStreamHandler source) throws IOException;
}