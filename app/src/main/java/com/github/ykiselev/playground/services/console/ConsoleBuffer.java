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

import com.github.ykiselev.common.circular.CircularBuffer;
import com.github.ykiselev.opengl.sprites.SpriteBatch;
import com.github.ykiselev.opengl.sprites.TextAttributes;
import com.github.ykiselev.opengl.text.Font;
import com.github.ykiselev.spi.services.layers.DrawingContext;

import static java.util.Objects.requireNonNull;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
final class ConsoleBuffer {

    enum ScrollAction {
        NONE, PG_UP, PG_DOWN, SCROLL_UP, SCROLL_DOWN
    }

    private final CircularBuffer<String> buffer;

    private final String[] snapshot;

    private int offset;

    private ScrollAction scrollAction = ScrollAction.NONE;

    ConsoleBuffer(CircularBuffer<String> buffer) {
        this.buffer = requireNonNull(buffer);
        this.snapshot = new String[buffer.capacity()];
    }

    void pageUp() {
        scrollAction = ScrollAction.PG_UP;
    }

    void pageDown() {
        scrollAction = ScrollAction.PG_DOWN;
    }

    void scroll(double delta) {
        if (delta > 0) {
            scrollAction = ScrollAction.SCROLL_UP;
        } else if (delta < 0) {
            scrollAction = ScrollAction.SCROLL_DOWN;
        }
    }

    private void calculateOffset(Font font, String[] lines, int count, int viewHeight, int width) {
        final int scrollSize = Math.max(1, viewHeight / 5);
        int totalHeight = 0;
        for (int i = count - 1; i >= 0; i--) {
            totalHeight += font.height(lines[i], width);
        }
        final int maxOffset = Math.max(0, totalHeight - viewHeight);
        switch (scrollAction) {
            case NONE:
                break;

            case PG_UP:
                offset += viewHeight;
                break;

            case PG_DOWN:
                offset -= viewHeight;
                break;

            case SCROLL_UP:
                offset += scrollSize;
                break;

            case SCROLL_DOWN:
                offset -= scrollSize;
                break;
        }
        if (offset > maxOffset) {
            offset = maxOffset;
        }
        if (offset < 0) {
            offset = 0;
        }
        scrollAction = ScrollAction.NONE;
    }

    void draw(DrawingContext ctx, int x0, int y0, int width, int height) {
        final TextAttributes textAttributes = ctx.textAttributes();
        final Font font = ctx.font();
        final int lines = buffer.copyTo(snapshot);

        calculateOffset(font, snapshot, lines, height, width);

        final SpriteBatch batch = ctx.batch();
        for (int i = lines - 1, skipped = 0, y = y0 + font.lineSpace(); i >= 0; i--) {
            final String line = snapshot[i];
            final int lineHeight = font.height(line, width);
            if (skipped >= offset) {
                y += lineHeight;
                batch.draw(x0, y, width, line, textAttributes);
            } else {
                skipped += lineHeight;
            }
            if (y >= height) {
                break;
            }
        }
    }
}
