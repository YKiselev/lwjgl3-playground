package com.github.ykiselev.io;

import org.junit.jupiter.api.Test;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
class ByteChannelAsStringTest {

    private static final String STRING = "This is a test string. Это проверочная строка.";

    private static final Charset CHARSET = StandardCharsets.UTF_16LE;

    @Test
    void shouldRead() {
        assertEquals(
                STRING,
                new ByteChannelAsString(
                        new ByteChannelFromArray(
                                STRING.getBytes(CHARSET)
                        ),
                        CHARSET
                ).read()
        );
    }
}