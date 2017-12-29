package com.github.ykiselev.memory;

import com.github.ykiselev.common.Wrap;
import org.lwjgl.system.MemoryUtil;

import java.nio.ShortBuffer;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class MemAllocShort extends Wrap<ShortBuffer> {

    public MemAllocShort(int size) {
        super(MemoryUtil.memAllocShort(size));
    }

    @Override
    public void close() {
        MemoryUtil.memFree(value());
    }
}
