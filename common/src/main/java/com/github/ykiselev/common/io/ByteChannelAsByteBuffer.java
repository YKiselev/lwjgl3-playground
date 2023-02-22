/*
 * Copyright 2017 Yuriy Kiselev (uze@yandex.ru)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.ykiselev.common.io;

import com.github.ykiselev.wrap.Wrap;
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class ByteChannelAsByteBuffer implements ReadableBytes {

    public static final int INITIAL_SIZE = 8 * 1024;

    private final int initialBufferSize;

    public ByteChannelAsByteBuffer() {
        this(INITIAL_SIZE);
    }

    public ByteChannelAsByteBuffer(int initialBufferSize) {
        this.initialBufferSize = initialBufferSize;
    }

    @Override
    public Wrap<ByteBuffer> read(ReadableByteChannel channel) {
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
            buffer.flip();
            return new MemoryUtilWrap(buffer);
        } catch (Exception ex) {
            MemoryUtil.memFree(buffer);
            throw new IllegalStateException(ex);
        }
    }

    private static final class MemoryUtilWrap extends Wrap<ByteBuffer> {

        MemoryUtilWrap(ByteBuffer value) {
            super(value);
        }

        @Override
        public void close() {
            MemoryUtil.memFree(value());
        }
    }
}
