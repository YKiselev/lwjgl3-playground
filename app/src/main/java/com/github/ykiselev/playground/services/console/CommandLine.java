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

import com.github.ykiselev.circular.ArrayCircularBuffer;
import com.github.ykiselev.circular.CircularBuffer;
import com.github.ykiselev.opengl.sprites.Colors;
import com.github.ykiselev.services.Services;
import com.github.ykiselev.services.commands.CommandException;
import com.github.ykiselev.services.commands.Commands;
import com.github.ykiselev.services.layers.DrawingContext;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Supplier;

import static java.util.Objects.requireNonNull;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class CommandLine {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final StringBuilder buf = new StringBuilder();

    private final CircularBuffer<String> history;

    private final Services services;

    private int cursorPos;

    private int historyIndex;

    /**
     * Command fragment copied from {@code buf} at the start of history search
     */
    private String commandFragment;

    public CommandLine(Services services, int historySize) {
        this.services = requireNonNull(services);
        this.history = new ArrayCircularBuffer<>(String.class, historySize);
    }

    /**
     * Adds character at the cursor position.<p/>
     * Note: only BMP code points are currently supported.
     *
     * @param codePoint the Unicode code point (UTF-32)
     */
    void add(int codePoint) {
        buf.insert(cursorPos, (char) codePoint);
        cursorPos++;
        // Reset history search
        commandFragment = null;
    }

    /**
     * Removes previous character (BackSpace key)
     */
    void removeLeft() {
        if (cursorPos > 0 && cursorPos - 1 < buf.length()) {
            buf.delete(cursorPos - 1, cursorPos);
            cursorPos--;
        }
    }

    /**
     * Removes character at cursor position (Del key)
     */
    public void remove() {
        if (cursorPos < buf.length()) {
            buf.delete(cursorPos, cursorPos + 1);
        }
    }

    /**
     * Moves cursor one char left
     */
    void left() {
        if (cursorPos > 0) {
            cursorPos--;
        }
    }

    /**
     * Moves cursor one char right
     */
    void right() {
        if (cursorPos < buf.length()) {
            cursorPos++;
        }
    }

    void draw(DrawingContext ctx, int x0, int y0, int width, int height, int color) {
        ctx.draw(x0, y0 + ctx.font().height(), width, buf, color);
        final int cursorWidth = ctx.font().width("_");
        final float brightness = (float) Math.sin(6 * GLFW.glfwGetTime());
        ctx.draw(x0 + cursorWidth * cursorPos, y0 + ctx.font().height(), width, "_", Colors.fade(color, brightness));
    }

    /**
     * Moves cursor to the beginning of the command line
     */
    void begin() {
        cursorPos = 0;
    }

    /**
     * Moves the cursor right after the last command line character
     */
    void end() {
        cursorPos = buf.length();
    }

    /**
     * Tries to complete entered command
     */
    void complete() {
        // todo
    }

    private void reset() {
        buf.setLength(0);
        cursorPos = 0;
        historyIndex = 0;
        commandFragment = null;
    }

    private void addToHistory(String commandLine) {
        if (!history.isEmpty()) {
            final String previous = history.get(history.count() - 1);
            if (previous.equals(commandLine)) {
                return;
            }
        }
        history.write(requireNonNull(commandLine));
    }

    /**
     * Executes current command line (if not empty).
     */
    void execute() {
        if (buf.length() == 0) {
            return;
        }
        final String commandLine = buf.toString();
        reset();
        try {
            services.resolve(Commands.class)
                    .execute(commandLine);
            addToHistory(commandLine);
        } catch (CommandException ex) {
            logger.error("Command failed: {}", ex.toString());
        }
    }

    /**
     * @return the previous history command or {@code null} if oldest command was already returned.
     */
    private String prevHistory() {
        if (historyIndex > history.count()) {
            historyIndex = history.count();
        }
        if (historyIndex > 0) {
            return history.get(--historyIndex);
        }
        return null;
    }

    /**
     * @return the next history command or empty string if latest command was already returned.
     */
    private String nextHistory() {
        if (historyIndex < 0) {
            historyIndex = -1;
        }
        if (historyIndex + 1 < history.count()) {
            return history.get(++historyIndex);
        }
        return "";
    }

    private void searchHistory(Supplier<String> supplier) {
        if (commandFragment == null) {
            commandFragment = buf.toString();
            historyIndex = history.count();
        }
        for (; ; ) {
            final String cmd = supplier.get();
            if (cmd == null) {
                return;
            }
            if (!cmd.isEmpty() && !commandFragment.isEmpty()) {
                if (!cmd.startsWith(commandFragment)) {
                    continue;
                }
            }
            buf.setLength(0);
            buf.append(cmd);
            cursorPos = buf.length();
            break;
        }
    }

    void searchHistoryBackward() {
        searchHistory(this::prevHistory);
    }

    void searchHistory() {
        searchHistory(this::nextHistory);
    }
}
