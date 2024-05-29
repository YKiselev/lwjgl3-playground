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
package com.github.ykiselev.opengl.matrices

import org.lwjgl.system.MemoryUtil


class MathArena : MatrixOps() {

    private val vec3fs = mutableListOf<Vector3f>()

    private val vec4fs = mutableListOf<Vector4f>()

    private val matrices = mutableListOf<Matrix>()

    @PublishedApi
    internal var vec3Idx = 0

    @PublishedApi
    internal var vec4Idx = 0

    @PublishedApi
    internal var matIdx = 0

    override fun vec3f(): Vector3f {
        if (vec3Idx >= vec3fs.size) {
            vec3fs.add(Vector3f())
        }
        return vec3fs[vec3Idx++]
    }

    override fun matrix(): Matrix {
        if (matIdx >= matrices.size) {
            matrices.add(Matrix(MemoryUtil.memAllocFloat(16)))
        }
        return matrices[matIdx++]
    }

    override fun vec4f(): Vector4f {
        if (vec4Idx >= vec4fs.size) {
            vec4fs.add(Vector4f())
        }
        return vec4fs[vec4Idx++]
    }

    inline operator fun invoke(block: MathArena.() -> Unit) {
        val oldVec3Idx = vec3Idx
        val oldVec4Idx = vec4Idx
        val oldMatIdx = matIdx
        try {
            block()
        } finally {
            vec3Idx = oldVec3Idx
            vec4Idx = oldVec4Idx
            matIdx = oldMatIdx
        }
    }
}

object TlsMathArena {

    private val TLS: ThreadLocal<MathArena> = ThreadLocal.withInitial {
        MathArena()
    }

    fun get(): MathArena = TLS.get()
}


inline fun math(block: MathArena.() -> Unit) {
    TlsMathArena.get()(block)
}
