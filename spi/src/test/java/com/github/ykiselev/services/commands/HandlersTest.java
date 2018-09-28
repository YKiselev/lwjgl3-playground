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

package com.github.ykiselev.services.commands;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
class HandlersTest {

    @Test
    void shouldFailIfEmpty() {
        assertThrows(IllegalArgumentException.class, () -> Handlers.consumer(a1 -> {
                }).accept(Collections.emptyList())
        );
        assertThrows(IllegalArgumentException.class, () -> Handlers.consumer((a1, a2) -> {
                }).accept(Collections.emptyList())
        );
        assertThrows(IllegalArgumentException.class, () -> Handlers.consumer((a1, a2, a3) -> {
                }).accept(Collections.emptyList())
        );
        assertThrows(IllegalArgumentException.class, () -> Handlers.consumer((a1, a2, a3, a4) -> {
                }).accept(Collections.emptyList())
        );
        assertThrows(IllegalArgumentException.class, () -> Handlers.consumer((a1, a2, a3, a4, a5) -> {
                }).accept(Collections.emptyList())
        );
        assertThrows(IllegalArgumentException.class, () -> Handlers.consumer((a1, a2, a3, a4, a5, a6) -> {
                }).accept(Collections.emptyList())
        );
    }

    @Test
    void consumer0() {
        Runnable h = mock(Runnable.class);
        Consumer<List<String>> c = Handlers.consumer(h);

        c.accept(Collections.singletonList("1"));
        verify(h, times(1)).run();

        assertThrows(IllegalArgumentException.class, () -> c.accept(Arrays.asList("1", "2")));
    }

    @Test
    void consumer1() {
        Commands.H1 h = mock(Commands.H1.class);
        Consumer<List<String>> c = Handlers.consumer(h);

        c.accept(Collections.singletonList("1"));
        verify(h, times(1)).handle("1");

        assertThrows(IllegalArgumentException.class, () -> c.accept(Arrays.asList("1", "2")));
    }

    @Test
    void consumer2() {
        Commands.H2 h = mock(Commands.H2.class);
        Consumer<List<String>> c = Handlers.consumer(h);

        c.accept(Collections.singletonList("1"));
        verify(h, times(1)).handle("1", null);

        c.accept(Arrays.asList("1", "2"));
        verify(h, times(1)).handle("1", "2");

        assertThrows(IllegalArgumentException.class, () -> c.accept(Arrays.asList("1", "2", "3")));
    }

    @Test
    void consumer3() {
        Commands.H3 h = mock(Commands.H3.class);
        Consumer<List<String>> c = Handlers.consumer(h);

        c.accept(Collections.singletonList("1"));
        verify(h, times(1)).handle("1", null, null);

        c.accept(Arrays.asList("1", "2"));
        verify(h, times(1)).handle("1", "2", null);

        c.accept(Arrays.asList("1", "2", "3"));
        verify(h, times(1)).handle("1", "2", "3");

        assertThrows(IllegalArgumentException.class, () -> c.accept(Arrays.asList("1", "2", "3", "4")));
    }

    @Test
    void consumer4() {
        Commands.H4 h = mock(Commands.H4.class);
        Consumer<List<String>> c = Handlers.consumer(h);

        c.accept(Collections.singletonList("1"));
        verify(h, times(1)).handle("1", null, null, null);

        c.accept(Arrays.asList("1", "2"));
        verify(h, times(1)).handle("1", "2", null, null);

        c.accept(Arrays.asList("1", "2", "3"));
        verify(h, times(1)).handle("1", "2", "3", null);

        c.accept(Arrays.asList("1", "2", "3", "4"));
        verify(h, times(1)).handle("1", "2", "3", "4");

        assertThrows(IllegalArgumentException.class, () -> c.accept(Arrays.asList("1", "2", "3", "4", "5")));
    }

    @Test
    void consumer5() {
        Commands.H5 h = mock(Commands.H5.class);
        Consumer<List<String>> c = Handlers.consumer(h);

        c.accept(Collections.singletonList("1"));
        verify(h, times(1)).handle("1", null, null, null, null);

        c.accept(Arrays.asList("1", "2"));
        verify(h, times(1)).handle("1", "2", null, null, null);

        c.accept(Arrays.asList("1", "2", "3"));
        verify(h, times(1)).handle("1", "2", "3", null, null);

        c.accept(Arrays.asList("1", "2", "3", "4"));
        verify(h, times(1)).handle("1", "2", "3", "4", null);

        c.accept(Arrays.asList("1", "2", "3", "4", "5"));
        verify(h, times(1)).handle("1", "2", "3", "4", "5");

        assertThrows(IllegalArgumentException.class, () -> c.accept(Arrays.asList("1", "2", "3", "4", "5", "6")));
    }

    @Test
    void consumer6() {
        Commands.H6 h = mock(Commands.H6.class);
        Consumer<List<String>> c = Handlers.consumer(h);

        c.accept(Collections.singletonList("1"));
        verify(h, times(1)).handle("1", null, null, null, null, null);

        c.accept(Arrays.asList("1", "2"));
        verify(h, times(1)).handle("1", "2", null, null, null, null);

        c.accept(Arrays.asList("1", "2", "3"));
        verify(h, times(1)).handle("1", "2", "3", null, null, null);

        c.accept(Arrays.asList("1", "2", "3", "4"));
        verify(h, times(1)).handle("1", "2", "3", "4", null, null);

        c.accept(Arrays.asList("1", "2", "3", "4", "5"));
        verify(h, times(1)).handle("1", "2", "3", "4", "5", null);

        c.accept(Arrays.asList("1", "2", "3", "4", "5", "6"));
        verify(h, times(1)).handle("1", "2", "3", "4", "5", "6");

        assertThrows(IllegalArgumentException.class, () -> c.accept(Arrays.asList("1", "2", "3", "4", "5", "6", "7")));
    }

}