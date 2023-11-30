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
package com.github.ykiselev.opengl.fonts

import com.github.ykiselev.common.lifetime.Ref
import com.github.ykiselev.common.math.PowerOfTwo
import com.github.ykiselev.opengl.textures.DefaultTexture2d
import com.github.ykiselev.opengl.textures.Texture2d
import com.github.ykiselev.wrap.Wrap
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL11.glTexImage2D
import org.lwjgl.opengl.GL12
import org.lwjgl.opengl.GL30
import org.lwjgl.opengl.GL33
import org.lwjgl.stb.STBTTPackContext
import org.lwjgl.stb.STBTTPackRange
import org.lwjgl.stb.STBTTPackedchar
import org.lwjgl.stb.STBTruetype
import org.lwjgl.system.MemoryStack
import org.lwjgl.system.MemoryUtil
import java.nio.ByteBuffer
import java.util.function.Consumer
import java.util.function.Supplier

/**
 * This class tries to pack as many fonts as possible into as little textures as possible.
 * Holds native resources so calling of [FontAtlasBuilder.close] after the usage is required.
 *
 * @author Yuriy Kiselev (uze@yandex.ru)
 * @since 07.04.2019
 */
class FontAtlasBuilder(private val bitmapFactory: Supplier<Bitmap<Wrap<ByteBuffer>>>) : AutoCloseable {

    internal data class PreFont(
        val key: String,
        val info: Wrap<TrueTypeFontInfo>,
        val codePoints: CodePoints,
        val charData: STBTTPackedchar.Buffer
    )

    internal class Context(val bitmap: Bitmap<Wrap<ByteBuffer>>, val pc: STBTTPackContext) : AutoCloseable {

        private val fonts: MutableList<PreFont> = mutableListOf()

        override fun close() {
            bitmap.pixels.close()
            pc.close()
        }

        fun add(
            key: String,
            info: Wrap<TrueTypeFontInfo>,
            codePoints: CodePoints,
            horizontalOverSample: Int,
            verticalOverSample: Int
        ): Boolean {
            STBTruetype.stbtt_PackSetOversampling(pc, horizontalOverSample, verticalOverSample)
            val numCodePoints = codePoints.numCodePoints()
            // todo - do we really need this?
            val pot = PowerOfTwo.next(numCodePoints)
            STBTTPackRange.malloc(codePoints.numRanges()).use { ranges ->
                val charData = STBTTPackedchar.malloc(pot)
                var i = 0
                var p = 0
                while (i < codePoints.numRanges()) {
                    val range = codePoints.range(i)
                    val pr = ranges[i]
                    pr.font_size(info.value().metrics().fontSize())
                    pr.first_unicode_codepoint_in_range(range.firstCodePoint())
                    pr.num_chars(range.size())
                    pr.chardata_for_range(STBTTPackedchar.Buffer(charData.address(p), range.size()))
                    pr.array_of_unicode_codepoints(null)
                    p += range.size()
                    i++
                }
                if (!STBTruetype.stbtt_PackFontRanges(pc, info.value().fontData(), 0, ranges)) {
                    charData.close()
                    return false
                }
                charData.clear()
                fonts.add(PreFont(key, info, codePoints, charData))
            }
            return true
        }

        fun finish(): Map<String, TrueTypeFont> {
            STBTruetype.stbtt_PackEnd(pc)
            val textureId = GL11.glGenTextures()
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureId)
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL12.GL_TEXTURE_BASE_LEVEL, 0)
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL12.GL_TEXTURE_MAX_LEVEL, 0)
            glTexImage2D(
                GL11.GL_TEXTURE_2D,
                0,
                GL30.GL_R8,
                bitmap.width,
                bitmap.height,
                0,
                GL11.GL_RED,
                GL11.GL_UNSIGNED_BYTE,
                bitmap.pixels.value()
            )
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR)
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR)
            MemoryStack.stackPush().use { ms ->
                val swizzleMask = ms.callocInt(4)
                swizzleMask.put(GL11.GL_ONE)
                    .put(GL11.GL_ONE)
                    .put(GL11.GL_ONE)
                    .put(GL11.GL_RED)
                    .flip()
                GL11.glTexParameteriv(GL11.GL_TEXTURE_2D, GL33.GL_TEXTURE_SWIZZLE_RGBA, swizzleMask)
            }
            val sharedTexture = Ref.of<Texture2d>(DefaultTexture2d(textureId))
            return fonts.associateBy({ it.key }) {
                TrueTypeFont(
                    it.info, it.charData, it.codePoints,
                    sharedTexture.newRef(), bitmap.width, bitmap.height
                )
            }
        }
    }

    private val fonts: MutableMap<String, TrueTypeFont> = mutableMapOf()
    private var context: Context? = null

    /**
     * Adds new font to this atlas. For optimal results feed fonts in sequence from smallest to larges. This version simply delegates to
     * [FontAtlasBuilder.addFont]
     * with both oversample values set to 1.
     *
     * @param key        the key of the font (each font in the atlas should have unique key)
     * @param info       the font to add to atlas
     * @param codePoints the code points to use when building font glyphs
     * @param horizontalOverSample the horizontal oversample value
     * @param verticalOverSample   the vertical oversample value
     */
    @JvmOverloads
    fun addFont(
        key: String,
        info: Wrap<TrueTypeFontInfo>,
        codePoints: CodePoints,
        horizontalOverSample: Int = 1,
        verticalOverSample: Int = 1
    ) {
        while (true) {
            if (context == null) {
                context = createContext(bitmapFactory.get())
            }
            if (context!!.add(key, info, codePoints, horizontalOverSample, verticalOverSample)) {
                break
            }
            closeContext { contextFonts: Map<String, TrueTypeFont> ->
                check(contextFonts.isNotEmpty()) { "Unable to pack font $info, perhaps supplied bitmap is too small to fit this font!" }
                fonts.putAll(contextFonts)
            }
        }
    }

    /**
     * Drains all added fonts from this instance.
     * Note: font added after call to this method will be placed in new bitmap.
     *
     * @return the fonts added to atlas so far.
     */
    fun drainFonts(): Map<String, TrueTypeFont> {
        closeContext {
            fonts.putAll(it)
        }
        return fonts.toMap().also {
            fonts.clear()
        }
    }

    private fun createContext(bitmap: Bitmap<Wrap<ByteBuffer>>): Context {
        val pc = STBTTPackContext.malloc()
        check(
            STBTruetype.stbtt_PackBegin(
                pc,
                bitmap.pixels.value(),
                bitmap.width,
                bitmap.height,
                0,
                1,
                MemoryUtil.NULL
            )
        ) { "Unable to start packing: not enough memory!" }
        return Context(bitmap, pc)
    }

    private fun closeContext(consumer: Consumer<Map<String, TrueTypeFont>>) {
        consumer.accept(context!!.finish())
        context!!.close()
        context = null
    }

    override fun close() {
        if (context != null) {
            context!!.close()
        }
    }
}
