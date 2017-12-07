package com.github.ykiselev.io;

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.nio.channels.Channels;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public class ByteChannelAsLinesTest {

    private static final List<String> STRINGS = Arrays.asList(
            "This is a test string.",
            "Это проверочная строка."
    );

    private static final Charset CHARSET = StandardCharsets.UTF_8;

    @Test
    public void shouldRead() {
        final List<String> result = new ArrayList<>();
        new ByteChannelAsLines(
                Channels.newChannel(
                        new ByteArrayInputStream(
                                STRINGS.stream()
                                        .collect(Collectors.joining("\n"))
                                        .getBytes(CHARSET)
                        )
                ),
                CHARSET
        ).consume(result::add);
        assertEquals(STRINGS, result);
    }
}