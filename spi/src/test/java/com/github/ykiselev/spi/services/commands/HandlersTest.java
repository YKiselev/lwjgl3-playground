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

package com.github.ykiselev.spi.services.commands;

import com.github.ykiselev.common.ThrowingRunnable;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public class HandlersTest {

    @Test
    public void shouldFailIfEmpty() {
        assertThrows(IllegalArgumentException.class, () -> Handlers.command("", a1 -> {
                }).run(Collections.emptyList())
        );
        assertThrows(IllegalArgumentException.class, () -> Handlers.command("", (a1, a2) -> {
                }).run(Collections.emptyList())
        );
        assertThrows(IllegalArgumentException.class, () -> Handlers.command("", (a1, a2, a3) -> {
                }).run(Collections.emptyList())
        );
        assertThrows(IllegalArgumentException.class, () -> Handlers.command("", (a1, a2, a3, a4) -> {
                }).run(Collections.emptyList())
        );
        assertThrows(IllegalArgumentException.class, () -> Handlers.command("", (a1, a2, a3, a4, a5) -> {
                }).run(Collections.emptyList())
        );
        assertThrows(IllegalArgumentException.class, () -> Handlers.command("", (a1, a2, a3, a4, a5, a6) -> {
                }).run(Collections.emptyList())
        );
    }

    @Test
    public void consumer0() throws Exception {
        ThrowingRunnable h = mock(ThrowingRunnable.class);
        Command c = Handlers.command("", h);

        c.run(Collections.singletonList("1"));
        verify(h, times(1)).run();

        assertThrows(IllegalArgumentException.class, () -> c.run(Arrays.asList("1", "2")));
    }

    @Test
    public void consumer1() throws Exception {
        Commands.H1 h = mock(Commands.H1.class);
        Command c = Handlers.command("", h);

        c.run(Collections.singletonList("1"));
        verify(h, times(1)).handle("1");

        assertThrows(IllegalArgumentException.class, () -> c.run(Arrays.asList("1", "2")));
    }

    @Test
    public void consumer2() throws Exception {
        Commands.H2 h = mock(Commands.H2.class);
        Command c = Handlers.command("", h);

        c.run(Collections.singletonList("1"));
        verify(h, times(1)).handle("1", null);

        c.run(Arrays.asList("1", "2"));
        verify(h, times(1)).handle("1", "2");

        assertThrows(IllegalArgumentException.class, () -> c.run(Arrays.asList("1", "2", "3")));
    }

    @Test
    public void consumer3() throws Exception {
        Commands.H3 h = mock(Commands.H3.class);
        Command c = Handlers.command("", h);

        c.run(Collections.singletonList("1"));
        verify(h, times(1)).handle("1", null, null);

        c.run(Arrays.asList("1", "2"));
        verify(h, times(1)).handle("1", "2", null);

        c.run(Arrays.asList("1", "2", "3"));
        verify(h, times(1)).handle("1", "2", "3");

        assertThrows(IllegalArgumentException.class, () -> c.run(Arrays.asList("1", "2", "3", "4")));
    }

    @Test
    public void consumer4() throws Exception {
        Commands.H4 h = mock(Commands.H4.class);
        Command c = Handlers.command("", h);

        c.run(Collections.singletonList("1"));
        verify(h, times(1)).handle("1", null, null, null);

        c.run(Arrays.asList("1", "2"));
        verify(h, times(1)).handle("1", "2", null, null);

        c.run(Arrays.asList("1", "2", "3"));
        verify(h, times(1)).handle("1", "2", "3", null);

        c.run(Arrays.asList("1", "2", "3", "4"));
        verify(h, times(1)).handle("1", "2", "3", "4");

        assertThrows(IllegalArgumentException.class, () -> c.run(Arrays.asList("1", "2", "3", "4", "5")));
    }

    @Test
    public void consumer5() throws Exception {
        Commands.H5 h = mock(Commands.H5.class);
        Command c = Handlers.command("", h);

        c.run(Collections.singletonList("1"));
        verify(h, times(1)).handle("1", null, null, null, null);

        c.run(Arrays.asList("1", "2"));
        verify(h, times(1)).handle("1", "2", null, null, null);

        c.run(Arrays.asList("1", "2", "3"));
        verify(h, times(1)).handle("1", "2", "3", null, null);

        c.run(Arrays.asList("1", "2", "3", "4"));
        verify(h, times(1)).handle("1", "2", "3", "4", null);

        c.run(Arrays.asList("1", "2", "3", "4", "5"));
        verify(h, times(1)).handle("1", "2", "3", "4", "5");

        assertThrows(IllegalArgumentException.class, () -> c.run(Arrays.asList("1", "2", "3", "4", "5", "6")));
    }

    @Test
    public void consumer6() throws Exception {
        Commands.H6 h = mock(Commands.H6.class);
        Command c = Handlers.command("", h);

        c.run(Collections.singletonList("1"));
        verify(h, times(1)).handle("1", null, null, null, null, null);

        c.run(Arrays.asList("1", "2"));
        verify(h, times(1)).handle("1", "2", null, null, null, null);

        c.run(Arrays.asList("1", "2", "3"));
        verify(h, times(1)).handle("1", "2", "3", null, null, null);

        c.run(Arrays.asList("1", "2", "3", "4"));
        verify(h, times(1)).handle("1", "2", "3", "4", null, null);

        c.run(Arrays.asList("1", "2", "3", "4", "5"));
        verify(h, times(1)).handle("1", "2", "3", "4", "5", null);

        c.run(Arrays.asList("1", "2", "3", "4", "5", "6"));
        verify(h, times(1)).handle("1", "2", "3", "4", "5", "6");

        assertThrows(IllegalArgumentException.class, () -> c.run(Arrays.asList("1", "2", "3", "4", "5", "6", "7")));
    }

}