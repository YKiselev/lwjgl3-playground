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
package com.github.ykiselev.openal.assets.vorbis

import com.github.ykiselev.openal.AudioSamples
import com.github.ykiselev.openal.Errors
import com.github.ykiselev.wrap.Wrap
import org.lwjgl.openal.AL10
import java.nio.ShortBuffer
import java.util.*

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
class VorbisAudio(
    private val format: Int,
    private val sampleRate: Int,
    private val length: Int,
    wrap: Wrap<ShortBuffer>
) : AudioSamples {
    private val wrap: Wrap<ShortBuffer>

    init {
        this.wrap = Objects.requireNonNull(wrap)
    }

    override fun close() {
        wrap.close()
    }

    override fun format(): Int {
        return format
    }

    override fun sampleRate(): Int {
        return sampleRate
    }

    override fun length(): Int {
        return length
    }

    override fun buffer(buffer: Int) {
        AL10.alBufferData(buffer, format, wrap.value(), sampleRate)
        Errors.assertNoAlErrors()
    }
}
