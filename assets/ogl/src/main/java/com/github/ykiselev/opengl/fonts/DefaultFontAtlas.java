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

package com.github.ykiselev.opengl.fonts;

import com.github.ykiselev.wrap.Wrap;

import java.util.Map;
import java.util.Set;

import static java.util.Objects.requireNonNull;

/**
 * @author Yuriy Kiselev (uze@yandex.ru)
 * @since 14.04.2019
 */
public final class DefaultFontAtlas implements FontAtlas {

    private final Map<String, Wrap<TrueTypeFont>> fonts;

    public DefaultFontAtlas(Map<String, Wrap<TrueTypeFont>> fonts) {
        this.fonts = requireNonNull(fonts);
    }

    @Override
    public Wrap<TrueTypeFont> get(String key) {
        return fonts.get(key);
    }

    @Override
    public Set<String> keys() {
        return fonts.keySet();
    }

    @Override
    public void close() {
        fonts.forEach((k, v) -> v.close());
    }
}
