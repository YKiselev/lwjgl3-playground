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

import com.github.ykiselev.circular.CircularBuffer;
import com.github.ykiselev.opengl.sprites.SpriteBatch;
import com.github.ykiselev.opengl.sprites.TextAlignment;
import com.github.ykiselev.opengl.sprites.TextBuilder;
import com.github.ykiselev.opengl.text.SpriteFont;
import com.github.ykiselev.services.layers.DrawingContext;

import static java.util.Objects.requireNonNull;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class ConsoleBuffer {

    private final CircularBuffer<String> buffer;

    private final String[] snapshot;

    private final TextBuilder textBuilder = new TextBuilder(200);

    public ConsoleBuffer(CircularBuffer<String> buffer) {
        this.buffer = requireNonNull(buffer);
        this.snapshot = new String[buffer.capacity()];
    }

    public void draw(DrawingContext ctx, int x0, int y0, int width, int height, int color) {
        final SpriteFont font = ctx.font();
        textBuilder.font(font);
        textBuilder.alignment(TextAlignment.LEFT);
        textBuilder.maxWidth(width);
        final int lines = buffer.copyTo(snapshot);
        final SpriteBatch batch = ctx.batch();
        batch.font(ctx.font());
        batch.useColorControlSequences(true);
        for (int i = lines - 1, y = y0 + font.height() + font.glyphYBorder(); i >= 0; i--) {
            final String line = snapshot[i];
            if (false) {
                textBuilder.clear();
                final int lineHeight = textBuilder.draw(line);
                y += lineHeight;
                ctx.batch().draw(textBuilder, x0, y);
            } else {
                final int lineHeight = font.height(line, width);
                y += lineHeight;
                batch.draw(x0, y, width, line, color);
            }
            if (y >= height) {
                break;
            }
        }
    }
}
