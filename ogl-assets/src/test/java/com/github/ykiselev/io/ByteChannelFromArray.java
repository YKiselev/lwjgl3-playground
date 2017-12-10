package com.github.ykiselev.io;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class ByteChannelFromArray implements ReadableByteChannel {

    private final ReadableByteChannel channel;

    public ByteChannelFromArray(ReadableByteChannel channel) {
        this.channel = channel;
    }

    public ByteChannelFromArray(byte[] data) {
        this(Channels.newChannel(new ByteArrayInputStream(data)));
    }

    @Override
    public int read(ByteBuffer dst) throws IOException {
        return channel.read(dst);
    }

    @Override
    public boolean isOpen() {
        return channel.isOpen();
    }

    @Override
    public void close() throws IOException {
        channel.close();
    }
}
