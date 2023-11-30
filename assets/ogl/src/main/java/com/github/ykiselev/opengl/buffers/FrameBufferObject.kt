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

import com.github.ykiselev.opengl.Bindable
import org.lwjgl.opengl.GL30

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
class FrameBufferObject(
    private val id: Int = GL30.glGenFramebuffers()
) : Bindable, AutoCloseable {

    override fun id(): Int {
        return id
    }

    override fun bind() {
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, id)
    }

    override fun unbind() {
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0)
    }

    override fun close() {
        GL30.glDeleteFramebuffers(id)
    }
}
