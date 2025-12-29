package com.microsoft.data.tools.tdslib.tokens.done;

import com.microsoft.data.tools.tdslib.tokens.Token;
import com.microsoft.data.tools.tdslib.tokens.TokenType;

public record DoneToken(int status, int currentCommand, long rowCount) implements Token {
    @Override
    public TokenType getType() {
        return TokenType.DONE;
    }
}