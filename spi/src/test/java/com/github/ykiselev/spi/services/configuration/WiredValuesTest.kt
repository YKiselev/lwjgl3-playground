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
package com.github.ykiselev.spi.services.configuration

import com.github.ykiselev.spi.services.configuration.values.ConfigValue
import com.github.ykiselev.spi.services.configuration.values.LongFormat
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mockito.kotlin.*
import java.util.concurrent.atomic.AtomicInteger


/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
class WiredValuesTest {

    private val cfg = mock<PersistedConfiguration> {
        on { wire(any()) } doReturn mock<AutoCloseable> {}
    }

    private var captor = argumentCaptor<Collection<ConfigValue>>()

    @Test
    fun shouldWireIntAndCheckForOverflow() {
        val value = AtomicInteger(1)
        WiredValues(cfg)
            .withInt(
                "a",
                { value.get() },
                { newValue: Int -> value.set(newValue) },
                false,
                LongFormat.DECIMAL
            ).build()
        verify(cfg, times(1)).wire(captor.capture())
        val r = captor.firstValue
        val v = r.iterator().next()
        assertEquals("a", v.name)
        //assertThrows(ArithmeticException::class.java) { v.setString("999999999999999999") }
        v.setString("2694881535")
    }
}