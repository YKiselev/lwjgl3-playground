package com.github.ykiselev.playground.services.assets

import com.github.ykiselev.assets.Assets
import com.github.ykiselev.assets.DefaultRecipe
import com.github.ykiselev.assets.Recipe
import com.github.ykiselev.wrap.Wrap
import com.github.ykiselev.wrap.Wraps
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.mockito.kotlin.*
import java.io.Closeable

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
class ManagedAssetsTest {

    private val delegate = mock<Assets>()
    private val assets = ManagedAssets(delegate)

    @Test
    fun shouldLoadOnce() {
        Mockito.`when`<Wrap<*>?>(
            delegate.tryLoad(
                eq("a"), any<Recipe<Any, Any, Any>>(), eq(assets)
            )
        )
            .thenReturn(Wraps.noop("A"))
        Assertions.assertSame(
            assets.load("a", DefaultRecipe.of(String::class.java)).value(),
            assets.load("a", DefaultRecipe.of(String::class.java)).value()
        )
    }

    @Test
    fun shouldCloseAutoCloseables() {
        val a = mock<AutoCloseable>()
        doReturn(Wraps.of(a))
            .`when`(delegate).tryLoad(
                eq("ac"), any<Recipe<Any, Any, Any>>(), eq(assets)
            )
        assets.load(
            "ac", DefaultRecipe.of(AutoCloseable::class.java)
        ).close()
        assets.close()
        verify(a, times(1)).close()
    }

    @Test
    fun shouldCloseCloseables() {
        val c = mock<Closeable>()
        doReturn(Wraps.of(c)).`when`(delegate).tryLoad(
            eq("c"), any<Recipe<Any, Any, Any>>(), eq(assets)
        )
        assets.load("c", DefaultRecipe.of(Closeable::class.java)).close()
        assets.close()
        verify(c, times(1)).close()
    }

    @Test
    fun shouldRemoveUnused() {
        val a = mock<AutoCloseable>()
        doReturn(Wraps.of(a))
            .`when`(delegate).tryLoad(
                eq("ac"), any<Recipe<Any, Any, Any>>(), eq(assets)
            )
        assets.load(
            "ac", DefaultRecipe.of(AutoCloseable::class.java)
        ).close()
        verify(a, times(1)).close()
    }

    @Test
    fun shouldReportLeaks() {
        val a = mock<AutoCloseable>()
        doReturn(Wraps.of(a))
            .`when`(delegate).tryLoad(
                eq("ac"), any<Recipe<Any, Any, Any>>(), eq(assets)
            )
        Assertions.assertNotNull(
            assets.load(
                "ac", DefaultRecipe.of(AutoCloseable::class.java)
            )
        )
        Assertions.assertNotNull(
            assets.load(
                "ac", DefaultRecipe.of(AutoCloseable::class.java)
            )
        )
        Assertions.assertThrows(RuntimeException::class.java) { assets.close() }
    }
}