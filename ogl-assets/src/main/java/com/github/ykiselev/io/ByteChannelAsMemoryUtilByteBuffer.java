package com.github.ykiselev.io;

import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;

import static java.util.Objects.requireNonNull;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class ByteChannelAsMemoryUtilByteBuffer implements ByteChannelAsByteBuffer {

    private final ReadableByteChannel channel;

    private final int initialBufferSize;

    public ByteChannelAsMemoryUtilByteBuffer(ReadableByteChannel channel, int initialBufferSize) {
        this.channel = requireNonNull(channel);
        this.initialBufferSize = initialBufferSize;
    }

    @Override
    public ByteBuffer read() {
        ByteBuffer buffer = MemoryUtil.memAlloc(initialBufferSize);
        try {
            for (; ; ) {
                final int result = channel.read(buffer);
                if (result == -1) {
                    break;
                }
                if (!buffer.hasRemaining()) {
                    buffer = MemoryUtil.memRealloc(buffer, buffer.capacity() * 3 / 2);
                }
            }
            return buffer;
        } catch (Exception ex) {
            MemoryUtil.memFree(buffer);
            throw new IllegalStateException(ex);
        }
    }
}
