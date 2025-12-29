package com.microsoft.data.tools.tdslib.tokens.returnstatus;

import com.microsoft.data.tools.tdslib.tokens.Token;
import com.microsoft.data.tools.tdslib.tokens.TokenType;

public record ReturnStatusToken(int value) implements Token {
    @Override
    public TokenType getType() {
        return TokenType.RETURN_STATUS;
    }
}