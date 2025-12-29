package com.microsoft.data.tools.tdslib.tokens.doneproc;

import com.microsoft.data.tools.tdslib.tokens.Token;
import com.microsoft.data.tools.tdslib.tokens.TokenType;

public record DoneProcToken(int status, int currentCommand, long rowCount) implements Token {
    @Override
    public TokenType getType() {
        return TokenType.DONE_PROC;
    }
}