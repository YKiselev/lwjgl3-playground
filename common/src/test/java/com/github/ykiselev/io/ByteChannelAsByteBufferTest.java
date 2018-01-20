package com.github.ykiselev.io;

import com.github.ykiselev.common.Wrap;
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
class ByteChannelAsByteBufferTest {

    private static final int CHANNEL_LENGTH = 8 * 4 * 1_000;

    private final ReadableByteChannel channel = mock(ReadableByteChannel.class);

    private final ReadableBytes bc = new ByteChannelAsByteBuffer(
            channel, 256
    );

    @Test
    void shouldRead() throws IOException {
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