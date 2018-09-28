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

import java.util.Collection;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class DefaultTokenizer implements Tokenizer {

    @Override
    public void tokenize(String commandLine, Collection<String> result) {
        if (commandLine == null || commandLine.isEmpty()) {
            return;
        }
        int i = 0, len = commandLine.length();
        boolean quoted;
        while (i < len) {
            while (i < len && Character.isWhitespace(commandLine.charAt(i))) {
                i++;
            }
            if (i >= len) {
                break;
            }
            quoted = false;
            if (commandLine.charAt(i) == '"') {
                quoted = true;
                i++;
            }
            if (i >= len) {
                break;
            }
            int tokenLen;
            if (quoted) {
                tokenLen = collectQuotedToken(commandLine, i);
                if (tokenLen > 1) {
                    result.add(commandLine.substring(i, i + tokenLen));
                } else {
                    result.add("");
                }
                i++;
            } else {
                tokenLen = collectToken(commandLine, i);
                if (tokenLen > 0) {
                    result.add(commandLine.substring(i, i + tokenLen));
                }
            }
            i += tokenLen;
        }
    }

    private int collectQuotedToken(String seq, int fromIndex) {
        int i = fromIndex;
        for (; i < seq.length(); i++) {
            final int nextQuote = seq.indexOf('"', i);
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
            if (Character.isWhitespace(seq.charAt(i))) {
                break;
            }
        }
        return i - fromIndex;
    }
}
