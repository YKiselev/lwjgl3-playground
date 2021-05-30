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

import com.github.ykiselev.common.circular.ArrayCircularBuffer;
import com.github.ykiselev.common.circular.CircularBuffer;
import com.github.ykiselev.common.iterators.EndlessIterator;
import com.github.ykiselev.common.iterators.MappingIterator;
import com.github.ykiselev.opengl.sprites.Colors;
import com.github.ykiselev.opengl.sprites.SpriteBatch;
import com.github.ykiselev.opengl.sprites.TextAttributes;
import com.github.ykiselev.opengl.sprites.TextDrawingFlags;
import com.github.ykiselev.opengl.text.Font;
import com.github.ykiselev.spi.api.Named;
import com.github.ykiselev.spi.services.commands.CommandException;
import com.github.ykiselev.spi.services.commands.Commands;
import com.github.ykiselev.spi.services.commands.Commands.ExecutionContext;
import com.github.ykiselev.spi.services.configuration.Config;
import com.github.ykiselev.spi.services.configuration.PersistedConfiguration;
import com.github.ykiselev.spi.services.configuration.values.ConfigValue;
import com.github.ykiselev.spi.services.layers.DrawingContext;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

import static java.util.Objects.requireNonNull;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class DefaultCommandLine implements CommandLine {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final StringBuilder buf = new StringBuilder();

    private final CircularBuffer<String> history;

    private final PersistedConfiguration persistedConfiguration;

    private final Commands commands;

    private final ExecutionContext context = new ExecutionContext() {
        @Override
        public void onException(RuntimeException ex) {
            logger.error(MARKER, ex.toString());
        }

        @Override
        public void onUnknownCommand(List<String> args) {
            final Config cfg = persistedConfiguration.root();
            final String name = args.get(0);
            if (cfg.hasVariable(name)) {
                try {
                    final ConfigValue value = cfg.getValue(name, ConfigValue.class);
                    if (args.size() == 2) {
                        value.setString(args.get(1));
                    }
                    logger.info(MARKER, "{}=\"{}\"", name, value.toString());
                } catch (RuntimeException ex) {
                    logger.error(MARKER, ex.toString());
                }
            } else {
                logger.error(MARKER, "Unknown command: {}", name);
            }
        }
    };

    private final Function<String, Collection<Named>> searchProvider;

    private int cursorPos;

    private int historyIndex;

    private boolean historySearch;

    private boolean search;

    private Iterator<String> found = Collections.emptyIterator();

    /**
     * Command fragment copied from {@code buf} at the start of history search
     */
    private String fragment;

    public DefaultCommandLine(PersistedConfiguration persistedConfiguration, Commands commands, int historySize, Function<String, Collection<Named>> searchProvider) {
        this.persistedConfiguration = requireNonNull(persistedConfiguration);
        this.commands = requireNonNull(commands);
        this.history = new ArrayCircularBuffer<>(String.class, historySize);
        this.searchProvider = requireNonNull(searchProvider);
    }

    /**
     * Adds character at the cursor position.<p/>
     * Note: only BMP code points are currently supported.
     *
     * @param codePoint the Unicode code point (UTF-32)
     */
    @Override
    public void add(int codePoint) {
        buf.insert(cursorPos, (char) codePoint);
        cursorPos++;
        historySearch = false;
        search = false;
    }

    /**
     * Removes previous character (BackSpace key)
     */
    @Override
    public void removeLeft() {
        if (cursorPos > 0 && cursorPos - 1 < buf.length()) {
            buf.delete(cursorPos - 1, cursorPos);
            cursorPos--;
        }
        historySearch = false;
        search = false;
    }

    /**
     * Removes character at cursor position (Del key)
     */
    @Override
    public void remove() {
        if (cursorPos < buf.length()) {
            buf.delete(cursorPos, cursorPos + 1);
        }
        historySearch = false;
        search = false;
    }

    /**
     * Moves cursor one char left
     */
    @Override
    public void left() {
        if (cursorPos > 0) {
            cursorPos--;
        }
    }

    /**
     * Moves cursor one char right
     */
    @Override
    public void right() {
        if (cursorPos < buf.length()) {
            cursorPos++;
        }
    }

    @Override
    public void draw(DrawingContext ctx, int x0, int y0, int width, int height) {
        final Font font = ctx.textAttributes().font();
        final int y = y0 + font.lineSpace() + 3;
        final SpriteBatch batch = ctx.batch();
        final TextAttributes attributes = ctx.textAttributes();
        final boolean restoreUseCcs = attributes.remove(TextDrawingFlags.USE_COLOR_CONTROL_SEQUENCES);
        batch.draw(x0, y, width, buf, attributes);
        final int cursorWidth = font.width("_");
        final float brightness = (float) Math.sin(8 * GLFW.glfwGetTime());
        final int prevColor = attributes.color();
        attributes.color(Colors.fade(prevColor, brightness));
        final int x = x0 + cursorWidth * cursorPos;
        batch.draw(x, y, width, "_", attributes);
        batch.draw(x, y + 1, width, "_", attributes);
        attributes.color(prevColor);
        if (restoreUseCcs) {
            attributes.add(TextDrawingFlags.USE_COLOR_CONTROL_SEQUENCES);
        }
    }

    /**
     * Moves cursor to the beginning of the command line
     */
    @Override
    public void begin() {
        cursorPos = 0;
    }

    /**
     * Moves the cursor right after the last command line character
     */
    @Override
    public void end() {
        cursorPos = buf.length();
    }

    /**
     * Tries to complete entered command
     */
    @Override
    public void complete() {
        if (!search) {
            if (buf.length() == 0) {
                return;
            }
            search = true;
            final Collection<Named> names = searchProvider.apply(buf.toString());
            if (names.isEmpty()) {
                found = Collections.emptyIterator();
            } else {
                found = new MappingIterator<>(new EndlessIterator<>(names), Named::name);
                final Iterator<Named> it = names.iterator();
                if (names.size() > 1) {
                    for (int i = 0; i < 8 && it.hasNext(); i++) {
                        final Named v = it.next();
                        if (v instanceof ConfigValue) {
                            logger.info(MARKER, "  {}=^0b\"{}\"", v.name(), v);
                        } else {
                            logger.info(MARKER, "  {}", v.name());
                        }
                    }
                    if (names.size() > 8) {
                        logger.info(MARKER, "...and {} more.", names.size() - 8);
                    }
                }
            }
        }
        if (found.hasNext()) {
            set(found.next());
        }
    }

    @Override
    public void reset() {
        buf.setLength(0);
        cursorPos = 0;
        historySearch = false;
        search = false;
        fragment = null;
        found = Collections.emptyIterator();
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
    @Override
    public void execute() {
        if (buf.length() == 0) {
            return;
        }
        final String commandLine = buf.toString();
        reset();
        try {
            commands.execute(commandLine, context);
            addToHistory(commandLine);
        } catch (CommandException ex) {
            logger.error(MARKER, ex.getMessage());
        }
    }

    private void set(String commandLine) {
        buf.setLength(0);
        buf.append(commandLine);
        cursorPos = buf.length();
    }

    private void searchHistory(Supplier<String> supplier) {
        search = false;
        if (!historySearch) {
            historySearch = true;
            fragment = buf.toString();
            historyIndex = history.count();
        }
        for (; ; ) {
            final String cmd = supplier.get();
            if (cmd == null) {
                return;
            }
            if (!cmd.isEmpty() && !fragment.isEmpty()) {
                if (!cmd.startsWith(fragment)) {
                    continue;
                }
            }
            set(cmd);
            break;
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

    @Override
    public void searchHistoryBackward() {
        searchHistory(this::prevHistory);
    }

    @Override
    public void searchHistory() {
        searchHistory(this::nextHistory);
    }
}
