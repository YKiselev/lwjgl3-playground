package com.github.ykiselev.io;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
class ByteChannelAsLinesTest {

    private static final List<String> STRINGS = Arrays.asList(
            "This is a test string.",
            "Это проверочная строка."
    );

    private static final Charset CHARSET = StandardCharsets.UTF_8;

    @Test
    public void shouldRead() throws IOException {
        try (ByteChannelAsLines lines = new ByteChannelAsLines(
                new ByteChannelFromArray(
                        STRINGS.stream()
                                .collect(Collectors.joining("\n"))
                                .getBytes(CHARSET)
                ),
                CHARSET
        )) {
            assertEquals(
                    STRINGS,
                    StreamSupport.stream(lines.spliterator(), false)
                            .collect(Collectors.toList())
            );
        }
    }
}