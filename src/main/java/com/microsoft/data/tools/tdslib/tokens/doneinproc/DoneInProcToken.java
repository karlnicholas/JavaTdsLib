package com.microsoft.data.tools.tdslib.tokens.doneinproc;

import com.microsoft.data.tools.tdslib.tokens.TokenType;
import com.microsoft.data.tools.tdslib.tokens.done.DoneToken;

/**
 * Token indicating the completion status of a statement in a procedure.
 */
public final class DoneInProcToken extends DoneToken {

    /**
     * Create a new instance with a status, current command and row count.
     *
     * @param status         Status flags.
     * @param currentCommand Current command.
     * @param rowCount       Row count.
     */
    public DoneInProcToken(int status, int currentCommand, long rowCount) {
        super(status, currentCommand, rowCount);
    }

    @Override
    public TokenType getType() {
        return TokenType.DONE_IN_PROC;
    }

    @Override
    public String toString() {
        return String.format("DoneInProcToken[Status=0x%04X, CurrentCommand=%d, RowCount=%d]",
                getStatus(), getCurrentCommand(), getRowCount());
    }
}