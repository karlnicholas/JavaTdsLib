package com.microsoft.data.tools.tdslib.io;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;

/**
 * A wrapper channel that delegates to an inner channel.
 * Allows swapping the inner channel dynamically.
 */
public class WrappedChannel implements ByteChannel {

    private ByteChannel innerChannel;

    public WrappedChannel(ByteChannel innerChannel) {
        this.innerChannel = innerChannel;
    }

    public void setInnerChannel(ByteChannel innerChannel) {
        this.innerChannel = innerChannel;
    }

    public ByteChannel getInnerChannel() {
        return innerChannel;
    }

    @Override
    public int read(ByteBuffer dst) throws IOException {
        return innerChannel.read(dst);
    }

    @Override
    public int write(ByteBuffer src) throws IOException {
        return innerChannel.write(src);
    }

    @Override
    public boolean isOpen() {
        return innerChannel.isOpen();
    }

    @Override
    public void close() throws IOException {
        innerChannel.close();
    }
}