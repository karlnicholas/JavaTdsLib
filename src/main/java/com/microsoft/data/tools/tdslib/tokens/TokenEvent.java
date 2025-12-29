package com.microsoft.data.tools.tdslib.tokens;

/**
 * Token event.
 */
public final class TokenEvent {

    /**
     * The token that was received.
     */
    private Token token;

    /**
     * Indicate if the token handler should stop receiving tokens.
     */
    private boolean exit;

    public Token getToken() {
        return token;
    }

    public void setToken(Token token) {
        this.token = token;
    }

    public boolean isExit() {
        return exit;
    }

    public void setExit(boolean exit) {
        this.exit = exit;
    }
}