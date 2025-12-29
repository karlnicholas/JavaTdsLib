package com.microsoft.data.tools.tdslib.tokens.info;

import com.microsoft.data.tools.tdslib.tokens.Token;
import com.microsoft.data.tools.tdslib.tokens.TokenType;

public record InfoToken(
        long number,
        int state,
        int severity,
        String message,
        String serverName,
        String procName,
        long lineNumber
) implements Token {
    @Override
    public TokenType getType() {
        return TokenType.INFO;
    }
}