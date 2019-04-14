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

import com.github.ykiselev.common.lifetime.SharedResource;
import com.github.ykiselev.common.math.PowerOfTwo;
import com.github.ykiselev.opengl.textures.DefaultTexture2d;
import com.github.ykiselev.opengl.textures.Texture2d;
import com.github.ykiselev.wrap.Wrap;
import org.lwjgl.stb.STBTTPackContext;
import org.lwjgl.stb.STBTTPackRange;
import org.lwjgl.stb.STBTTPackedchar;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;
import static org.lwjgl.opengl.GL11.GL_ALPHA;
import static org.lwjgl.opengl.GL11.GL_LINEAR;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glGenTextures;
import static org.lwjgl.opengl.GL11.glTexImage2D;
import static org.lwjgl.opengl.GL11.glTexParameteri;
import static org.lwjgl.stb.STBTruetype.stbtt_PackBegin;
import static org.lwjgl.stb.STBTruetype.stbtt_PackEnd;
import static org.lwjgl.stb.STBTruetype.stbtt_PackFontRanges;
import static org.lwjgl.stb.STBTruetype.stbtt_PackSetOversampling;
import static org.lwjgl.system.MemoryUtil.NULL;

/**
 * This class tries to pack as many fonts as possible into as little textures as possible.
 * Holds native resources so calling of {@link FontAtlasBuilder#close()} after the usage is required.
 *
 * @author Yuriy Kiselev (uze@yandex.ru)
 * @since 07.04.2019
 */
public final class FontAtlasBuilder implements AutoCloseable {

    static final class PreFont {

        private final String key;

        private final Wrap<TrueTypeFontInfo> info;

        private final CodePoints codePoints;

        private final STBTTPackedchar.Buffer charData;

        PreFont(String key, Wrap<TrueTypeFontInfo> info, CodePoints codePoints, STBTTPackedchar.Buffer charData) {
            this.key = requireNonNull(key);
            this.info = requireNonNull(info);
            this.codePoints = requireNonNull(codePoints);
            this.charData = requireNonNull(charData);
        }
    }

    static final class Context implements AutoCloseable {

        private final Bitmap<Wrap<ByteBuffer>> bitmap;

        private final STBTTPackContext pc;

        private final List<PreFont> fonts = new ArrayList<>();

        private Context(Bitmap<Wrap<ByteBuffer>> bitmap, STBTTPackContext pc) {
            this.bitmap = requireNonNull(bitmap);
            this.pc = requireNonNull(pc);
        }

        @Override
        public void close() {
            bitmap.pixels().close();
            pc.close();
        }

        boolean add(String key, Wrap<TrueTypeFontInfo> info, CodePoints codePoints, int horizontalOverSample, int verticalOverSample) {
            stbtt_PackSetOversampling(pc, horizontalOverSample, verticalOverSample);

            final int numCodePoints = codePoints.numCodePoints();
            // todo - do we really need this?
            final int pot = PowerOfTwo.next(numCodePoints);
            try (STBTTPackRange.Buffer ranges = STBTTPackRange.malloc(codePoints.numRanges())) {
                final STBTTPackedchar.Buffer charData = STBTTPackedchar.malloc(pot);
                for (int i = 0, p = 0; i < codePoints.numRanges(); i++) {
                    final CodePoints.Range range = codePoints.range(i);
                    final STBTTPackRange pr = ranges.get(i);
                    pr.font_size(info.value().metrics().fontSize());
                    pr.first_unicode_codepoint_in_range(range.firstCodePoint());
                    pr.num_chars(range.size());
                    pr.chardata_for_range(new STBTTPackedchar.Buffer(charData.address(p), range.size()));
                    pr.array_of_unicode_codepoints(null);
                    p += range.size();
                }

                if (!stbtt_PackFontRanges(pc, info.value().fontData(), 0, ranges)) {
                    charData.close();
                    return false;
                }

                charData.clear();
                fonts.add(new PreFont(key, info, codePoints, charData));
            }

            return true;
        }

