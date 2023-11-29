package com.github.ykiselev.playground.services.sound

import com.github.ykiselev.openal.Errors
import com.github.ykiselev.spi.services.configuration.PersistedConfiguration
import org.lwjgl.openal.*
import org.lwjgl.system.MemoryUtil
import org.slf4j.LoggerFactory
import java.nio.IntBuffer

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
internal class OpenAlSoundDevice(
    persistedConfiguration: PersistedConfiguration,
    private val device: Long
) : SoundDevice {

    private val logger = LoggerFactory.getLogger(javaClass)
    private val context: Long
    private var effectsLevel = 0
    private var musicLevel = 0
    private val subscriptions: AutoCloseable

    init {
        subscriptions = persistedConfiguration.wire()
            .withInt("sound.effects.level", { effectsLevel }, { value: Int -> setEffectsLevel(value) }, true)
            .withInt("sound.music.level", { musicLevel }, { v: Int -> musicLevel = v }, true)
            .build()
        val capabilities = ALC.createCapabilities(device)
        require(capabilities.OpenALC10) { "OpenAL 1.0 is not supported!" }
        logger.info("Is OpenALC11 ? {}, has ALC_EXT_EFX ? {}", capabilities.OpenALC11, capabilities.ALC_EXT_EFX)
        if (capabilities.OpenALC11) {
            val devices = ALUtil.getStringList(MemoryUtil.NULL, ALC11.ALC_ALL_DEVICES_SPECIFIER)
            if (devices == null) {
                Errors.assertNoAlErrors()
            } else {
                logger.info("All devices: {}", devices)
            }
        }
        val defaultDeviceSpecifier = ALC10.alcGetString(MemoryUtil.NULL, ALC10.ALC_DEFAULT_DEVICE_SPECIFIER)
        logger.info("Default device: {}", defaultDeviceSpecifier)
        val deviceSpecifier = ALC10.alcGetString(device, ALC10.ALC_DEVICE_SPECIFIER)
        logger.info("Device: {}", deviceSpecifier)
        context = ALC10.alcCreateContext(device, null as IntBuffer?)
        EXTThreadLocalContext.alcSetThreadContext(context)
        AL.createCapabilities(capabilities)
        logger.info(
            "ALC_FREQUENCY={} Hz, ALC_REFRESH={} Hz, ALC_SYNC={}, ALC_MONO_SOURCES={}, ALC_STEREO_SOURCES={}",
            ALC10.alcGetInteger(device, ALC10.ALC_FREQUENCY),
            ALC10.alcGetInteger(device, ALC10.ALC_REFRESH),
            ALC10.alcGetInteger(device, ALC10.ALC_SYNC) == ALC10.ALC_TRUE,
            ALC10.alcGetInteger(device, ALC11.ALC_MONO_SOURCES),
            ALC10.alcGetInteger(device, ALC11.ALC_STEREO_SOURCES)
        )
    }

    private fun setEffectsLevel(value: Int) {
        logger.info("Setting sound level to {}", value)
        effectsLevel = value
    }

    @Throws(Exception::class)
    override fun close() {
        ALC10.alcMakeContextCurrent(MemoryUtil.NULL)
        ALC10.alcDestroyContext(context)
        ALC10.alcCloseDevice(device)
        subscriptions.close()
    }
}