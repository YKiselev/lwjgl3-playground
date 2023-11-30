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
package com.github.ykiselev.opengl.buffers

import com.github.ykiselev.opengl.textures.DefaultTexture2d
import com.github.ykiselev.opengl.textures.Texture2d
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL30

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
class FrameBuffer : AutoCloseable {

    val color: Texture2d = DefaultTexture2d()
    val depth: Texture2d = DefaultTexture2d()
    val normal: Texture2d = DefaultTexture2d()
    private val fbo: FrameBufferObject = FrameBufferObject()
    private var w = 0
    private var h = 0

    /**
     * Should be called on un-bound frame buffer only!
     *
     * @param width  new width of frame buffer
     * @param height new height of frame buffer
     */
    fun size(width: Int, height: Int) {
        if (w != width || h != height) {
            bind()
            color.bind()
            GL11.glTexImage2D(
                GL11.GL_TEXTURE_2D,
                0,
                GL11.GL_RGBA,
                width,
                height,
                0,
                GL11.GL_RGBA,
                GL11.GL_UNSIGNED_BYTE,
                0
            )
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST)
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST)
            GL30.glFramebufferTexture2D(
                GL30.GL_FRAMEBUFFER,
                GL30.GL_COLOR_ATTACHMENT0,
                GL11.GL_TEXTURE_2D,
                color.id(),
                0
            )
            color.unbind()
            depth.bind()
            //glTexImage2D(GL_TEXTURE_2D, 0, GL_DEPTH_COMPONENT24, width, height, 0, GL_DEPTH_COMPONENT, GL_UNSIGNED_INT, 0);
            GL11.glTexImage2D(
                GL11.GL_TEXTURE_2D,
                0,
                GL30.GL_DEPTH24_STENCIL8,
                width,
                height,
                0,
                GL30.GL_DEPTH_STENCIL,
                GL30.GL_UNSIGNED_INT_24_8,
                0
            )
            //glTexImage2D(GL_TEXTURE_2D, 0, GL_DEPTH_COMPONENT32F, width, height, 0, GL_DEPTH_COMPONENT, GL_FLOAT, 0);
            //glFramebufferTexture2D(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_TEXTURE_2D, depth.id(), 0);
            GL30.glFramebufferTexture2D(
                GL30.GL_FRAMEBUFFER,
                GL30.GL_DEPTH_STENCIL_ATTACHMENT,
                GL11.GL_TEXTURE_2D,
                depth.id(),
                0
            )
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST)
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST)
            depth.unbind()
            normal.bind()
            GL11.glTexImage2D(
                GL11.GL_TEXTURE_2D,
                0,
                GL11.GL_RGBA,
                width,
                height,
                0,
                GL11.GL_RGBA,
                GL11.GL_UNSIGNED_BYTE,
                0
            )
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST)
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST)
            GL30.glFramebufferTexture2D(
                GL30.GL_FRAMEBUFFER,
                GL30.GL_COLOR_ATTACHMENT1,
                GL11.GL_TEXTURE_2D,
                normal.id(),
                0
            )
            normal.unbind()
            w = width
            h = height
            val check = GL30.glCheckFramebufferStatus(GL30.GL_FRAMEBUFFER)
            check(check == GL30.GL_FRAMEBUFFER_COMPLETE) { "Frame buffer incomplete: $check" }
            unbind()
        }
    }

    fun bind() {
        fbo.bind()
    }

    fun unbind() {
        fbo.unbind()
    }

    override fun close() {
        fbo.close()
        color.close()
        depth.close()
        normal.close()
    }
}