        Map<String, TrueTypeFont> finish() {
            stbtt_PackEnd(pc);

            final int textureId = glGenTextures();
            glBindTexture(GL_TEXTURE_2D, textureId);
            glTexImage2D(GL_TEXTURE_2D, 0, GL_ALPHA, bitmap.width(), bitmap.height(), 0, GL_ALPHA, GL_UNSIGNED_BYTE, bitmap.pixels().value());
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);

            final SharedResource<Texture2d> sharedTexture = new SharedResource<>(
                    new DefaultTexture2d(textureId), (sr, t) -> t.close()
            );

            return fonts.stream()
                    .collect(Collectors.toMap(
                            pf -> pf.key,
                            pf -> new TrueTypeFont(pf.info, pf.charData, pf.codePoints,
                                    sharedTexture.share(), bitmap.width(), bitmap.height())
                    ));
        }
    }

    private final Supplier<Bitmap<Wrap<ByteBuffer>>> bitmapFactory;

    private final Map<String, TrueTypeFont> fonts = new HashMap<>();

    private Context context;

    /**
     * Primary ctor.
     *
     * @param bitmapFactory the bitmap factory which wil be called every time new bitmap is required to fill with font glyphs.
     */
    public FontAtlasBuilder(Supplier<Bitmap<Wrap<ByteBuffer>>> bitmapFactory) {
        this.bitmapFactory = requireNonNull(bitmapFactory);
    }

    /**
     * Adds new font to this atlas. For optimal results feed fonts in sequence from smallest to larges. This version simply delegates to
     * {@link FontAtlasBuilder#addFont(String, Wrap, CodePoints, int, int)}
     * with both oversample values set to 1.
     *
     * @param key        the key of the font (each font in the atlas should have unique key)
     * @param info       the font to add to atlas
     * @param codePoints the code points to use when building font glyphs
     */
    public void addFont(String key, Wrap<TrueTypeFontInfo> info, CodePoints codePoints) {
        addFont(key, info, codePoints, 1, 1);
    }

    /**
     * Adds new font to this atlas. For optimal results feed fonts in sequence from smallest to larges.
     *
     * @param key                  the key of the font (each font in the atlas should have unique key)
     * @param info                 the font to add to atlas
     * @param codePoints           the code points to use when building font glyphs
     * @param horizontalOverSample the horizontal oversample value
     * @param verticalOverSample   the vertical oversample value
     */
    public void addFont(String key, Wrap<TrueTypeFontInfo> info, CodePoints codePoints, int horizontalOverSample, int verticalOverSample) {
        for (; ; ) {
            if (context == null) {
                context = createContext(bitmapFactory.get());
            }
            if (context.add(key, info, codePoints, horizontalOverSample, verticalOverSample)) {
                break;
            }
            closeContext(contextFonts -> {
                if (contextFonts.isEmpty()) {
                    throw new IllegalStateException("Unable to pack font " + info + ", perhaps supplied bitmap is too small to fit this font!");
                }
                fonts.putAll(contextFonts);
            });
        }
    }

    /**
     * Drains all added fonts from this instance.
     * Note: font added after call to this method will be placed in new bitmap.
     *
     * @return the fonts added to atlas so far.
     */
    public Map<String, TrueTypeFont> drainFonts() {
        closeContext(fonts::putAll);
        final Map<String, TrueTypeFont> result = new HashMap<>(fonts);
        fonts.clear();
        return result;
    }

    private Context createContext(Bitmap<Wrap<ByteBuffer>> bitmap) {
        final STBTTPackContext pc = STBTTPackContext.malloc();
        if (!stbtt_PackBegin(pc, bitmap.pixels().value(), bitmap.width(), bitmap.height(), 0, 1, NULL)) {
            throw new IllegalStateException("Unable to start packing: not enough memory!");
        }
        return new Context(bitmap, pc);
    }

    private void closeContext(Consumer<Map<String, TrueTypeFont>> consumer) {
        consumer.accept(context.finish());
        context.close();
        context = null;
    }

    @Override
    public void close() {
        if (context != null) {
            context.close();
        }
    }
}
