package com.microsoft.data.tools.tdslib.tokens.loginack;

import com.microsoft.data.tools.tdslib.TdsVersion;
import com.microsoft.data.tools.tdslib.tokens.Token;
import com.microsoft.data.tools.tdslib.tokens.TokenType;

public record LoginAckToken(
        SqlInterfaceType interfaceType,
        TdsVersion tdsVersion,
        String progName,
        ProgVersion progVersion
) implements Token {
    @Override
    public TokenType getType() {
        return TokenType.LOGIN_ACK;
    }
}