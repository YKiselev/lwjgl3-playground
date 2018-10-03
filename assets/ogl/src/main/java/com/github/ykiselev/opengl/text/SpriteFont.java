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

package com.github.ykiselev.opengl.text;

import com.github.ykiselev.opengl.textures.Texture2d;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public interface SpriteFont extends AutoCloseable {

    Texture2d texture();

    int height();

    int glyphXBorder();

    int glyphYBorder();

    /**
     * Returns character's glyph or default character glyph
     *
     * @param ch character to search glyph for
     * @return character glyph if found, default character glyph otherwise
     */
    Glyph glyphOrDefault(char ch);

    /**
     * Searches for character glyph.
     *
     * @param ch character to search glyph for
     * @return character glyph if found {@code null} otherwise
     */
    Glyph glyph(char ch);

    int width(CharSequence text);

    int height(CharSequence text, int width);

}
