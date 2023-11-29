package com.github.ykiselev.playground.services.config

import com.github.ykiselev.spi.services.configuration.ConfigurationException
import com.github.ykiselev.spi.services.configuration.PersistedConfiguration
import com.github.ykiselev.spi.services.configuration.values.ConfigValue
import com.github.ykiselev.spi.services.configuration.values.Values
import org.junit.jupiter.api.*
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
            Assertions.assertEquals("5", cfg.root().getString("a.string"))
            Assertions.assertTrue(cfg.root().getBoolean("a.boolean2"))
            Assertions.assertEquals(1, cfg.root().getInt("a.int"))
            Assertions.assertEquals(3L, cfg.root().getLong("a.long"))
            Assertions.assertEquals(4.0, cfg.root().getDouble("a.double"))
        }

        @Test
        fun shouldKnowVariable() {
            Assertions.assertTrue(cfg.root().hasVariable("a.string"))
            Assertions.assertTrue(cfg.root().hasVariable("a.boolean2"))
            Assertions.assertTrue(cfg.root().hasVariable("a.int"))
            Assertions.assertTrue(cfg.root().hasVariable("a.long"))
            Assertions.assertTrue(cfg.root().hasVariable("a.double"))
        }

        @Test
        fun shouldSetVariable() {
            cfg.root()["a.string"] = "c"
            Assertions.assertEquals("c", cfg.root().getString("a.string"))
            cfg.root()["a.boolean2"] = false
            Assertions.assertFalse(cfg.root().getBoolean("a.boolean2"))
            cfg.root()["a.int"] = 5
            Assertions.assertEquals(5, cfg.root().getInt("a.int"))
            cfg.root()["a.long"] = Long.MAX_VALUE
            Assertions.assertEquals(Long.MAX_VALUE, cfg.root().getLong("a.long"))
            cfg.root()["a.double"] = Double.NaN
            Assertions.assertEquals(Double.NaN, cfg.root().getDouble("a.double"))
        }

        @Test
        fun shouldThrowIfTypeMismatch() {
            Assertions.assertThrows(ClassCastException::class.java) { cfg.root().getBoolean("a.string") }
            Assertions.assertThrows(ClassCastException::class.java) { cfg.root().getLong("a.string") }
            Assertions.assertThrows(ClassCastException::class.java) { cfg.root().getDouble("a.string") }
        }

        @Test
        fun shouldListAllVariablesAndConstLists() {
            Assertions.assertArrayEquals(
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
        Assertions.assertThrows(ConfigurationException.VariableNotFoundException::class.java) {
            AppConfig(fileConfig).root().getValue("a", Values.WiredString::class.java)
        }
    }
}