package com.github.ykiselev.memory;

import com.github.ykiselev.common.AbstractWrap;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class MemAllocFloat extends AbstractWrap<FloatBuffer> {

    public MemAllocFloat(int size) {
        super(MemoryUtil.memAllocFloat(size));
    }

    @Override
    public void close() {
        MemoryUtil.memFree(value());
    }
}
