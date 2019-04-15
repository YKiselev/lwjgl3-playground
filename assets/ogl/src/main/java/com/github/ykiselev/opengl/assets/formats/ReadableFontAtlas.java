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
import com.github.ykiselev.assets.Recipe;
import com.github.ykiselev.assets.ResourceException;
import com.github.ykiselev.common.memory.MemAlloc;
import com.github.ykiselev.opengl.OglRecipes;
import com.github.ykiselev.opengl.fonts.Bitmap;
import com.github.ykiselev.opengl.fonts.CodePoints;
import com.github.ykiselev.opengl.fonts.DefaultFontAtlas;
import com.github.ykiselev.opengl.fonts.FontAtlas;
import com.github.ykiselev.opengl.fonts.FontAtlasBuilder;
import com.github.ykiselev.opengl.fonts.TrueTypeFontInfo;
import com.github.ykiselev.playground.assets.common.AssetUtils;
import com.github.ykiselev.wrap.Wrap;
import com.github.ykiselev.wrap.Wraps;
import com.typesafe.config.Config;

import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.IntStream;

import static java.util.Objects.requireNonNull;

/**
 * @author Yuriy Kiselev (uze@yandex.ru)
 * @since 09.04.2019
 */
public final class ReadableFontAtlas implements ReadableAsset<FontAtlas, Void> {

    private final Supplier<Bitmap<Wrap<ByteBuffer>>> bitmapFactory;

    public ReadableFontAtlas(Supplier<Bitmap<Wrap<ByteBuffer>>> bitmapFactory) {
        this.bitmapFactory = requireNonNull(bitmapFactory);
    }

    /**
     * @param width  the width to use for bitmaps
     * @param height the height to use for bitmaps
     * @see FontAtlasBuilder#FontAtlasBuilder(int, int)
     */
    public ReadableFontAtlas(int width, int height) {
        this(() -> new Bitmap<>(width, height, new MemAlloc(width * height)));
    }

    @Override
    public Wrap<FontAtlas> read(ReadableByteChannel channel, Recipe<?, FontAtlas, Void> recipe, Assets assets) throws ResourceException {
        try (Wrap<Config> cfg = AssetUtils.read(channel, OglRecipes.CONFIG, assets)) {
            final Config atlasConfig = cfg.value();
            @SuppressWarnings("unchecked") final Map.Entry<String, Wrap<TrueTypeFontInfo>>[] fonts = atlasConfig.getConfig("fonts").root()
                    .entrySet()
                    .stream()
                    .map(e -> {
                        final String value = (String) e.getValue().unwrapped();
                        if (value.isEmpty()) {
                            return null;
                        }
                        return Map.entry(e.getKey(), assets.load(value, OglRecipes.TRUE_TYPE_FONT_INFO));
                    }).filter(Objects::nonNull)
                    .toArray(Map.Entry[]::new);
            // Sort array from smallest font to larges to improve glyph texture fill rate
            Arrays.sort(fonts, Comparator.comparingDouble(e -> e.getValue().value().metrics().fontSize()));
            final CodePoints codePoints = CodePoints.of(readCodePoints(atlasConfig.getStringList("code-points")));
            try (FontAtlasBuilder atlas = new FontAtlasBuilder(bitmapFactory)) {
                Arrays.stream(fonts).forEach(e ->
                        atlas.addFont(e.getKey(), e.getValue(), codePoints)
                );

                return Wraps.of(
                        new DefaultFontAtlas(
                                // todo - make map immutable
                                atlas.drainFonts()
                        )
                );
            }
        }
    }

    static IntStream readCodePoints(Collection<String> src) {
        return src.stream().flatMapToInt(s -> {
            final int idx = s.indexOf("-");
            if (idx != -1) {
                return IntStream.range(
                        cp(s, 0, idx),
                        cp(s, idx + 1, s.length()) + 1
                );
            } else {
                return IntStream.of(cp(s, 0, s.length()));
            }
        });
    }

    /**
     * Extracts single code point from the given string starting at {@code from} index and ending right before {@code to} index.
     *
     * @param src  the source string
     * @param from from index (inclusive)
     * @param to   to index (exclusive)
     * @return the code point
     */
    private static int cp(String src, int from, int to) {
        final int result = src.codePointAt(from);
        final int count = Character.charCount(result);
        if (from + count != to) {
            throw new IllegalArgumentException("Bad code point: \"" + src.substring(from, to) + "\"");
        }
        return result;
    }
}
