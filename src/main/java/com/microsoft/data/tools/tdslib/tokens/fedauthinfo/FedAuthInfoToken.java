package com.microsoft.data.tools.tdslib.tokens.fedauthinfo;

import com.microsoft.data.tools.tdslib.tokens.Token;
import com.microsoft.data.tools.tdslib.tokens.TokenType;

public record FedAuthInfoToken(String spn, String stsUrl) implements Token {
    @Override
    public TokenType getType() {
        return TokenType.FED_AUTH_INFO;
    }
}