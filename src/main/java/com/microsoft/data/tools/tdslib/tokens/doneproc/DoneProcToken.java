package com.microsoft.data.tools.tdslib.tokens.doneproc;

import com.microsoft.data.tools.tdslib.tokens.TokenType;
import com.microsoft.data.tools.tdslib.tokens.done.DoneToken;

/**
 * Statement in a procedure done.
 */
public final class DoneProcToken extends DoneToken {

    /**
     * Create a new instance with a status, current command and row count.
     *
     * @param status         Status flags (see DoneStatus).
     * @param currentCommand Current command.
     * @param rowCount       Row count.
     */
    public DoneProcToken(int status, int currentCommand, long rowCount) {
        super(status, currentCommand, rowCount);
    }

    @Override
    public TokenType getType() {
        return TokenType.DONE_PROC;
    }

    @Override
    public String toString() {
        return String.format("DoneProcToken[Status=0x%04X, CurrentCommand=%d, RowCount=%d]",
                getStatus(), getCurrentCommand(), getRowCount());
    }
}