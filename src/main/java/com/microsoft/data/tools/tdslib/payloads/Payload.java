package com.microsoft.data.tools.tdslib.payloads;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Payload for messages.
 */
public abstract class Payload {

    /**
     * The buffer with the payload data.
     * <p>
     * Mapped to java.nio.ByteBuffer.
     * We explicitly handle Endianness here because TDS is Little-Endian.
     */
    protected ByteBuffer buffer;

    /**
     * Gets the buffer with the payload data.
     *
     * @return The ByteBuffer containing the payload.
     */
    public ByteBuffer getBuffer() {
        return this.buffer;
    }

    /**
     * Sets the buffer.
     * (Emulates C# 'protected set')
     *
     * @param buffer The new buffer.
     */
    protected void setBuffer(ByteBuffer buffer) {
        this.buffer = buffer;
        // TDS protocol is strictly Little Endian. Java defaults to Big Endian.
        // We enforce it here to prevent subtle bugs later.
        if (this.buffer != null) {
            this.buffer.order(ByteOrder.LITTLE_ENDIAN);
        }
    }

    /**
     * Internally builds the payload buffer.
     */
    protected abstract void buildBufferInternal();

    /**
     * Builds the payload buffer.
     *
     * @return Buffer with the payload raw data.
     */
    public ByteBuffer buildBuffer() {
        buildBufferInternal();
        return getBuffer();
    }
}