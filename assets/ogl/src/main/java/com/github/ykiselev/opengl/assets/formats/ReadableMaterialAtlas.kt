package com.github.ykiselev.opengl.assets.formats

import com.github.ykiselev.assets.Assets
import com.github.ykiselev.assets.ReadableAsset
import com.github.ykiselev.assets.Recipe
import com.github.ykiselev.opengl.OglRecipes
import com.github.ykiselev.opengl.materials.Material
import com.github.ykiselev.opengl.materials.MaterialAtlas
import com.github.ykiselev.opengl.textures.DefaultTexture2d
import com.github.ykiselev.opengl.textures.ImageData
import com.github.ykiselev.opengl.textures.Texture2d
import com.github.ykiselev.playground.assets.common.AssetUtils.read
import com.github.ykiselev.wrap.Wrap
import com.github.ykiselev.wrap.Wraps.of
import com.typesafe.config.Config
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL30
import java.nio.channels.ReadableByteChannel

class ReadableMaterialAtlas : ReadableAsset<MaterialAtlas, Void> {

    override fun read(
        channel: ReadableByteChannel,
        recipe: Recipe<*, MaterialAtlas, Void>?,
        assets: Assets
    ): Wrap<MaterialAtlas> =
        assets.load("materials/default/material.conf", OglRecipes.CONFIG).use { mc ->
            read(channel, OglRecipes.CONFIG, assets).use { cfg ->
                val builder = MaterialAtlasBuilder(TEX_WIDTH, TEX_HEIGHT)
                cfg.value().getConfigList("blocks")
                    .stream()
                    .map { e: Config -> e.withFallback(mc.value()) }
                    .forEach { e: Config ->
                        read(
                            e.getString("asset"),
                            OglRecipes.IMAGE_DATA,
                            assets
                        ).use { wrp -> builder.add(wrp.value(), e.getBoolean("opaque")) }
                    }
                of(builder.build())
            }
        }

    internal class MaterialAtlasBuilder(private val width: Int, private val height: Int) {
        private var tex: Texture2d? = null
        private var x = 0
        private var y = 0
        private val sscale: Float = IMAGE_WIDTH / width.toFloat()
        private val tscale: Float = IMAGE_HEIGHT / height.toFloat()
        private val materials: MutableList<Material> = ArrayList()

        val isEmpty: Boolean
            get() = x == 0 && y == 0

        private fun newTexture(): Texture2d {
            val tex: Texture2d = DefaultTexture2d()
            tex.bind()
            GL11.glTexImage2D(
                GL11.GL_TEXTURE_2D,
                0,
                GL11.GL_RGBA8,
                width,
                height,
                0,
                GL11.GL_RGBA,
                GL11.GL_UNSIGNED_BYTE,
                0
            )
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT)
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT)
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST_MIPMAP_LINEAR)
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST)
            return tex
        }

        fun add(imageData: ImageData, opaque: Boolean): Boolean {
            require(!(imageData.width() != IMAGE_WIDTH || imageData.height() != IMAGE_HEIGHT)) { "Wrong image size: " + imageData.width() + "x" + imageData.height() }
            if (x + imageData.width() > width) {
                if (y + imageData.height() > height) {
                    return false
                }
                y += IMAGE_HEIGHT
                x = 0
            }
            if (tex == null) {
                tex = newTexture()
            }
            GL11.glTexSubImage2D(
                GL11.GL_TEXTURE_2D, 0, x, y, imageData.width(), imageData.height(),
                imageData.bestFormat(), GL11.GL_UNSIGNED_BYTE, imageData.image()
            )
            materials.add(Material(opaque, x / width.toFloat(), y / height.toFloat()))
            x += imageData.width()
            return true
        }

        fun build(): MaterialAtlas {
            check(!(tex == null || isEmpty)) { "Nothing to build!" }
            GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D)
            tex!!.unbind()
            val atlas = MaterialAtlas(tex, materials, sscale, tscale)
            tex = null
            y = 0
            x = y
            materials.clear()
            return atlas
        }
    }

    companion object {
        private const val IMAGE_WIDTH = 16 * 3
        private const val IMAGE_HEIGHT = 16
        private const val TEX_WIDTH = 512
        private const val TEX_HEIGHT = 512
    }
}
