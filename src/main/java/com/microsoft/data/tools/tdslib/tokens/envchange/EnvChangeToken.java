package com.microsoft.data.tools.tdslib.tokens.envchange;

import com.microsoft.data.tools.tdslib.tokens.Token;
import com.microsoft.data.tools.tdslib.tokens.TokenType;

/**
 * Environment change token interface.
 * @param <T> Type of value.
 */
public interface EnvChangeToken<T> extends Token {

    EnvChangeTokenSubType getSubType(); // We implement this manually, so "get" is fine

    T oldValue(); // Matches Record accessor
    T newValue(); // Matches Record accessor

    @Override
    default TokenType getType() {
        return TokenType.ENV_CHANGE;
    }
}