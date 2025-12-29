package com.microsoft.data.tools.tdslib.payloads;

import java.nio.ByteBuffer;

/**
 * Raw message payload.
 */
public class RawPayload extends Payload {

    /**
     * Create a new instance of this class.
     *
     * @param byteBuffer Buffer with the payload data.
     */
    public RawPayload(ByteBuffer byteBuffer) {
        // Use the setter defined in the abstract Payload class
        // (which also enforces Little-Endian ordering)
        setBuffer(byteBuffer);
    }

    /**
     * Builds the payload buffer.
     */
    @Override
    protected void buildBufferInternal() {
        // no operation
    }

    /**
     * Gets a human readable string representation of this object.
     *
     * @return Human readable string representation.
     */
    @Override
    public String toString() {
        // Note: Standard java.nio.ByteBuffer.toString() prints state (pos/lim/cap),
        // not the content. If the C# version prints content, we will need a HexDump helper.
        return "RawPayload[buffer=" + getBuffer() + "]";
    }

}