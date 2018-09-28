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

package com.github.ykiselev.lwjgl3.services.console;

import com.github.ykiselev.services.commands.Tokenizer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
class DefaultTokenizerTest {

    private final List<String> parts = new ArrayList<>();

    private final Tokenizer processor = new DefaultTokenizer();

    private final Consumer<String> consumer = v -> {
        int fromIndex = 0;
        final int len = v != null ? v.length() : 0;
        while (fromIndex < len) {
            fromIndex = processor.tokenize(v, fromIndex, parts);
        }
    };

    private void assertEquals(String... v) {
        assertArrayEquals(v, parts.toArray());
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " ", "\t", "   "})
    void shouldParseEmpty(String value) {
        consumer.accept(value);
        assertTrue(parts.isEmpty());
    }

    @Test
    void shouldParseNull() {
        consumer.accept(null);
        assertTrue(parts.isEmpty());
    }

    @ParameterizedTest
    @ValueSource(strings = {" token ", "token ", " token", "token"})
    void shouldParseSingleToken(String value) {
        consumer.accept(value);
        assertEquals("token");
    }

    @Test
    void shouldParseTwoTokens() {
        consumer.accept(" first \t second ");
        assertEquals("first", "second");
    }

    @ParameterizedTest
    @ValueSource(strings = {" \" first \t second \"", "\" first \t second \"", "\" first \t second \"", "' first \t second '"})
    void shouldParseQuotedTokens(String value) {
        consumer.accept(value);
        assertEquals(" first \t second ");
    }

    @ParameterizedTest
    @ValueSource(strings = {"simple \"quoted tokens\"", " simple \"quoted tokens\"", "simple \"quoted tokens\" ", " simple \t\"quoted tokens\" "})
    void shouldParseMixed(String value) {
        consumer.accept(value);
        assertEquals("simple", "quoted tokens");
    }

    @ParameterizedTest
    @ValueSource(strings = {"1;2", " 1 ;2", "1; 2", " 1 \t; \t2  "})
    void shouldParseDelimitedBySemicolon(String value) {
        consumer.accept(value);
        assertEquals("1", "2");
    }

    @ParameterizedTest
    @ValueSource(strings = {"a b c //d", "a//xxx\rb//yyy\nc"})
    void shouldStripSingleLineComments(String value) {
        consumer.accept(value);
        assertEquals("a", "b", "c");
    }

    @Test
    void shouldNotStripSingleLineCommentInsideQuotedText() {
        consumer.accept("a \"b c //d\"");
        assertEquals("a", "b c //d");
    }

    @ParameterizedTest
    @ValueSource(strings = {"a b c /* d */", "a/*xxx*/ b/*yyy*/ c"})
    void shouldStripMultiLineComments(String value) {
        consumer.accept(value);
        assertEquals("a", "b", "c");
    }

    @Test
    void shouldNotStripMultiLineCommentInsideQuotedText() {
        consumer.accept("a \"b c /* d */\"");
        assertEquals("a", "b c /* d */");
    }

    @Test
    void shouldFailIfUnclosedMultiLineComment() {
        assertThrows(IllegalArgumentException.class, () -> consumer.accept("a b c /* d"));
    }
}