package com.github.ykiselev.memory;

import com.github.ykiselev.common.Wrap;
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class MemAlloc extends Wrap<ByteBuffer> {

    public MemAlloc(int size) {
        super(MemoryUtil.memAlloc(size));
    }

    @Override
    public void close() {
        MemoryUtil.memFree(value());
    }
}
