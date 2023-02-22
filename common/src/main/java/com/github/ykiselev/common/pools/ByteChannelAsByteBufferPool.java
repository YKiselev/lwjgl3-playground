package com.github.ykiselev.common.pools;

import com.github.ykiselev.common.io.ByteChannelAsByteBuffer;
import com.github.ykiselev.common.memory.pool.ObjectPool;
import com.github.ykiselev.common.memory.pool.ObjectPoolFrame;
import com.github.ykiselev.wrap.Wrap;

import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;

public final class ByteChannelAsByteBufferPool {

    private static final ThreadLocal<ObjectPool<ByteChannelAsByteBuffer>> TLS = ThreadLocal.withInitial(() ->
            new ObjectPool<>(ByteChannelAsByteBuffer::new));

    public static ObjectPoolFrame<ByteChannelAsByteBuffer> push() {
        return TLS.get().push();
    }

    public static Wrap<ByteBuffer> read(ReadableByteChannel channel) {
        try (var pool = ByteChannelAsByteBufferPool.push()) {
            return pool.allocate().read(channel);
        }
    }
}
