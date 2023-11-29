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
package com.github.ykiselev.playground.services.config

import com.github.ykiselev.spi.services.configuration.ConfigurationException.VariableNotFoundException
import com.github.ykiselev.spi.services.configuration.PersistedConfiguration
import com.github.ykiselev.spi.services.configuration.values.ConfigValue
import com.github.ykiselev.spi.services.configuration.values.Values.WiredString
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import org.mockito.kotlin.*

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
class AppConfigTest {

    internal data class State(
        var var1: Int = 1,
        var var2: Boolean = true,
        var var3: Long = 3L,
        var var4: Double = 4.0,
        var var5: String = "5"
    )

    private val fileConfig = mock<FileConfig>()

    @Nested
    @DisplayName("when filled")
    inner class WhenFilled {

        private var cfg: PersistedConfiguration = AppConfig(fileConfig)

        @BeforeEach
        fun setUp() {
            val toWire = State()
            cfg.wire()
                .withInt("a.int", toWire::var1, { var1: Int -> toWire.var1 = var1 }, false)
                .withBoolean("a.boolean2", toWire::var2, { var2: Boolean -> toWire.var2 = var2 }, false)
                .withLong("a.long", toWire::var3, { var3: Long -> toWire.var3 = var3 }, false)
                .withDouble("a.double", toWire::var4, { var5: Double -> toWire.var4 = var5 }, false)
                .withString("a.string", toWire::var5, { var6: String -> toWire.var5 = var6 }, false)
                .build()
        }

        @Test
        fun shouldGetVariable() {
            assertEquals("5", cfg.root().getString("a.string"))
            assertTrue(cfg.root().getBoolean("a.boolean2"))
            assertEquals(1, cfg.root().getInt("a.int"))
            assertEquals(3L, cfg.root().getLong("a.long"))
            assertEquals(4.0, cfg.root().getDouble("a.double"))
        }

        @Test
        fun shouldKnowVariable() {
            assertTrue(cfg.root().hasVariable("a.string"))
            assertTrue(cfg.root().hasVariable("a.boolean2"))
            assertTrue(cfg.root().hasVariable("a.int"))
            assertTrue(cfg.root().hasVariable("a.long"))
            assertTrue(cfg.root().hasVariable("a.double"))
        }

        @Test
        fun shouldSetVariable() {
            cfg.root()["a.string"] = "c"
            assertEquals("c", cfg.root().getString("a.string"))
            cfg.root()["a.boolean2"] = false
            Assertions.assertFalse(cfg.root().getBoolean("a.boolean2"))
            cfg.root()["a.int"] = 5
            assertEquals(5, cfg.root().getInt("a.int"))
            cfg.root()["a.long"] = Long.MAX_VALUE
            assertEquals(Long.MAX_VALUE, cfg.root().getLong("a.long"))
            cfg.root()["a.double"] = Double.NaN
            assertEquals(Double.NaN, cfg.root().getDouble("a.double"))
        }

        @Test
        fun shouldThrowIfTypeMismatch() {
            assertThrows(ClassCastException::class.java) { cfg.root().getBoolean("a.string") }
            assertThrows(ClassCastException::class.java) { cfg.root().getLong("a.string") }
            assertThrows(ClassCastException::class.java) { cfg.root().getDouble("a.string") }
        }

        @Test
        fun shouldListAllVariablesAndConstLists() {
            assertArrayEquals(
                arrayOf(
                    "a.boolean2",
                    "a.double",
                    "a.int",
                    "a.long",
                    "a.string"
                ),
                cfg.values()
                    .map<Any>(ConfigValue::name)
                    .sorted()
                    .toArray()
            )
        }
    }

    @Test
    fun shouldRead() {
        AppConfig(fileConfig)
        verify(fileConfig, times(1)).loadAll("app.conf")
    }

    @Test
    fun shouldWrite() {
        AppConfig(fileConfig).persist()
        verify(fileConfig, times(1)).persist(
            eq("app.conf"), any<Map<String, Any>>()
        )
    }

    @Test
    fun shouldThrowIfNoVariable() {
        assertThrows(VariableNotFoundException::class.java) {
            AppConfig(fileConfig).root().getValue("a", WiredString::class.java)
        }
    }
}
