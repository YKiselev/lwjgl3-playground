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

package com.github.ykiselev.spi.services.layers;

import com.github.ykiselev.opengl.sprites.SpriteBatch;
import com.github.ykiselev.opengl.sprites.TextAttributes;
import com.github.ykiselev.opengl.text.Font;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public interface DrawingContext {

    default Font font() {
        final TextAttributes attributes = textAttributes();
        if (attributes.spriteFont() != null) {
            return attributes.spriteFont();
        }
        return attributes.trueTypeFont();
    }

    SpriteBatch batch();

    StringBuilder stringBuilder();

    TextAttributes textAttributes();
}