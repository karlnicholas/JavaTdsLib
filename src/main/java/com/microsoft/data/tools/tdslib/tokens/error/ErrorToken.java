package com.microsoft.data.tools.tdslib.tokens.error;

import com.microsoft.data.tools.tdslib.tokens.Token;
import com.microsoft.data.tools.tdslib.tokens.TokenType;

/**
 * Error message token.
 */
public final class ErrorToken extends Token {

    private final long number;
    private final int state;
    private final int severity;
    private final String message;
    private final String serverName;
    private final String procName;
    private final long lineNumber;

    /**
     * Creates a new instance of the token.
     */
    public ErrorToken(long number, int state, int severity, String message,
                      String serverName, String procName, long lineNumber) {
        this.number = number;
        this.state = state;
        this.severity = severity;
        this.message = message;
        this.serverName = serverName;
        this.procName = procName;
        this.lineNumber = lineNumber;
    }

    @Override
    public TokenType getType() {
        return TokenType.ERROR;
    }

    public long getNumber() { return number; }
    public int getState() { return state; }
    public int getSeverity() { return severity; }
    public String getMessage() { return message; }
    public String getServerName() { return serverName; }
    public String getProcName() { return procName; }
    public long getLineNumber() { return lineNumber; }

    @Override
    public String toString() {
        return String.format("ErrorToken[Number=%d, State=%d, Severity=%d, Message=%s, ServerName=%s, ProcName=%s, LineNumber=%d]",
                number, state, severity, message, serverName, procName, lineNumber);
    }
}