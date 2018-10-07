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

import com.github.ykiselev.opengl.sprites.Colors;
import com.github.ykiselev.services.layers.DrawingContext;
import org.lwjgl.glfw.GLFW;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class CommandLine {

    private final StringBuilder buf = new StringBuilder();

    private int cursorPos;

    public void add(int codePoint) {
        // todo : Only BMP code points are supported
        buf.insert(cursorPos, (char) codePoint);
        cursorPos++;
    }

    /**
     * Removes previous character (BackSpace key)
     */
    public void removeLeft() {
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
    public void left() {
        if (cursorPos > 0) {
            cursorPos--;
        }
    }

    /**
     * Moves cursor one char right
     */
    public void right() {
        if (cursorPos < buf.length()) {
            cursorPos++;
        }
    }

    public void draw(DrawingContext ctx, int x0, int y0, int width, int height, int color) {
        ctx.draw(x0, y0 + ctx.font().height(), width, buf, color);
        final int cursorWidth = ctx.font().width("_");
        final float brightness = (float) Math.sin(6 * GLFW.glfwGetTime());
        ctx.draw(x0 + cursorWidth * cursorPos, y0 + ctx.font().height(), width, "_", Colors.fade(color, brightness));
    }

    public void begin() {
        cursorPos = 0;
    }

    public void end() {
        cursorPos = buf.length();
    }

    public void complete() {
        // todo
    }
}
