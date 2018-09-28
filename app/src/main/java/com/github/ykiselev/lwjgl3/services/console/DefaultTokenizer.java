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

import java.util.Collection;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class DefaultTokenizer implements Tokenizer {

    @Override
    public int tokenize(String text, int fromIndex, Collection<String> result) {
        if (text == null || text.isEmpty()) {
            return 0;
        }
        int i = fromIndex, len = text.length(), slashes = 0;
        while (i < len) {
            final char ch = text.charAt(i);
            // First check for double-char sequences
            if (ch == '*' && slashes == 1) {
                // Multi-line comment found, skipp all till the closing sequence
                final int end = text.indexOf("*/", i);
                if (end == -1) {
                    throw new IllegalArgumentException("Unclosed multi-line comment!");
                }
                i = end + 2;
                continue;
            }
            if (ch == '/') {
                slashes++;
                if (slashes == 2) {
                    // Single line comment found, skip all till the first of (CR, LF, end of string)
                    final int lf = text.indexOf('\n', i);
                    final int cr = text.indexOf('\r', i);
                    if (lf == -1 && cr == -1) {
                        i = len;
                        break; // the whole remaining text is a commentary
                    }
                    if (lf >= 0 && cr >= 0) {
                        i = Math.min(cr, lf) + 1;
                    } else if (cr >= 0) {
                        i = cr + 1;
                    } else {
                        i = lf + 1;
                    }
                    slashes = 0;
                } else {
                    i++;
                }
                continue;
            }
            slashes = 0;
            // Stop parsing on delimiter, CR, LF
            if (ch == ';' || ch == '\r' || ch == '\n') {
                i++;
                break;
            }
            // Then skip white-spaces
            if (Character.isWhitespace(ch)) {
                i++;
                continue;
            }
            final int tokenLen;
            if (ch == '"' || ch == '\'') {
                i++;
                if (i >= len) {
                    break;
                }
                tokenLen = collectQuotedToken(text, i, ch);
                if (tokenLen > 1) {
                    result.add(text.substring(i, i + tokenLen));
                } else {
                    result.add("");
                }
                i++;
            } else {
                tokenLen = collectToken(text, i);
                if (tokenLen > 0) {
                    result.add(text.substring(i, i + tokenLen));
                }
            }
            i += tokenLen;
        }
        return i;
    }

    private int collectQuotedToken(String seq, int fromIndex, char quote) {
        int i = fromIndex;
        for (; i < seq.length(); i++) {
            final int nextQuote = seq.indexOf(quote, i);
            if (nextQuote == -1) {
                throw new IllegalArgumentException("Unpaired quote!");
            } else {
                i = nextQuote;
                if (seq.charAt(nextQuote - 1) == '\\') {
                    i++;
                } else {
                    break;
                }
            }
        }
        if (i > seq.length()) {
            throw new IllegalArgumentException("Unpaired quote!");
        }
        return i - fromIndex;
    }

    private int collectToken(String seq, int fromIndex) {
        int i = fromIndex;
        for (; i < seq.length(); i++) {
            final char ch = seq.charAt(i);
            if (Character.isWhitespace(ch) || ch == '/' || ch == ';') {
                break;
            }
        }
        return i - fromIndex;
    }
}
