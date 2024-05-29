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
package com.github.ykiselev.base.game

import com.github.ykiselev.assets.Assets
import com.github.ykiselev.opengl.OglRecipes
import com.github.ykiselev.opengl.matrices.Matrix
import com.github.ykiselev.opengl.matrices.math
import com.github.ykiselev.opengl.models.GenericIndexedGeometry
import com.github.ykiselev.opengl.models.Pyramid
import com.github.ykiselev.opengl.shaders.uniforms.UniformVariable
import com.github.ykiselev.opengl.vertices.VertexDefinitions
import org.lwjgl.glfw.GLFW
import org.lwjgl.system.MemoryStack

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
class Pyramids(assets: Assets) : AutoCloseable {

    private val geometry: GenericIndexedGeometry

    private val program = assets.load("progs/colored.conf", OglRecipes.PROGRAM)

    private val mvpUniform: UniformVariable

    init {
        Pyramid().use { p ->
            geometry = GenericIndexedGeometry(VertexDefinitions.POSITION_COLOR, p)
        }
        mvpUniform = program.value().lookup("mvp")
    }

    fun draw(vp: Matrix) {
        geometry.begin()
        program.value().bind()
        val sec = GLFW.glfwGetTime()
        math {
            var rm = matrix()// ms.mallocFloat(16)
            var mvp = matrix()// ms.mallocFloat(16)

            // 1
            rm = rotation(0.0, 0.0, Math.toRadians(25.0 * sec % 360))
            mvp = vp * rm// multiply(vp, rm, mvp)
            mvpUniform.matrix4(false, mvp.m)
            geometry.draw()

            // 2
            rm = identity().translate( 5.0, 0.0, 0.0)
            mvp = rotation(0.0, Math.toRadians(15 * sec % 360), 0.0)

            //multiply(rm, mvp, rm)
            mvp = vp * (rm * mvp)
            //multiply(vp, rm, mvp)
            mvpUniform.matrix4(false, mvp.m)
            geometry.draw()

            // 3
            //????? rm = identity().translate(-2.0, 0.0, 0.0)
            rm = rotation(Math.toRadians(15 * sec % 360), 0.0, 0.0)
            mvp = vp * rm //multiply(vp, rm, mvp)
            mvpUniform.matrix4(false, mvp.m)
        }
        program.value().unbind()
        geometry.end()
    }

    override fun close() {
        geometry.close()
        program.close()
    }
}
