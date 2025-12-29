package com.microsoft.data.tools.tdslib.tokens.envchange;

import com.microsoft.data.tools.tdslib.tokens.Token;
import com.microsoft.data.tools.tdslib.tokens.TokenType;

/**
 * Environment change token.
 *
 * @param <T> Type of value.
 */
public abstract class EnvChangeToken<T> extends Token {

    private final T oldValue;
    private final T newValue;

    protected EnvChangeToken(T oldValue, T newValue) {
        this.oldValue = oldValue;
        this.newValue = newValue;
    }

    @Override
    public TokenType getType() {
        return TokenType.ENV_CHANGE;
    }

    public abstract EnvChangeTokenSubType getSubType();

    public T getOldValue() {
        return oldValue;
    }

    public T getNewValue() {
        return newValue;
    }

    @Override
    public String toString() {
        return String.format("EnvChangeToken[SubType=%s, NewValue=%s, OldValue=%s]",
                getSubType(), newValue, oldValue);
    }
}