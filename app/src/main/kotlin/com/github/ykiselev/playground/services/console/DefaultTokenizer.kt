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
package com.github.ykiselev.playground.services.console

import com.github.ykiselev.spi.services.commands.Tokenizer
import kotlin.math.min

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
class DefaultTokenizer : Tokenizer {

    override fun tokenize(text: String?, fromIndex: Int, result: MutableCollection<String>): Int {
        if (text.isNullOrEmpty()) {
            return 0
        }
        var i = fromIndex
        val len = text.length
        var slashes = 0
        while (i < len) {
            val ch = text[i]
            // First check for double-char sequences
            if (ch == '*' && slashes == 1) {
                // Multi-line comment found, skipp all till the closing sequence
                val end = text.indexOf("*/", i)
                require(end != -1) { "Unclosed multi-line comment!" }
                i = end + 2
                continue
            }
            if (ch == '/') {
                slashes++
                if (slashes == 2) {
                    // Single line comment found, skip all till the first of (CR, LF, end of string)
                    val lf = text.indexOf('\n', i)
                    val cr = text.indexOf('\r', i)
                    if (lf == -1 && cr == -1) {
                        i = len
                        break // the whole remaining text is a commentary
                    }
                    i = if (lf >= 0 && cr >= 0) {
                        (min(cr.toDouble(), lf.toDouble()) + 1).toInt()
                    } else if (cr >= 0) {
                        cr + 1
                    } else {
                        lf + 1
                    }
                    slashes = 0
                } else {
                    i++
                }
                continue
            }
            slashes = 0
            // Stop parsing on delimiter, CR, LF
            if (ch == ';' || ch == '\r' || ch == '\n') {
                i++
                break
            }
            // Then skip white-spaces
            if (Character.isWhitespace(ch)) {
                i++
                continue
            }
            val tokenLen: Int
            if (ch == '"' || ch == '\'') {
                i++
                if (i >= len) {
                    break
                }
                tokenLen = collectQuotedToken(text, i, ch)
                if (tokenLen > 1) {
                    result.add(text.substring(i, i + tokenLen))
                } else {
                    result.add("")
                }
                i++
            } else {
                tokenLen = collectToken(text, i)
                if (tokenLen > 0) {
                    result.add(text.substring(i, i + tokenLen))
                }
            }
            i += tokenLen
        }
        return i
    }

    private fun collectQuotedToken(seq: String, fromIndex: Int, quote: Char): Int {
        var i = fromIndex
        while (i < seq.length) {
            val nextQuote = seq.indexOf(quote, i)
            require(nextQuote != -1) { "Unpaired quote!" }
            i = nextQuote
            if (seq[nextQuote - 1] == '\\') {
                i++
            } else {
                break
            }
            i++
        }
        require(i <= seq.length) { "Unpaired quote!" }
        return i - fromIndex
    }

    private fun collectToken(seq: String, fromIndex: Int): Int {
        var i = fromIndex
        while (i < seq.length) {
            val ch = seq[i]
            if (Character.isWhitespace(ch) || ch == '/' || ch == ';') {
                break
            }
            i++
        }
        return i - fromIndex
    }
}
