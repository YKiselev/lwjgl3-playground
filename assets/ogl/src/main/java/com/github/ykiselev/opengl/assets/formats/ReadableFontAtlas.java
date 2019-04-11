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

package com.github.ykiselev.opengl.assets.formats;

import com.github.ykiselev.assets.Assets;
import com.github.ykiselev.assets.ReadableAsset;
import com.github.ykiselev.assets.ResourceException;
import com.github.ykiselev.opengl.fonts.Bitmap;
import com.github.ykiselev.opengl.fonts.FontAtlas;
import com.github.ykiselev.opengl.fonts.TrueTypeFont;
import com.github.ykiselev.opengl.fonts.TrueTypeFontInfo;
import com.github.ykiselev.wrap.Wrap;
import com.github.ykiselev.wrap.Wraps;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigValue;

import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;

/**
 * @author Yuriy Kiselev (uze@yandex.ru)
 * @since 09.04.2019
 */
public final class ReadableFontAtlas implements ReadableAsset<Map<String, Wrap<TrueTypeFont>>> {

    private final Supplier<Bitmap<Wrap<ByteBuffer>>> bitmapFactory;

    public ReadableFontAtlas(Supplier<Bitmap<Wrap<ByteBuffer>>> bitmapFactory) {
        this.bitmapFactory = requireNonNull(bitmapFactory);
    }

    @Override
    public Wrap<Map<String, Wrap<TrueTypeFont>>> read(ReadableByteChannel channel, Assets assets) throws ResourceException {
        final Config atlasConfig;
        final ReadableAsset<Config> readableConfig = assets.resolve(Config.class);
        try (Wrap<Config> config = readableConfig.read(channel, assets)) {
            atlasConfig = config.value();
        }
        @SuppressWarnings("unchecked") final Map.Entry<String, TrueTypeFontInfo>[] fonts = atlasConfig.getConfig("fonts").root()
                .values()
                .stream()
                .map(ConfigValue::unwrapped)
                .map(String.class::cast)
                .filter(v -> !v.isEmpty())
                .map(uri -> Map.entry(uri, assets.load(uri, TrueTypeFontInfo.class).value()))
                .toArray(Map.Entry[]::new);
        // Sort array from smallest font to larges to improve glyph texture fill rate
        Arrays.sort(fonts, Comparator.comparingDouble(e -> e.getValue().metrics().fontSize()));
        try (FontAtlas atlas = new FontAtlas(bitmapFactory)) {
            // todo - code points
            Arrays.stream(fonts).forEach(e -> atlas.addFont(e.getKey(), e.getValue(), null));

            return Wraps.simple(
                    atlas.drainFonts().entrySet().stream().collect(Collectors.toMap(
                            Map.Entry::getKey,
                            e -> Wraps.of(e.getValue())
                    ))
            );
        }
    }
}
