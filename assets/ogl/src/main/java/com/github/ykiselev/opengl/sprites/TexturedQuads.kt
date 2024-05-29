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
package com.github.ykiselev.opengl.sprites

import com.github.ykiselev.opengl.matrices.MatrixOps
import com.github.ykiselev.opengl.shaders.ProgramObject
import com.github.ykiselev.opengl.shaders.uniforms.UniformVariable
import com.github.ykiselev.opengl.vbo.IndexBufferObject
import com.github.ykiselev.opengl.vbo.VertexArrayObject
import com.github.ykiselev.opengl.vbo.VertexBufferObject
import com.github.ykiselev.opengl.vertices.VertexDefinitions
import com.github.ykiselev.wrap.Wrap
import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL13
import org.lwjgl.opengl.GL15
import org.lwjgl.opengl.GL20
import org.lwjgl.system.MemoryStack
import org.lwjgl.system.MemoryUtil
import java.nio.ByteBuffer
import java.nio.FloatBuffer
import java.util.*
import java.util.function.IntConsumer

/**
 * Created by Uze on 17.01.2015.
 */
class TexturedQuads(program: Wrap<ProgramObject>) : AutoCloseable {
    /**
     * Maximum number of quads in a call to glDrawElements.
     */
    private var maxQuads = 0

    /**
     * One of GL_UNSIGNED_BYTE, GL_UNSIGNED_SHORT, GL_UNSIGNED_INT depending on the length of vertex buffer.
     */
    private var indexValueType = 0

    private val program: Wrap<ProgramObject> = Objects.requireNonNull(program)

    private val vertices: FloatBuffer

    private val colors: FloatBuffer

    private val vbo: VertexBufferObject

    private val vao: VertexArrayObject

    private val ebo: IndexBufferObject

    private val texUniform: UniformVariable

    private val mvpUniform: UniformVariable

    private val colorsUniform: UniformVariable

    private val matrix: FloatBuffer

    private var currentTexture = 0

    private var quadCounter = 0

    private var width = 0

    private var height = 0

    private var drawCount = 0

    fun width(): Int =
        width

    fun height(): Int =
        height

    fun drawCount(): Int =
        drawCount


    init {
        val prg = program.value()

        mvpUniform = prg.lookup("mvp")
        colorsUniform = prg.lookup("colors")
        texUniform = prg.lookup("tex")

        val info = prg.describe(colorsUniform)

        prg.bind()
        vao = VertexArrayObject()
        vao.bind()

        vbo = VertexBufferObject()
        vbo.bind()
        VertexDefinitions.POSITION2_TEXTURE2.apply()

        ebo = IndexBufferObject()
        ebo.bind()

        // Quad == 2 triangles == 6 indices (can't use stripes due to texture coordinates difference between quads)
        val maxIndices: Int
        MemoryStack.stackPush().use { ms ->
            this.maxQuads = info.size() and 4.inv()
            require(maxQuads >= 1) { "Invalid array length: \"colors[" + info.size() + "\"" }
            val type = info.type()
            require(type == GL20.GL_FLOAT_VEC4) { "Expected vec4 got different type: $type" }
            // Allocate indices
            maxIndices = maxQuads * 6
            this.indexValueType = if (maxIndices < 0xffff) GL11.GL_UNSIGNED_SHORT else GL11.GL_UNSIGNED_INT

            val indices = ms.malloc(4 * maxIndices)
            fillIndexData(indices)
            indices.flip()
            GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, indices, GL15.GL_STATIC_DRAW)
        }
        vao.unbind()
        vbo.unbind()
        ebo.unbind()
        prg.unbind()

