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

package com.github.ykiselev.playground.services.console;

import com.github.ykiselev.common.ThrowingRunnable;
import com.github.ykiselev.services.commands.CommandException.CommandExecutionFailedException;
import com.github.ykiselev.services.commands.CommandException.CommandStackOverflowException;
import com.github.ykiselev.services.commands.CommandException.UnknownCommandException;
import com.github.ykiselev.services.commands.Commands;
import com.github.ykiselev.test.ParallelRunner;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Collections;
import java.util.Iterator;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public class AppCommandsTest {

    private final Commands commands = new AppCommands(new DefaultTokenizer(), 1);

    @Test
    public void shouldReportUnknownCommand() {
        assertThrows(UnknownCommandException.class, () ->
                commands.execute("this will not work!"));
    }

    @Test
    public void shouldFail() {
        commands.add("fail", () -> {
            throw new RuntimeException("Oops!");
        });
        assertThrows(CommandExecutionFailedException.class, () ->
                commands.execute("fail"));
    }

    @Test
    public void shouldOverflow() throws Exception {
        ThrowingRunnable h = Mockito.mock(ThrowingRunnable.class);
        commands.add("overflow", h);

        final Iterator<String> it = Collections.singletonList("overflow").iterator();
        doAnswer(invocation -> {
            if (it.hasNext()) {
                commands.execute(it.next());
            }
            return null;
        }).when(h).run();

        assertThrows(CommandStackOverflowException.class, () ->
                commands.execute("overflow"));

        verify(h, times(1)).run();
    }

    @Test
    public void shouldExecute() throws Exception {
        ThrowingRunnable h = mock(ThrowingRunnable.class);
        commands.add("cmd", h);
        commands.execute("cmd");
        verify(h, times(1)).run();
    }

    @Test
    public void shouldExecuteSeparatedBySemicolon() throws Exception {
        ThrowingRunnable h = mock(ThrowingRunnable.class);
        commands.add("cmd", h);
        commands.execute("cmd;cmd");
        verify(h, times(2)).run();
    }

    @Test
    public void shouldExecuteMultiLine() throws Exception {
        ThrowingRunnable h = mock(ThrowingRunnable.class);
        commands.add("cmd", h);
        commands.execute("cmd\rcmd\ncmd\r\ncmd");
        verify(h, times(4)).run();
    }

    @Test
    public void shouldPassArgs() throws Exception {
        Commands.H2 h = mock(Commands.H2.class);
        commands.add("cmd", h);
        commands.execute("cmd 1");
        verify(h, times(1)).handle("cmd", "1");
    }

    @Test
    public void shouldBeThreadSafe() throws Exception {
        final ThreadLocal<String[]> savedArgs = new ThreadLocal<>();
        commands.add("a", (a, b) -> savedArgs.set(new String[]{a, b}));
        commands.add("b", (a, b) -> savedArgs.set(new String[]{a, b}));
        Supplier<ThrowingRunnable> s = () -> () -> {
            ThreadLocalRandom rnd = ThreadLocalRandom.current();
            String cmd = rnd.nextBoolean() ? "a" : "b";
            String arg = Long.toString(rnd.nextLong());
            commands.execute(cmd + " " + arg);
            assertArrayEquals(new String[]{cmd, arg}, savedArgs.get());
        };
        ParallelRunner.fromRunnable(1000, s)
                .call();
    }
}