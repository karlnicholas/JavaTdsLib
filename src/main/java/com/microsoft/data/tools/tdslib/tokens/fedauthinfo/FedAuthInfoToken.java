package com.microsoft.data.tools.tdslib.tokens.fedauthinfo;

import com.microsoft.data.tools.tdslib.tokens.Token;
import com.microsoft.data.tools.tdslib.tokens.TokenType;

/**
 * Federate authentication information token.
 */
public final class FedAuthInfoToken extends Token {

    private final String spn;
    private final String stsUrl;

    /**
     * Creates a new instance of the token.
     *
     * @param spn    Service principal name.
     * @param stsUrl Token endpoint url.
     */
    public FedAuthInfoToken(String spn, String stsUrl) {
        this.spn = spn;
        this.stsUrl = stsUrl;
    }

    @Override
    public TokenType getType() {
        return TokenType.FED_AUTH_INFO;
    }

    public String getSpn() {
        return spn;
    }

    public String getStsUrl() {
        return stsUrl;
    }

    @Override
    public String toString() {
        return String.format("FedAuthInfo[SPN=%s, STSUrl=%s]", spn, stsUrl);
    }
}