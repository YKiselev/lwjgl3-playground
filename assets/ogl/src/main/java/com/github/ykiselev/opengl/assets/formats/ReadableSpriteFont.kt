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
package com.github.ykiselev.opengl.assets.formats

import com.github.ykiselev.assets.Assets
import com.github.ykiselev.assets.ReadableAsset
import com.github.ykiselev.assets.Recipe
import com.github.ykiselev.assets.ResourceException
import com.github.ykiselev.gfx.font.SpriteFont
import com.github.ykiselev.opengl.OglRecipes
import com.github.ykiselev.opengl.text.DefaultSpriteFont
import com.github.ykiselev.opengl.text.Glyph
import com.github.ykiselev.opengl.text.GlyphRange
import com.github.ykiselev.opengl.text.GlyphRanges
import com.github.ykiselev.opengl.textures.Texture2d
import com.github.ykiselev.wrap.Wrap
import com.github.ykiselev.wrap.Wraps.of
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL12
import org.lwjgl.opengl.GL33
import org.lwjgl.system.MemoryStack
import java.io.ByteArrayInputStream
import java.io.IOException
import java.io.ObjectInputStream
import java.nio.channels.Channels
import java.nio.channels.ReadableByteChannel

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
class ReadableSpriteFont : ReadableAsset<com.github.ykiselev.opengl.text.SpriteFont, Void> {

    override fun read(
        channel: ReadableByteChannel,
        recipe: Recipe<*, com.github.ykiselev.opengl.text.SpriteFont, Void>?,
        assets: Assets
    ): Wrap<com.github.ykiselev.opengl.text.SpriteFont> {
        val spriteFont = readSpriteFont(channel)
        val texture = readSpriteFontTexture(assets, spriteFont)
        texture.value().bind()
        val width = GL11.glGetTexLevelParameteri(GL11.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_WIDTH)
        val height = GL11.glGetTexLevelParameteri(GL11.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_HEIGHT)
        setupTextureParameters()
        val characterWidth = spriteFont.characterWidth()
        val defaultCharacter = spriteFont.defaultCharacter()

        // Prepare glyphs
        val cs = 1.0 / width.toDouble()
        val ct = 1.0 / height.toDouble()
        val fontHeight = spriteFont.fontHeight()
        val ranges = arrayOfNulls<GlyphRange>(spriteFont.glyphs().size)
        var defaultGlyph: Glyph? = null
        var r = 0
        for (range in spriteFont.glyphs()) {
            val srcGlyphs = range.glyphs()
            if (srcGlyphs.isEmpty()) {
                continue
            }
            val glyphs = arrayOfNulls<Glyph>(srcGlyphs.size)
            for ((g, src) in srcGlyphs.withIndex()) {
                val glyphWidth = if (characterWidth > 0) characterWidth else src.width().toInt()
                val s0 = (cs * src.x()).toFloat()
                val t0 = (ct * src.y()).toFloat()
                val s1 = (cs * (src.x() + glyphWidth)).toFloat()
                val t1 = (ct * (src.y() + fontHeight)).toFloat()
                glyphs[g] = Glyph(s0, t0, s1, t1, glyphWidth)
                if (src.character() == defaultCharacter) {
                    defaultGlyph = glyphs[g]
                }
            }
            ranges[r] = GlyphRange(
                srcGlyphs[0].character(),
                glyphs
            )
            r++
        }
        return of(
            DefaultSpriteFont(
                texture,
                fontHeight,
                spriteFont.glyphXBorder(),
                spriteFont.glyphYBorder(),
                GlyphRanges(
                    ranges,
                    defaultGlyph
                )
            )
        )
    }

    private fun setupTextureParameters() {
        val format = GL11.glGetTexLevelParameteri(GL11.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_INTERNAL_FORMAT)
        if (format == GL11.GL_RED) {
            MemoryStack.stackPush().use { ms ->
                val swizzleMask = ms.callocInt(4)
                swizzleMask.put(GL11.GL_ONE)
                    .put(GL11.GL_ONE)
                    .put(GL11.GL_ONE)
                    .put(GL11.GL_RED)
                    .flip()
                GL11.glTexParameteriv(GL11.GL_TEXTURE_2D, GL33.GL_TEXTURE_SWIZZLE_RGBA, swizzleMask)
            }
        }
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE)
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE)
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST)
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST)
    }

    private fun readSpriteFontTexture(assets: Assets, spriteFont: SpriteFont): Wrap<Texture2d> =
        try {
            Channels.newChannel(ByteArrayInputStream(spriteFont.image())).use { bc ->
                assets.resolve(OglRecipes.SPRITE)!!.read(bc, OglRecipes.SPRITE, assets)
            }
        } catch (e: IOException) {
            throw ResourceException(e)
        }

    private fun readSpriteFont(channel: ReadableByteChannel): SpriteFont =
        try {
            ObjectInputStream(Channels.newInputStream(channel)).use { ois ->
                ois.readObject() as SpriteFont
            }
        } catch (e: IOException) {
            throw ResourceException(e)
        } catch (e: ClassNotFoundException) {
            throw ResourceException(e)
        }
}
