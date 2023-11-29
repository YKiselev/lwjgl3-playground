package com.github.ykiselev.playground.services.assets

import com.github.ykiselev.assets.*
import com.github.ykiselev.common.closeables.Closeables
import com.github.ykiselev.openal.assets.ReadableVorbisAudio
import com.github.ykiselev.opengl.OglRecipes
import com.github.ykiselev.opengl.assets.formats.*
import com.github.ykiselev.spi.MonitorInfo
import com.github.ykiselev.wrap.Wrap
import org.lwjgl.opengl.GL20
import java.nio.channels.ReadableByteChannel
import java.util.*
import java.util.stream.Stream

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
class GameAssets(private val delegate: Assets) : Assets, AutoCloseable {

    @Throws(ResourceException::class)
    override fun <K, T, C> tryLoad(resource: String, recipe: Recipe<K, T, C>?, assets: Assets): Wrap<T>? =
        delegate.tryLoad(resource, recipe, assets)

    @Throws(ResourceException::class)
    override fun <K, T, C> resolve(resource: String, recipe: Recipe<K, T, C>): ReadableAsset<T, C> =
        delegate.resolve(resource, recipe)

    override fun close() {
        Closeables.closeIfNeeded(delegate)
    }

    @Throws(ResourceException::class)
    override fun open(resource: String): ReadableByteChannel? =
        delegate.open(resource)

    @Throws(ResourceException::class)
    override fun openAll(resource: String): Sequence<ReadableByteChannel> =
        delegate.openAll(resource)

    companion object {

        fun create(resources: Resources, monitorInfo: MonitorInfo): GameAssets {
            val readableConfig = ReadableConfig()
            val readableTexture2d = ReadableTexture2d()
            val byKey = mapOf(
                OglRecipes.CONFIG.key() to readableConfig,
                OglRecipes.PROGRAM.key() to ReadableProgramObject(),
                OglRecipes.SPRITE_FONT.key() to ReadableSpriteFont(),
                OglRecipes.SPRITE.key() to readableTexture2d,
                OglRecipes.MIP_MAP_TEXTURE.key() to readableTexture2d,
                OglRecipes.OBJ_MODEL.key() to ReadableObjModel(),
                OglRecipes.TRUE_TYPE_FONT_INFO.key() to ReadableTrueTypeFontInfo(monitorInfo.yScale),
                OglRecipes.FONT_ATLAS.key() to ReadableFontAtlas(512, 512),
                OglRecipes.MATERIAL_ATLAS.key() to ReadableMaterialAtlas(),
                OglRecipes.IMAGE_DATA.key() to ReadableImageData()
            )
            val byExtension = mapOf<String?, ReadableAsset<*, *>>(
                "vs" to ReadableShaderObject(GL20.GL_VERTEX_SHADER),
                "fs" to ReadableShaderObject(GL20.GL_FRAGMENT_SHADER),
                "png" to readableTexture2d,
                "jpg" to readableTexture2d,
                "conf" to readableConfig,
                "ogg" to ReadableVorbisAudio()
            )
            return GameAssets(
                ManagedAssets(
                    SimpleAssets(
                        resources,
                        CompositeReadableAssets(
                            ResourceByKey(byKey),
                            ResourceByExtension(byExtension)
                        )
                    )
                )
            )
        }
    }
}