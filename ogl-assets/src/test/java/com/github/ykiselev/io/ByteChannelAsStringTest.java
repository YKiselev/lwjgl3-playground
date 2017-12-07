package com.github.ykiselev.io;

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.nio.channels.Channels;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import static org.junit.Assert.assertEquals;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public class ByteChannelAsStringTest {

    private static final String STRING = "This is a test string. Это проверочная строка.";

    private static final Charset CHARSET = StandardCharsets.UTF_16LE;

    @Test
    public void shouldRead() {
        assertEquals(
                STRING,
                new ByteChannelAsString(
                        Channels.newChannel(
                                new ByteArrayInputStream(
                                        STRING.getBytes(CHARSET)
                                )
                        ),
                        CHARSET
                ).read()
        );
    }
}