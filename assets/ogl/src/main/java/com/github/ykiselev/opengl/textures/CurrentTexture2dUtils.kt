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
package com.github.ykiselev.opengl.textures

import com.github.ykiselev.common.memory.MemAlloc
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL30
import org.lwjgl.stb.STBIWriteCallback
import org.lwjgl.stb.STBImage
import org.lwjgl.stb.STBImageWrite
import java.nio.ByteBuffer
import java.util.function.Consumer

/**
 * Copies pixels of current 2D texture to supplied byte buffer.
 *
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
fun writeCurrentTexture2d(consumer: Consumer<ByteBuffer>) {
    val width = GL11.glGetTexLevelParameteri(GL11.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_WIDTH)
    val height = GL11.glGetTexLevelParameteri(GL11.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_HEIGHT)
    val depthType = GL11.glGetTexLevelParameteri(GL11.GL_TEXTURE_2D, 0, GL30.GL_TEXTURE_DEPTH_TYPE)
    val packAlignment = GL11.glGetInteger(GL11.GL_PACK_ALIGNMENT)
    val isRgb = depthType == GL11.GL_NONE
    val format = if (isRgb) GL11.GL_RGB else GL11.GL_DEPTH_COMPONENT
    val comps = if (isRgb) 3 else 1
    val strideInBytes = packAlignment * ((width * comps + packAlignment - 1) / packAlignment)
    MemAlloc(strideInBytes * height).use { wrap ->
        GL11.glGetTexImage(GL11.GL_TEXTURE_2D, 0, format, GL11.GL_UNSIGNED_BYTE, wrap.value())
        val callback: STBIWriteCallback = object : STBIWriteCallback() {
            override fun invoke(context: Long, data: Long, size: Int) {
                consumer.accept(
                    getData(data, size)
                )
            }
        }
        check(
            STBImageWrite.stbi_write_png_to_func(
                callback,
                0,
                width,
                height,
                comps,
                wrap.value(),
                strideInBytes
            )
        ) { "Write failed: " + STBImage.stbi_failure_reason() }
    }
}
