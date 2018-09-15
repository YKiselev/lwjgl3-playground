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

package com.github.ykiselev.lwjgl3.services.assets;

import com.github.ykiselev.assets.Assets;
import com.github.ykiselev.wrap.Wraps;
import org.junit.jupiter.api.Test;

import java.io.Closeable;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
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
                .thenReturn(Wraps.simple("A"));
        assertSame(
                assets.load("a", String.class).value(),
                assets.load("a", String.class).value()
        );
    }

    @Test
    void shouldCloseAutoCloseables() throws Exception {
        final AutoCloseable a = mock(AutoCloseable.class);
        when(delegate.tryLoad(eq("ac"), eq(AutoCloseable.class), eq(assets)))
                .thenReturn(Wraps.of(a));
        assertNotNull(assets.load("ac", AutoCloseable.class));
        assets.close();
        verify(a, times(1)).close();
    }

    @Test
    void shouldCloseCloseables() throws Exception {
        final Closeable c = mock(Closeable.class);
        when(delegate.tryLoad(eq("c"), eq(Closeable.class), eq(assets)))
                .thenReturn(Wraps.of(c));
        assertNotNull(assets.load("c", Closeable.class));
        assets.close();
        verify(c, times(1)).close();
    }

    @Test
    void shouldRemoveUnused() throws Exception {
        final AutoCloseable a = mock(AutoCloseable.class);
        when(delegate.tryLoad(eq("ac"), eq(AutoCloseable.class), eq(assets)))
                .thenReturn(Wraps.of(a));
        assets.load("ac", AutoCloseable.class).close();
        verify(a, times(1)).close();
    }

    @Test
    void shouldReportLeaks() {
        final AutoCloseable a = mock(AutoCloseable.class);
        when(delegate.tryLoad(eq("ac"), eq(AutoCloseable.class), eq(assets)))
                .thenReturn(Wraps.of(a));
        assertNotNull(assets.load("ac", AutoCloseable.class));
        assertNotNull(assets.load("ac", AutoCloseable.class));
        assertThrows(RuntimeException.class, assets::close);
    }
}