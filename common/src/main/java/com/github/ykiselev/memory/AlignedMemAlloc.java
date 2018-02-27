package com.github.ykiselev.memory;

import com.github.ykiselev.common.AbstractWrap;
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class AlignedMemAlloc extends AbstractWrap<ByteBuffer> {

    public static final int DEFAULT_ALIGNMENT = 8;

    public AlignedMemAlloc(int alignment, int size) {
        super(MemoryUtil.memAlignedAlloc(alignment, size));
    }

    public AlignedMemAlloc(int size) {
        this(DEFAULT_ALIGNMENT, size);
    }

    @Override
    public void close() {
        MemoryUtil.memAlignedFree(value());
    }
}
