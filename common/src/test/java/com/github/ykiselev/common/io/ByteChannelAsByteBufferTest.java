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
import org.junit.jupiter.api.Test;
import org.mockito.stubbing.Answer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public class ByteChannelAsByteBufferTest {

    private static final int CHANNEL_LENGTH = 8 * 4 * 1_000;

    private final ReadableByteChannel channel = mock(ReadableByteChannel.class);

    private final ReadableBytes bc = new ByteChannelAsByteBuffer(
            channel, 256
    );

    @Test
    public void shouldRead() throws IOException {
        when(channel.read(any(ByteBuffer.class)))
                .then((Answer<Integer>) invocation -> {
                    final ByteBuffer buffer = invocation.getArgument(0);
                    int result = 0;
                    while (buffer.position() < CHANNEL_LENGTH && buffer.hasRemaining() && result < 100) {
                        buffer.put((byte) 123);
                        result++;
                    }
                    if (result == 0 && buffer.position() >= CHANNEL_LENGTH) {
                        result = -1;
                    }
                    return result;
                });
        try (Wrap<ByteBuffer> wrap = bc.read()) {
            assertEquals(CHANNEL_LENGTH, wrap.value().limit());
        }
    }
}