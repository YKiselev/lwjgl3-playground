package com.github.ykiselev.playground.services.console

import com.github.ykiselev.common.test.ParallelRunner
import com.github.ykiselev.spi.services.commands.Tokenizer
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import java.util.concurrent.Callable

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
class DefaultTokenizerTest {

    private val parts: MutableList<String> = mutableListOf()
    private val processor: Tokenizer = DefaultTokenizer()
    private val consumer = { v: String? -> parse(v, parts) }

    private fun assertEquals(vararg v: String) {
        Assertions.assertArrayEquals(v, parts.toTypedArray())
    }

    private fun parse(text: String?, parts: MutableList<String>) {
        var fromIndex = 0
        val len = text?.length ?: 0
        while (fromIndex < len) {
            fromIndex = processor.tokenize(text, fromIndex, parts)
        }
    }

    @ParameterizedTest
    @ValueSource(strings = ["", " ", "\t", "   "])
    fun shouldParseEmpty(value: String?) {
        consumer(value)
        Assertions.assertTrue(parts.isEmpty())
    }

    @Test
    fun shouldParseNull() {
        consumer(null)
        Assertions.assertTrue(parts.isEmpty())
    }

    @ParameterizedTest
    @ValueSource(strings = [" token ", "token ", " token", "token"])
    fun shouldParseSingleToken(value: String?) {
        consumer(value)
        assertEquals("token")
    }

    @Test
    fun shouldParseTwoTokens() {
        consumer(" first \t second ")
        assertEquals("first", "second")
    }

    @ParameterizedTest
    @ValueSource(strings = [" \" first \t second \"", "\" first \t second \"", "\" first \t second \"", "' first \t second '"])
    fun shouldParseQuotedTokens(value: String?) {
        consumer(value)
        assertEquals(" first \t second ")
    }

    @ParameterizedTest
    @ValueSource(strings = ["simple \"quoted tokens\"", " simple \"quoted tokens\"", "simple \"quoted tokens\" ", " simple \t\"quoted tokens\" "])
    fun shouldParseMixed(value: String?) {
        consumer(value)
        assertEquals("simple", "quoted tokens")
    }

    @ParameterizedTest
    @ValueSource(strings = ["1;2", " 1 ;2", "1; 2", " 1 \t; \t2  "])
    fun shouldParseDelimitedBySemicolon(value: String?) {
        consumer(value)
        assertEquals("1", "2")
    }

    @ParameterizedTest
    @ValueSource(strings = ["a b c //d", "a//xxx\rb//yyy\nc"])
    fun shouldStripSingleLineComments(value: String?) {
        consumer(value)
        assertEquals("a", "b", "c")
    }

    @Test
    fun shouldNotStripSingleLineCommentInsideQuotedText() {
        consumer("a \"b c //d\"")
        assertEquals("a", "b c //d")
    }

    @ParameterizedTest
    @ValueSource(strings = ["a b c /* d */", "a/*xxx*/ b/*yyy*/ c"])
    fun shouldStripMultiLineComments(value: String?) {
        consumer(value)
        assertEquals("a", "b", "c")
    }

    @Test
    fun shouldNotStripMultiLineCommentInsideQuotedText() {
        consumer("a \"b c /* d */\"")
        assertEquals("a", "b c /* d */")
    }

    @Test
    fun shouldFailIfUnclosedMultiLineComment() {
        Assertions.assertThrows(IllegalArgumentException::class.java) { consumer("a b c /* d") }
    }

    @Test
    fun shouldBeThreadSafe() {
        val inputs = arrayOf(
            "a b c d e f",
            "1 2 3 4 5 6",
            "a 1 b 2 c 3",
            "x y z 3 2 1"
        )
        val outputs = arrayOf(
            arrayOf("a", "b", "c", "d", "e", "f"),
            arrayOf("1", "2", "3", "4", "5", "6"),
            arrayOf("a", "1", "b", "2", "c", "3"),
            arrayOf("x", "y", "z", "3", "2", "1")
        )
        val s = {
            object : Callable<Unit> {
                private val tokens: MutableList<String> = ArrayList()
                override fun call() {
                    for (i in inputs.indices) {
                        tokens.clear()
                        parse(inputs[i], tokens)
                        Assertions.assertArrayEquals(outputs[i], tokens.toTypedArray())
                    }
                }
            }
        }
        ParallelRunner(1000, s, s, s, s)
            .call()
    }
}