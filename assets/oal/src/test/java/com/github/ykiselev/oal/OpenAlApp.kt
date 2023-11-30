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
package com.github.ykiselev.oal

import com.github.ykiselev.assets.Assets
import com.github.ykiselev.assets.DefaultRecipe.Companion.of
import com.github.ykiselev.openal.AudioSamples
import com.github.ykiselev.openal.Errors
import com.github.ykiselev.openal.assets.ReadableVorbisAudio
import org.junit.jupiter.api.Assertions
import org.lwjgl.openal.*
import org.lwjgl.system.MemoryUtil
import org.mockito.Mockito
import java.nio.IntBuffer
import java.nio.channels.Channels

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
class OpenAlApp {

    private val readableResource = ReadableVorbisAudio()
    private val assets = Mockito.mock(Assets::class.java)

    private fun run() {
        val device = ALC10.alcOpenDevice(null as CharSequence?)
        require(device != MemoryUtil.NULL) { "No device found!" }
        try {
            val capabilities = ALC.createCapabilities(device)
            require(capabilities.OpenALC10) { "OpenAL 1.0 is not supported!" }
            println("OpenALC11: " + capabilities.OpenALC11)
            println("caps.ALC_EXT_EFX = " + capabilities.ALC_EXT_EFX)
            if (capabilities.OpenALC11) {
                val devices = ALUtil.getStringList(MemoryUtil.NULL, ALC11.ALC_ALL_DEVICES_SPECIFIER)
                if (devices == null) {
                    Errors.assertNoAlErrors()
                } else {
                    for (i in devices.indices) {
                        println(i.toString() + ": " + devices[i])
                    }
                }
            }
            val defaultDeviceSpecifier = ALC10.alcGetString(MemoryUtil.NULL, ALC10.ALC_DEFAULT_DEVICE_SPECIFIER)
            Assertions.assertNotNull(defaultDeviceSpecifier)
            println("Default device: $defaultDeviceSpecifier")
            val context = ALC10.alcCreateContext(device, null as IntBuffer?)
            EXTThreadLocalContext.alcSetThreadContext(context)
            AL.createCapabilities(capabilities)
            println("ALC_FREQUENCY: " + ALC10.alcGetInteger(device, ALC10.ALC_FREQUENCY) + "Hz")
            println("ALC_REFRESH: " + ALC10.alcGetInteger(device, ALC10.ALC_REFRESH) + "Hz")
            println("ALC_SYNC: " + (ALC10.alcGetInteger(device, ALC10.ALC_SYNC) == ALC10.ALC_TRUE))
            println("ALC_MONO_SOURCES: " + ALC10.alcGetInteger(device, ALC11.ALC_MONO_SOURCES))
            println("ALC_STEREO_SOURCES: " + ALC10.alcGetInteger(device, ALC11.ALC_STEREO_SOURCES))
            try {
                testPlayback()
            } finally {
                ALC10.alcMakeContextCurrent(MemoryUtil.NULL)
                ALC10.alcDestroyContext(context)
            }
        } finally {
            ALC10.alcCloseDevice(device)
        }
    }

    private fun testPlayback() {
        // generate buffers and sources
        val buffer = AL10.alGenBuffers()
        Errors.assertNoAlErrors()
        val source = AL10.alGenSources()
        Errors.assertNoAlErrors()
        readableResource.read(
            Channels.newChannel(javaClass.getResourceAsStream("/sample.ogg")),
            of(AudioSamples::class.java),
            assets
        ).use { samples ->
            samples.value().buffer(buffer)

            //let's loop the sound
            AL10.alSourcei(source, AL10.AL_LOOPING, AL10.AL_FALSE)
            Errors.assertNoAlErrors()
            if (false) {
                //set up source input
                AL10.alSourcei(source, AL10.AL_BUFFER, buffer)
                Errors.assertNoAlErrors()

                //play source 0
                AL10.alSourcePlay(source)
                Errors.assertNoAlErrors()

                // not sure if this is correct method to wait for sound to stop
                while (AL10.alGetSourcei(source, AL10.AL_SOURCE_STATE) != AL10.AL_STOPPED) {
                    Thread.sleep(10)
                }

                //stop source 0
                AL10.alSourceStop(source)
                Errors.assertNoAlErrors()
            } else {
                println("Queueing first buffer")
                AL10.alSourceQueueBuffers(source, buffer)
                Errors.assertNoAlErrors()
                println("Playing...")
                AL10.alSourcePlay(source)
                Errors.assertNoAlErrors()
                for (i in 0..4) {
                    var processed: Int
                    while (AL10.alGetSourcei(source, AL10.AL_BUFFERS_PROCESSED).also { processed = it } < 1) {
                        Thread.sleep(5)
                    }
                    println("Processed $processed")
                    for (k in 0 until processed) {
                        val b = AL10.alSourceUnqueueBuffers(source)
                        samples.value().buffer(b)
                        //bufferData(buffer, info, wrap.value());
                        AL10.alSourceQueueBuffers(source, b)
                        Errors.assertNoAlErrors()
                        AL10.alSourcePlay(source)
                        Errors.assertNoAlErrors()
                    }
                }
                println("Waiting for completion...")
                while (AL10.alGetSourcei(source, AL10.AL_BUFFERS_QUEUED) > 0) {
                    if (AL10.alGetSourcei(source, AL10.AL_BUFFERS_PROCESSED) > 0) {
                        AL10.alSourceUnqueueBuffers(source)
                        Errors.assertNoAlErrors()
                    }
                    Thread.sleep(5)
                }
                println("All is done!")
            }

            //delete buffers and sources
            AL10.alDeleteSources(source)
            Errors.assertNoAlErrors()
            AL10.alDeleteBuffers(buffer)
            Errors.assertNoAlErrors()
        }
    }

    companion object {

        @JvmStatic
        fun main(args: Array<String>) {
            OpenAlApp().run()
        }
    }
}
