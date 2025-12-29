package com.microsoft.data.tools.tdslib.tokens.loginack;

import com.microsoft.data.tools.tdslib.TdsVersion;
import com.microsoft.data.tools.tdslib.tokens.Token;
import com.microsoft.data.tools.tdslib.tokens.TokenType;

/**
 * Login7 response.
 */
public final class LoginAckToken extends Token {

    private final SqlInterfaceType interfaceType;
    private final TdsVersion tdsVersion;
    private final String progName;
    private final ProgVersion progVersion;

    /**
     * Creates a new instance of this token.
     */
    public LoginAckToken(SqlInterfaceType interfaceType, TdsVersion tdsVersion, String progName, ProgVersion progVersion) {
        this.interfaceType = interfaceType;
        this.tdsVersion = tdsVersion;
        this.progName = progName;
        this.progVersion = progVersion;
    }

    @Override
    public TokenType getType() {
        return TokenType.LOGIN_ACK;
    }

    public SqlInterfaceType getInterfaceType() {
        return interfaceType;
    }

    public TdsVersion getTdsVersion() {
        return tdsVersion;
    }

    public String getProgName() {
        return progName;
    }

    public ProgVersion getProgVersion() {
        return progVersion;
    }

    @Override
    public String toString() {
        return String.format("LoginAckToken[InterfaceType=%s, TdsVersion=%s, ProgName=%s, ProgVersion=%s]",
                interfaceType, tdsVersion, progName, progVersion);
    }
}