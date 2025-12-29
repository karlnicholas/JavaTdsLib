package com.microsoft.data.tools.tdslib.tokens.returnstatus;

import com.microsoft.data.tools.tdslib.tokens.Token;
import com.microsoft.data.tools.tdslib.tokens.TokenType;

/**
 * Return status of an RPC or TSQL exec query.
 */
public final class ReturnStatusToken extends Token {

    private final int value;

    /**
     * Create a new instance of this token with a status value.
     *
     * @param value Return status value.
     */
    public ReturnStatusToken(int value) {
        this.value = value;
    }

    @Override
    public TokenType getType() {
        return TokenType.RETURN_STATUS;
    }

    public int getValue() {
        return value;
    }

    @Override
    public String toString() {
        return String.format("ReturnStatusToken[Value=%d]", value);
    }
}