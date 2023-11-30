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
import com.github.ykiselev.common.io.ByteChannelAsString
import com.github.ykiselev.opengl.shaders.DefaultShaderObject
import com.github.ykiselev.opengl.shaders.ShaderObject
import com.github.ykiselev.wrap.Wrap
import com.github.ykiselev.wrap.Wraps.of
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL20
import org.slf4j.LoggerFactory
import java.nio.channels.ReadableByteChannel
import java.nio.charset.StandardCharsets

/**
 * Created by Y.Kiselev on 15.05.2016.
 */
class ReadableShaderObject(private val type: Int) : ReadableAsset<ShaderObject, Void> {

    private val logger = LoggerFactory.getLogger(javaClass)

    override fun read(
        channel: ReadableByteChannel,
        recipe: Recipe<*, ShaderObject, Void>?,
        assets: Assets
    ): Wrap<ShaderObject> {
        val id = GL20.glCreateShader(type)
        GL20.glShaderSource(id, ByteChannelAsString(channel, StandardCharsets.UTF_8).read())
        GL20.glCompileShader(id)
        val status = GL20.glGetShaderi(id, GL20.GL_COMPILE_STATUS)
        val log: String? = GL20.glGetShaderInfoLog(id, MAX_SHADER_LOG_LENGTH)
        if (status != GL11.GL_TRUE) {
            throw ResourceException(log)
        } else {
            log?.also {
                logger.warn("Shader log: {}", it)
            }
        }
        return of(DefaultShaderObject(id))
    }

    companion object {
        private const val MAX_SHADER_LOG_LENGTH = 8 * 1024
    }
}
