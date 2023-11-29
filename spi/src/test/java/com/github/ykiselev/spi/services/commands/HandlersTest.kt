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
package com.github.ykiselev.spi.services.commands

import com.github.ykiselev.spi.services.commands.Commands.*
import com.github.ykiselev.spi.services.commands.Handlers.command
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.mockito.Mockito.times
import org.mockito.Mockito.verify

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
class HandlersTest {
    @Test
    fun shouldFailIfEmpty() {
        assertThrows(
            IllegalArgumentException::class.java
        ) { command("") { _: String -> }.run(emptyList()) }
        assertThrows(
            IllegalArgumentException::class.java
        ) { command("") { _: String, _: String -> }.run(emptyList()) }
        assertThrows(
            IllegalArgumentException::class.java
        ) { command("") { _: String, _: String, _: String -> }.run(emptyList()) }
        assertThrows(
            IllegalArgumentException::class.java
        ) { command("") { _: String, _: String, _: String, _: String -> }.run(emptyList()) }
        assertThrows(
            IllegalArgumentException::class.java
        ) {
            command("") { _: String, _: String, _: String, _: String, _: String -> }
                .run(emptyList())
        }
        assertThrows(
            IllegalArgumentException::class.java
        ) {
            command("") { _: String, _: String, _: String, _: String, _: String, _: String -> }
                .run(emptyList())
        }
    }

    @Test
    @Throws(Exception::class)
    fun consumer0() {
        val h = Mockito.mock(H0::class.java)
        val c = command("", h)
        c.run(listOf("1"))
        verify(h, times(1)).handle()
        assertThrows(IllegalArgumentException::class.java) { c.run(listOf("1", "2")) }
    }

    @Test
    @Throws(Exception::class)
    fun consumer1() {
        val h = Mockito.mock(H1::class.java)
        val c = command("c", h)
        c.run(listOf("c", "1"))
        verify(h, times(1)).handle("1")
        assertThrows(IllegalArgumentException::class.java) { c.run(listOf("1", "2", "3")) }
    }

    @Test
    @Throws(Exception::class)
    fun consumer2() {
        val h = Mockito.mock(H2::class.java)
        val c = command("c", h)
        c.run(listOf("c", "1"))
        verify(h, times(1)).handle("1", "")
        c.run(listOf("c", "1", "2"))
        verify(h, times(1)).handle("1", "2")
        assertThrows(IllegalArgumentException::class.java) { c.run(listOf("1", "2", "3", "4")) }
    }

    @Test
    @Throws(Exception::class)
    fun consumer3() {
        val h = Mockito.mock(H3::class.java)
        val c = command("c", h)
        c.run(listOf("c", "1"))
        verify(h, times(1)).handle("1", "", "")
        c.run(listOf("c", "1", "2"))
        verify(h, times(1)).handle("1", "2", "")
        c.run(listOf("c", "1", "2", "3"))
        verify(h, times(1)).handle("1", "2", "3")
        assertThrows(IllegalArgumentException::class.java) { c.run(listOf("1", "2", "3", "4", "5")) }
    }

    @Test
    @Throws(Exception::class)
    fun consumer4() {
        val h = Mockito.mock(H4::class.java)
        val c = command("c", h)
        c.run(listOf("c", "1"))
        verify(h, times(1)).handle("1", "", "", "")
        c.run(listOf("c", "1", "2"))
        verify(h, times(1)).handle("1", "2", "", "")
        c.run(listOf("c", "1", "2", "3"))
        verify(h, times(1)).handle("1", "2", "3", "")
        c.run(listOf("c", "1", "2", "3", "4"))
        verify(h, times(1)).handle("1", "2", "3", "4")
        assertThrows(IllegalArgumentException::class.java) {
            c.run(
                listOf("1", "2", "3", "4", "5", "6")
            )
        }
    }

    @Test
    @Throws(Exception::class)
    fun consumer5() {
        val h = Mockito.mock(H5::class.java)
        val c = command("c", h)
        c.run(listOf("c", "1"))
        verify(h, times(1)).handle("1", "", "", "", "")
        c.run(listOf("c", "1", "2"))
        verify(h, times(1)).handle("1", "2", "", "", "")
        c.run(listOf("c", "1", "2", "3"))
        verify(h, times(1)).handle("1", "2", "3", "", "")
        c.run(listOf("c", "1", "2", "3", "4"))
        verify(h, times(1)).handle("1", "2", "3", "4", "")
        c.run(listOf("c", "1", "2", "3", "4", "5"))
        verify(h, times(1)).handle("1", "2", "3", "4", "5")
        assertThrows(IllegalArgumentException::class.java) {
            c.run(
                listOf("1", "2", "3", "4", "5", "6", "7")
            )
        }
    }

    @Test
    @Throws(Exception::class)
    fun consumer6() {
        val h = Mockito.mock(H6::class.java)
        val c = command("c", h)
        c.run(listOf("c", "1"))
        verify(h, times(1)).handle("1", "", "", "", "", "")
        c.run(listOf("c", "1", "2"))
        verify(h, times(1)).handle("1", "2", "", "", "", "")
        c.run(listOf("c", "1", "2", "3"))
        verify(h, times(1)).handle("1", "2", "3", "", "", "")
        c.run(listOf("c", "1", "2", "3", "4"))
        verify(h, times(1)).handle("1", "2", "3", "4", "", "")
        c.run(listOf("c", "1", "2", "3", "4", "5"))
        verify(h, times(1)).handle("1", "2", "3", "4", "5", "")
        c.run(listOf("c", "1", "2", "3", "4", "5", "6"))
        verify(h, times(1)).handle("1", "2", "3", "4", "5", "6")
        assertThrows(IllegalArgumentException::class.java) {
            c.run(
                listOf("1", "2", "3", "4", "5", "6", "7", "8")
            )
        }
    }
}