        val maxVertices = maxQuads * 4 * VERTEX_SIZE_IN_FLOATS
        vertices = BufferUtils.createFloatBuffer(maxVertices)
        matrix = MemoryUtil.memAllocFloat(16)
        colors = MemoryUtil.memAllocFloat(maxQuads * 4)
    }

    private fun fillIndexData(b: ByteBuffer) {
        val c = when (indexValueType) {
            GL11.GL_UNSIGNED_BYTE -> IntConsumer { v: Int -> b.put(v.toByte()) }
            GL11.GL_UNSIGNED_SHORT -> IntConsumer { v: Int -> b.putShort(v.toShort()) }
            GL11.GL_UNSIGNED_INT -> IntConsumer { value: Int -> b.putInt(value) }
            else -> throw IllegalArgumentException("Bad index value type: $indexValueType")
        }
        var offset = 0
        for (i in 0 until maxQuads) {
            c.accept(offset)
            c.accept(offset + 1)
            c.accept(offset + 2)
            c.accept(offset + 2)
            c.accept(offset + 1)
            c.accept(offset + 3)
            offset += 4
        }
    }

    override fun close() {
        vbo.close()
        ebo.close()
        vao.close()
        program.close()
        MemoryUtil.memFree(matrix)
        MemoryUtil.memFree(colors)
    }

    private fun flush() {
        if (quadCounter <= 0) {
            return
        }
        vbo.bind()
        vertices.flip()
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, vertices, GL15.GL_STREAM_DRAW)

        colors.flip()
        colorsUniform.vector4(colors)

        GL11.glDrawElements(GL11.GL_TRIANGLES, quadCounter * 6, indexValueType, 0)

        colors.clear()
        vertices.clear()
        quadCounter = 0
        drawCount++
    }

    /**
     * Add new quad (4 vertices) to queue
     * <pre>
     * (2) --- (3)
     * |       |
     * |       |
     * (0) --- (1)
     *
     * (x0, y1) --- (x1, y1)
     * |                 |
     * |                 |
     * (x0, y0) --- (x1, y0)
     *
     * (s0, t1) --- (s1, t1)
     * |                 |
     * |                 |
     * (s0, t0) --- (s1, t0)
     *
    </pre> *
     *
     * Note that if you wish to draw quad with texture loaded from external resource then s, t must be assigned as such:
     * <pre>
     * (0, 0) --- (1, 0)
     * |             |
     * |             |
     * (0, 1) --- (1, 1)
    </pre> *
     * That is because OpenGL texture origin (0,0) is in lower left corner while most of the image formats have origin in the left top corner.
     * This leads to images drawn upside-down. To re-mediate this we need to flip image vertically either
     *
     *  1. During resource preparation step
     *  1. At run-time before actually submitting image to OpenGL
     *  1. By flipping texture t-coordinate (cheapest solution, but leads to increased api complexity)
     *
     *
     * Quad is rendered as two triangles with indices 0, 1, 2, 2, 1, 3
     *
     * @param color the RGBA color (0xff0000ff - red, 0x00ff00ff - green, 0x0000ffff - blue)
     */
    fun addQuad(x0: Float, y0: Float, s0: Float, t0: Float, x1: Float, y1: Float, s1: Float, t1: Float, color: Int) {
        if (quadCounter >= maxQuads) {
            flush()
        }

        vertices.put(x0).put(y0).put(s0).put(t0)
        vertices.put(x1).put(y0).put(s1).put(t0)
        vertices.put(x0).put(y1).put(s0).put(t1)
        vertices.put(x1).put(y1).put(s1).put(t1)

        Colors.putAsVector4(colors, color)

        quadCounter++
    }

    /**
     *
     */
    fun addQuad(
        x0: Float,
        y0: Float,
        s0: Float,
        t0: Float,
        x1: Float,
        y1: Float,
        s1: Float,
        t1: Float,
        r: Float,
        g: Float,
        b: Float,
        a: Float
    ) {
        if (quadCounter >= maxQuads) {
            flush()
        }

        vertices.put(x0).put(y0).put(s0).put(t0)
        vertices.put(x1).put(y0).put(s1).put(t0)
        vertices.put(x0).put(y1).put(s0).put(t1)
        vertices.put(x1).put(y1).put(s1).put(t1)
        colors.put(r).put(g).put(b).put(a)

        quadCounter++
    }

    fun use(texture: Int) {
        if (texture == 0) {
            flush()
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0)
        } else if (texture != currentTexture) {
            flush()
            GL13.glActiveTexture(GL13.GL_TEXTURE0)
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture)
        }
        currentTexture = texture
    }

    /**
     * @param x                   the left viewport coordinate
     * @param y                   the bottom viewport coordinate
     * @param width               the width of viewport
     * @param height              the height of viewport
     * @param enableAlphaBlending set to `true` to use alpha-blending
     */
    fun begin(x: Int, y: Int, width: Int, height: Int, enableAlphaBlending: Boolean) {
        this.width = width
        this.height = height

        GL11.glViewport(x, y, width, height)

        vao.bind()
        program.value().bind()
        use(0)

        if (enableAlphaBlending) {
            GL11.glEnable(GL11.GL_BLEND)
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
        } else {
            GL11.glDisable(GL11.GL_BLEND)
        }

        texUniform.value(0)

        MatrixOps.orthographic(x.toFloat(), (x + width).toFloat(), (y + height).toFloat(), y.toFloat(), -1f, 1f, matrix)
        mvpUniform.matrix4(false, matrix)

        drawCount = 0
    }

    fun end() {
        flush()
        use(0)
        vao.unbind()
        vbo.unbind()
        ebo.unbind()
        program.value().unbind()
        GL11.glDisable(GL11.GL_BLEND)
    }

    companion object {
        private const val VERTEX_SIZE_IN_FLOATS = 4
    }
}