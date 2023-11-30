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
import com.github.ykiselev.opengl.OglRecipes
import com.github.ykiselev.opengl.textures.DefaultTexture2d
import com.github.ykiselev.opengl.textures.ImageData
import com.github.ykiselev.opengl.textures.Texture2d
import com.github.ykiselev.playground.assets.common.AssetUtils.read
import com.github.ykiselev.wrap.Wrap
import com.github.ykiselev.wrap.Wraps.of
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL12
import org.lwjgl.opengl.GL30
import java.nio.channels.ReadableByteChannel

/**
 * Created by Y.Kiselev on 05.06.2016.
 */
class ReadableTexture2d : ReadableAsset<Texture2d, ReadableTexture2d.Context> {

    class Context(val isMipMapped: Boolean)

    override fun read(
        channel: ReadableByteChannel,
        recipe: Recipe<*, Texture2d, Context>?,
        assets: Assets
    ): Wrap<Texture2d> =
        read(channel, OglRecipes.IMAGE_DATA, assets).use { image ->
            of(loadTexture(image.value(), recipe?.context()?.isMipMapped ?: false))
        }

    private fun loadTexture(imageData: ImageData, mipMapped: Boolean): Texture2d {
        val texture: Texture2d = DefaultTexture2d()
        texture.bind()
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT)
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT)
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST_MIPMAP_LINEAR)
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR)
        GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, imageData.alignment())
        GL11.glTexImage2D(
            GL11.GL_TEXTURE_2D, 0, imageData.bestInternalFormat(), imageData.width(), imageData.height(),
            0, imageData.bestFormat(), GL11.GL_UNSIGNED_BYTE, imageData.image()
        )
        if (mipMapped) {
            GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D)
        } else {
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL12.GL_TEXTURE_BASE_LEVEL, 0)
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL12.GL_TEXTURE_MAX_LEVEL, 0)
        }
        texture.unbind()
        return texture
    }
}
