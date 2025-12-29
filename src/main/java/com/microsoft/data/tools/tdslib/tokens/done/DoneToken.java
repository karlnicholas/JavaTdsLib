package com.microsoft.data.tools.tdslib.tokens.done;

import com.microsoft.data.tools.tdslib.tokens.Token;
import com.microsoft.data.tools.tdslib.tokens.TokenType;

/**
 * Indicates the completion status of a SQL statement.
 */
public class DoneToken extends Token {

    private final int status;
    private final int currentCommand;
    private final long rowCount;

    /**
     * Create a new instance with a status, current command and row count.
     *
     * @param status         Status flags (see DoneStatus).
     * @param currentCommand Current command.
     * @param rowCount       Row count.
     */
    public DoneToken(int status, int currentCommand, long rowCount) {
        this.status = status;
        this.currentCommand = currentCommand;
        this.rowCount = rowCount;
    }

    @Override
    public TokenType getType() {
        return TokenType.DONE;
    }

    public int getStatus() {
        return status;
    }

    public int getCurrentCommand() {
        return currentCommand;
    }

    public long getRowCount() {
        return rowCount;
    }

    @Override
    public String toString() {
        return String.format("DoneToken[Status=0x%04X, CurrentCommand=%d, RowCount=%d]",
                status, currentCommand, rowCount);
    }
}