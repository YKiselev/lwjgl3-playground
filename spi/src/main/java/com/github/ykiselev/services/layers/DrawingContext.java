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

package com.github.ykiselev.services.layers;

import com.github.ykiselev.opengl.sprites.SpriteBatch;
import com.github.ykiselev.opengl.sprites.TextAlignment;
import com.github.ykiselev.opengl.text.SpriteFont;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public interface DrawingContext {

    SpriteFont font();

    SpriteBatch batch();

    default int draw(int x, int y, int width, CharSequence text, TextAlignment alignment, int color) {
        return batch().draw(font(), x, y, width, text, alignment, color);
    }

    StringBuilder stringBuilder();

    default int draw(int x, int y, int width, CharSequence text, int color) {
        return batch().draw(font(), x, y, width, text, color);
    }
}