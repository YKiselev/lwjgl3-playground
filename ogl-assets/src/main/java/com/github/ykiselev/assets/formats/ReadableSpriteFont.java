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

package com.github.ykiselev.assets.formats;

import com.github.ykiselev.assets.Assets;
import com.github.ykiselev.assets.ReadableAsset;
import com.github.ykiselev.assets.ResourceException;
import com.github.ykiselev.gfx.font.GlyphRange;
import com.github.ykiselev.opengl.text.DefaultSpriteFont;
import com.github.ykiselev.opengl.text.Glyph;
import com.github.ykiselev.opengl.text.SpriteFont;
import com.github.ykiselev.opengl.textures.SimpleTexture2d;
import com.github.ykiselev.opengl.textures.Texture2d;
import org.lwjgl.system.MemoryStack;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.nio.IntBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

import static org.lwjgl.opengl.GL11.GL_NEAREST;
import static org.lwjgl.opengl.GL11.GL_ONE;
import static org.lwjgl.opengl.GL11.GL_RED;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_HEIGHT;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_INTERNAL_FORMAT;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WIDTH;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_S;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_T;
import static org.lwjgl.opengl.GL11.glGetTexLevelParameteri;
import static org.lwjgl.opengl.GL11.glTexParameteri;
import static org.lwjgl.opengl.GL11.glTexParameteriv;
import static org.lwjgl.opengl.GL12.GL_CLAMP_TO_EDGE;
import static org.lwjgl.opengl.GL33.GL_TEXTURE_SWIZZLE_RGBA;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class ReadableSpriteFont implements ReadableAsset<SpriteFont> {

    @Override
    public SpriteFont read(ReadableByteChannel channel, Assets assets) throws ResourceException {
        final com.github.ykiselev.gfx.font.SpriteFont spriteFont = readSpriteFont(channel);
        final Texture2d texture = readSpriteFontTexture(assets, spriteFont);
        texture.bind();
        final int width = glGetTexLevelParameteri(GL_TEXTURE_2D, 0, GL_TEXTURE_WIDTH);
        final int height = glGetTexLevelParameteri(GL_TEXTURE_2D, 0, GL_TEXTURE_HEIGHT);
        setupTextureParameters();

        final int characterWidth = spriteFont.characterWidth();
        final char defaultCharacter = spriteFont.defaultCharacter();

        // Prepare glyphs
        final double cs = 1.0 / (double) width;
        final double ct = 1.0 / (double) height;
        final int fontHeight = spriteFont.fontHeight();
        final com.github.ykiselev.opengl.text.GlyphRange[] ranges = new com.github.ykiselev.opengl.text.GlyphRange[spriteFont.glyphs().length];
        Glyph defaultGlyph = null;
        int r = 0;
        for (GlyphRange range : spriteFont.glyphs()) {
            final com.github.ykiselev.gfx.font.Glyph[] srcGlyphs = range.glyphs();
            if (srcGlyphs.length == 0) {
                continue;
            }
            final Glyph[] glyphs = new Glyph[srcGlyphs.length];
            int g = 0;
            for (com.github.ykiselev.gfx.font.Glyph src : srcGlyphs) {
                final int glyphWidth = characterWidth > 0 ? characterWidth : src.width();
                float s0 = (float) (cs * src.x());
                float t0 = (float) (ct * src.y());
                float s1 = (float) (cs * (src.x() + glyphWidth));
                float t1 = (float) (ct * (src.y() + fontHeight));
                glyphs[g] = new Glyph(s0, t0, s1, t1, glyphWidth);
                if (src.character() == defaultCharacter) {
                    defaultGlyph = glyphs[g];
                }
                g++;
            }
            ranges[r] = new com.github.ykiselev.opengl.text.GlyphRange(
                    srcGlyphs[0].character(),
                    glyphs
            );
            r++;
        }
        return new DefaultSpriteFont(
                texture,
                fontHeight,
                spriteFont.glyphXBorder(),
                spriteFont.glyphYBorder(),
                ranges,
                defaultGlyph
        );
    }

    private void setupTextureParameters() {
        final int format = glGetTexLevelParameteri(GL_TEXTURE_2D, 0, GL_TEXTURE_INTERNAL_FORMAT);
        if (format == GL_RED) {
            try (MemoryStack ms = MemoryStack.stackPush()) {
                final IntBuffer swizzleMask = ms.callocInt(4);
                swizzleMask.put(GL_ONE)
                        .put(GL_ONE)
                        .put(GL_ONE)
                        .put(GL_RED)
                        .flip();
                glTexParameteriv(GL_TEXTURE_2D, GL_TEXTURE_SWIZZLE_RGBA, swizzleMask);
            }
        }
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
    }

    private Texture2d readSpriteFontTexture(Assets assets, com.github.ykiselev.gfx.font.SpriteFont spriteFont) {
        final Texture2d texture;
        try (ReadableByteChannel bc = Channels.newChannel(new ByteArrayInputStream(spriteFont.image()))) {
            texture = assets.resolve(SimpleTexture2d.class)
                    .read(bc, assets);
        } catch (IOException e) {
            throw new ResourceException(e);
        }
        return texture;
    }

    private com.github.ykiselev.gfx.font.SpriteFont readSpriteFont(ReadableByteChannel channel) {
        final com.github.ykiselev.gfx.font.SpriteFont spriteFont;
        try (ObjectInputStream ois = new ObjectInputStream(Channels.newInputStream(channel))) {
            spriteFont = (com.github.ykiselev.gfx.font.SpriteFont) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new ResourceException(e);
        }
        return spriteFont;
    }
}
