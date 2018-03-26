package com.github.ykiselev.lwjgl3.assets;

import com.github.ykiselev.assets.Assets;
import org.junit.jupiter.api.Test;

import java.io.Closeable;
import java.util.Optional;

import static org.junit.Assert.assertSame;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
class ManagedAssetsTest {

    private Assets delegate = mock(Assets.class);

    private final ManagedAssets assets = new ManagedAssets(delegate);

    @Test
    void shouldLoadOnce() {
        when(delegate.tryLoad(eq("a"), eq(String.class), eq(assets)))
                .thenReturn(Optional.of("A"));
        assertSame(
                assets.load("a", String.class),
                assets.load("a", String.class)
        );
    }

    @Test
    void shouldCloseAutoCloseables() throws Exception {
        final AutoCloseable a = mock(AutoCloseable.class);
        when(delegate.tryLoad(eq("ac"), eq(AutoCloseable.class), eq(assets)))
                .thenReturn(Optional.of(a));
        assets.load("ac", AutoCloseable.class).close();
        assets.close();
        verify(a, atLeast(1)).close();
    }

    @Test
    void shouldCloseCloseables() throws Exception {
        final Closeable c = mock(Closeable.class);
        when(delegate.tryLoad(eq("c"), eq(Closeable.class), eq(assets)))
                .thenReturn(Optional.of(c));
        assets.load("c", Closeable.class).close();
        assets.close();
        verify(c, atLeast(1)).close();
    }

}