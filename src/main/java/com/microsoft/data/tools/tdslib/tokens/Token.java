package com.microsoft.data.tools.tdslib.tokens;

/**
 * Tds data stream token.
 */
public interface Token {
    /**
     * Type of the token.
     */
    TokenType getType();
}