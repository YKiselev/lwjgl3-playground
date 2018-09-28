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

package com.github.ykiselev.lwjgl3.services.console;

import com.github.ykiselev.services.commands.CommandException.CommandExecutionFailedException;
import com.github.ykiselev.services.commands.CommandException.CommandStackOverflowException;
import com.github.ykiselev.services.commands.CommandException.UnknownCommandException;
import com.github.ykiselev.services.commands.Commands;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
class AppCommandsTest {

    private final Commands commands = new AppCommands(new DefaultTokenizer(), 1);

    @Test
    void shouldReportUnknownCommand() {
        assertThrows(UnknownCommandException.class, () ->
                commands.execute("this will not work!"));
    }

    @Test
    void shouldFail() {
        commands.add("fail", () -> {
            throw new RuntimeException("Oops!");
        });
        assertThrows(CommandExecutionFailedException.class, () ->
                commands.execute("fail now"));
    }

    @Test
    void shouldOverflow() {
        commands.add("overflow", () -> commands.execute("overflow"));
        assertThrows(CommandStackOverflowException.class, () ->
                commands.execute("overflow"));
    }

    @Test
    void shouldExecute() {
        Runnable h = mock(Runnable.class);
        commands.add("cmd", h);
        commands.execute("cmd");
        verify(h, times(1)).run();
    }

    @Test
    void shouldExecuteSeparatedBySemicolon() {
        Runnable h = mock(Runnable.class);
        commands.add("cmd", h);
        commands.execute("cmd;cmd");
        verify(h, times(2)).run();
    }

}