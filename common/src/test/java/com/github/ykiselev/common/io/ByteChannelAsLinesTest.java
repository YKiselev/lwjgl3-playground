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
public class ByteChannelAsLinesTest {

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