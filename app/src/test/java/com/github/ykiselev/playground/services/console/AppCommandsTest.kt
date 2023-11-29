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

import com.github.ykiselev.common.ThrowingRunnable
import com.github.ykiselev.common.test.ParallelRunner
import com.github.ykiselev.spi.services.commands.CommandException.*
import com.github.ykiselev.spi.services.commands.Commands
import com.github.ykiselev.spi.services.commands.Commands.H0
import com.github.ykiselev.spi.services.commands.Commands.H2
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import java.util.concurrent.ThreadLocalRandom
import java.util.function.Supplier

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
class AppCommandsTest {

    private val commands: Commands = AppCommands(DefaultTokenizer(), 1)

    @Test
    fun shouldReportUnknownCommand() {
        assertThrows(UnknownCommandException::class.java) { commands.execute("this will not work!") }
    }

    @Test
    fun shouldFail() {
        commands.add("fail", H0 { throw RuntimeException("Oops!") })
        assertThrows(CommandExecutionFailedException::class.java) { commands.execute("fail") }
    }

    @Test
    @Throws(Exception::class)
    fun shouldOverflow() {
        val h = Mockito.mock(H0::class.java)
        commands.add("overflow", h)
        val it = listOf("overflow").iterator()
        Mockito.doAnswer { _ ->
            if (it.hasNext()) {
                commands.execute(it.next())
            }
            null
        }.`when`(h).handle()
        assertThrows(CommandStackOverflowException::class.java) { commands.execute("overflow") }
        Mockito.verify(h, Mockito.times(1)).handle()
    }

    @Test
    @Throws(Exception::class)
    fun shouldExecute() {
        val h = Mockito.mock(H0::class.java)
        commands.add("cmd", h)
        commands.execute("cmd")
        Mockito.verify(h, Mockito.times(1)).handle()
    }

    @Test
    @Throws(Exception::class)
    fun shouldExecuteSeparatedBySemicolon() {
        val h = Mockito.mock(H0::class.java)
        commands.add("cmd", h)
        commands.execute("cmd;cmd")
        Mockito.verify(h, Mockito.times(2)).handle()
    }

    @Test
    @Throws(Exception::class)
    fun shouldExecuteMultiLine() {
        val h = Mockito.mock(H0::class.java)
        commands.add("cmd", h)
        commands.execute("cmd\rcmd\ncmd\r\ncmd")
        Mockito.verify(h, Mockito.times(4)).handle()
    }

    @Test
    @Throws(Exception::class)
    fun shouldPassArgs() {
        val h = Mockito.mock(H2::class.java)
        commands.add("cmd", h)
        commands.execute("cmd 1")
        Mockito.verify(h, Mockito.times(1)).handle("cmd", "1")
    }

    @Test
    @Throws(Exception::class)
    fun shouldBeThreadSafe() {
        val savedArgs = ThreadLocal<Array<String>>()
        commands.add("a") { a: String, b: String -> savedArgs.set(arrayOf(a, b)) }
        commands.add("b") { a: String, b: String -> savedArgs.set(arrayOf(a, b)) }
        val s = Supplier {
            ThrowingRunnable {
                val rnd = ThreadLocalRandom.current()
                val cmd = if (rnd.nextBoolean()) "a" else "b"
                val arg = rnd.nextLong().toString()
                commands.execute("$cmd $arg")
                Assertions.assertArrayEquals(arrayOf(cmd, arg), savedArgs.get())
            }
        }
        ParallelRunner.fromRunnable(1000, s)
            .run()
    }
}