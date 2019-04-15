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

package com.github.ykiselev.playground.services.assets;

import com.github.ykiselev.assets.Assets;
import com.github.ykiselev.assets.DefaultRecipe;
import com.github.ykiselev.assets.Recipe;
import com.github.ykiselev.wrap.Wraps;
import org.junit.jupiter.api.Test;

import java.io.Closeable;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public class ManagedAssetsTest {

    private Assets delegate = mock(Assets.class);

    private final ManagedAssets assets = new ManagedAssets(delegate);

    @Test
    public void shouldLoadOnce() {
        when(delegate.tryLoad(eq("a"), any(Recipe.class), eq(assets)))
                .thenReturn(Wraps.noop("A"));
        assertSame(
                assets.load("a", DefaultRecipe.of(String.class)).value(),
                assets.load("a", DefaultRecipe.of(String.class)).value()
        );
    }

    @Test
    public void shouldCloseAutoCloseables() throws Exception {
        final AutoCloseable a = mock(AutoCloseable.class);
        doReturn(Wraps.of(a))
                .when(delegate).tryLoad(eq("ac"), any(Recipe.class), eq(assets));
        assertNotNull(assets.load("ac", DefaultRecipe.of(AutoCloseable.class)));
        assets.close();
        verify(a, times(1)).close();
    }

    @Test
    public void shouldCloseCloseables() throws Exception {
        final Closeable c = mock(Closeable.class);
        doReturn(Wraps.of(c)).
                when(delegate).tryLoad(eq("c"), any(Recipe.class), eq(assets));
        assertNotNull(assets.load("c", DefaultRecipe.of(Closeable.class)));
        assets.close();
        verify(c, times(1)).close();
    }

    @Test
    public void shouldRemoveUnused() throws Exception {
        final AutoCloseable a = mock(AutoCloseable.class);
        doReturn(Wraps.of(a))
                .when(delegate).tryLoad(eq("ac"), any(Recipe.class), eq(assets));
        assets.load("ac", DefaultRecipe.of(AutoCloseable.class)).close();
        verify(a, times(1)).close();
    }

    @Test
    public void shouldReportLeaks() {
        final AutoCloseable a = mock(AutoCloseable.class);
        doReturn(Wraps.of(a))
                .when(delegate).tryLoad(eq("ac"), any(Recipe.class), eq(assets));
        assertNotNull(assets.load("ac", DefaultRecipe.of(AutoCloseable.class)));
        assertNotNull(assets.load("ac", DefaultRecipe.of(AutoCloseable.class)));
        assertThrows(RuntimeException.class, assets::close);
    }
}