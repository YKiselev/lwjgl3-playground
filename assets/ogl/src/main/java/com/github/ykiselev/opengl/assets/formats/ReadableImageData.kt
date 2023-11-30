package com.github.ykiselev.opengl.assets.formats

import com.github.ykiselev.assets.Assets
import com.github.ykiselev.assets.ReadableAsset
import com.github.ykiselev.assets.Recipe
import com.github.ykiselev.assets.ResourceException
import com.github.ykiselev.common.pools.ByteChannelAsByteBufferPool
import com.github.ykiselev.opengl.textures.ImageData
import com.github.ykiselev.wrap.Wrap
import com.github.ykiselev.wrap.Wraps.of
import org.lwjgl.stb.STBImage
import org.lwjgl.system.MemoryStack
import java.nio.channels.ReadableByteChannel

class ReadableImageData : ReadableAsset<ImageData, Void> {

    override fun read(
        channel: ReadableByteChannel,
        recipe: Recipe<*, ImageData, Void>?,
        assets: Assets
    ): Wrap<ImageData> =
        ByteChannelAsByteBufferPool.read(channel).use { wrap ->
            MemoryStack.stackPush().use { stack ->
                val xb = stack.callocInt(1)
                val yb = stack.callocInt(1)
                val compb = stack.callocInt(1)
                val image = STBImage.stbi_load_from_memory(wrap.value(), xb, yb, compb, 0)
                    ?: throw ResourceException("Unable to read image: " + STBImage.stbi_failure_reason())
                of(ImageData(image, xb[0], yb[0], compb[0]))
            }
        }
}
