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

package com.github.ykiselev.opengl.sprites;

import com.github.ykiselev.opengl.fonts.TrueTypeFont;
import com.github.ykiselev.opengl.text.Font;
import com.github.ykiselev.opengl.text.SpriteFont;

import java.util.EnumSet;
import java.util.Set;

import static java.util.Objects.requireNonNull;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class TextAttributes {

    private SpriteFont spriteFont;

    private TrueTypeFont trueTypeFont;

    private TextAlignment alignment = TextAlignment.LEFT;

    private int color = Colors.WHITE;

    private Set<TextDrawingFlags> flags = EnumSet.noneOf(TextDrawingFlags.class);

    public SpriteFont spriteFont() {
        return spriteFont;
    }

    public void spriteFont(SpriteFont font) {
        this.trueTypeFont = null;
        this.spriteFont = font;
    }

    public TrueTypeFont trueTypeFont() {
        return trueTypeFont;
    }

    public void trueTypeFont(TrueTypeFont trueTypeFont) {
        this.spriteFont = null;
        this.trueTypeFont = trueTypeFont;
    }

    public Font font() {
        return trueTypeFont != null ? trueTypeFont : spriteFont;
    }

    public TextAlignment alignment() {
        return alignment;
    }

    public int color() {
        return color;
    }

    public Set<TextDrawingFlags> flags() {
        return flags;
    }

    public void alignment(TextAlignment alignment) {
        this.alignment = requireNonNull(alignment);
    }

    public void color(int color) {
        this.color = color;
    }

    public boolean useColorControlSequences() {
        return flags.contains(TextDrawingFlags.USE_COLOR_CONTROL_SEQUENCES);
    }

    public boolean useKerning() {
        return flags.contains(TextDrawingFlags.USE_KERNING);
    }

    public void flags(Set<TextDrawingFlags> flags) {
        this.flags = requireNonNull(flags);
    }

    public boolean add(TextDrawingFlags flag) {
        return flags.add(flag);
    }

    public boolean remove(TextDrawingFlags flag) {
        return flags.remove(flag);
    }

}
