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

package com.github.ykiselev.services.commands;

import java.util.Collection;

/**
 * Splits passed string into list of tokens. Token can be:
 * <ul>
 * <li>Group of non-whitespace characters</li>
 * <li>Group of quoted characters (including white-spaces)</li>
 * </ul>
 * Tokens are delimited by whitespace(s): "token1 token2".<br/>
 * To treat sequence with white-spaces as a single token use single or double quotes - (") or (').
 * <pre>
 *     token1 "token2 with whitespaces" 'token3 with whitespaces'
 * </pre>
 * Single-line comments are supported. Single-line comment is started with "//" and lasting till the first CR or LF character is encountered.
 * <pre>
 *     token1 // remaining line is ignored
 *     token2 token3
 * </pre>
 * Multi-line comments are also supported:
 * <pre>
 *     /* this is multi-
 *     line comment{@literal *}/
 * </pre>
 * Comments should be ignored by tokenizer (silently stripped).<br/>
 * Note: single-line or multi-line comments inside quoted text are not stripped and are treated as a part of that text.
 *
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public interface Tokenizer {

    /**
     * Tokenizer should stop processing if group delimiter character ";" is encountered.
     *
     * @param text      the text to tokenize
     * @param fromIndex the index to start
     * @param result    the list of found tokens
     * @return the index of the last scanned character in {@code text}.
     */
    int tokenize(String text, int fromIndex, Collection<String> result);
}
