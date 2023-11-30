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
import com.github.ykiselev.opengl.OglRecipes
import com.github.ykiselev.opengl.shaders.DefaultProgramObject
import com.github.ykiselev.opengl.shaders.ProgramObject
import com.github.ykiselev.opengl.shaders.ShaderObject
import com.github.ykiselev.playground.assets.common.AssetUtils.read
import com.github.ykiselev.wrap.Wrap
import com.github.ykiselev.wrap.Wraps.of
import com.typesafe.config.Config
import org.apache.commons.lang3.StringUtils
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL20
import org.slf4j.LoggerFactory
import java.nio.channels.ReadableByteChannel

/**
 * Created by Y.Kiselev on 15.05.2016.
 */
class ReadableProgramObject : ReadableAsset<ProgramObject, Void> {

    private val logger = LoggerFactory.getLogger(javaClass)

    override fun read(
        channel: ReadableByteChannel,
        recipe: Recipe<*, ProgramObject, Void>?,
        assets: Assets
    ): Wrap<ProgramObject> {
        assets.load("progs/default/program-object.conf", OglRecipes.CONFIG).use { fallback ->
            read(channel, OglRecipes.CONFIG, assets).use { cfg ->
                val config = cfg.value()
                    .withFallback(fallback.value())
                val id = GL20.glCreateProgram()
                val shaders = readShaders(assets, config)
                for (w in shaders) {
                    GL20.glAttachShader(id, w.value().id())
                }
                val locations = config.getStringList("vertex-attribute-locations")
                for ((i, location) in locations.withIndex()) {
                    GL20.glBindAttribLocation(id, i, location)
                }
                GL20.glLinkProgram(id)
                val log = GL20.glGetProgramInfoLog(id, MAX_PROGRAM_LOG_LENGTH)
                val status = GL20.glGetProgrami(id, GL20.GL_LINK_STATUS)
                if (status != GL11.GL_TRUE) {
                    throw ResourceException(log)
                } else if (StringUtils.isNotEmpty(log)) {
                    logger.warn("Program link log: {}", log)
                }
                val program: ProgramObject = DefaultProgramObject(id, shaders.toTypedArray())
                val samplers = config.getStringList("samplers")
                if (samplers.isNotEmpty()) {
                    program.bind()
                    for ((unit, uniform) in samplers.withIndex()) {
                        GL20.glUniform1i(program.uniformLocation(uniform), unit)
                    }
                    program.unbind()
                }
                return of(program)
            }
        }
    }

    private fun readShaders(assets: Assets, config: Config): List<Wrap<ShaderObject>> =
        config.getConfig("shaders").root()
            .values
            .asSequence()
            .map { it.unwrapped() }
            .mapNotNull { String::class.java.cast(it) }
            .filter { it.isNotEmpty() }
            .map { uri: String ->
                assets.load<Any, ShaderObject, Any>(uri, null)
            }.toList()

    companion object {
        private const val MAX_PROGRAM_LOG_LENGTH = 8 * 1024
    }
}
