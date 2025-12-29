package com.microsoft.data.tools.tdslib.tokens.doneinproc;

import com.microsoft.data.tools.tdslib.tokens.Token;
import com.microsoft.data.tools.tdslib.tokens.TokenType;

public record DoneInProcToken(int status, int currentCommand, long rowCount) implements Token {
    @Override
    public TokenType getType() {
        return TokenType.DONE_IN_PROC;
    }
}