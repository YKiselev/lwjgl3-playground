package com.github.ykiselev.playground.services.sound

import com.github.ykiselev.common.closeables.Closeables
import com.github.ykiselev.spi.services.SoundEffects
import com.github.ykiselev.spi.services.configuration.PersistedConfiguration
import org.apache.commons.lang3.StringUtils
import org.lwjgl.openal.ALC10
import org.lwjgl.system.MemoryUtil
import org.slf4j.LoggerFactory

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
class AppSoundEffects(cfg: PersistedConfiguration) : SoundEffects, AutoCloseable {

    private val logger = LoggerFactory.getLogger(javaClass)
    private val subscriptions: AutoCloseable
    private var deviceName: String? = null
    private var enabled = true
    private val device: SoundDevice

    init {
        subscriptions = cfg.wire()
            .withString("sound.device", { deviceName.orEmpty() }, ::setDeviceName, true)
            .withBoolean("sound.enabled", { enabled }, { v: Boolean -> enabled = v }, true)
            .build()
        device = if (enabled) {
            var device = ALC10.alcOpenDevice(deviceName)
            if (device == MemoryUtil.NULL) {
                deviceName = null
                device = ALC10.alcOpenDevice(null as CharSequence?)
                require(device != MemoryUtil.NULL) { "No device found!" }
            }
            OpenAlSoundDevice(cfg, device)
        } else {
            NullDevice()
        }
    }

    private fun setDeviceName(value: String) {
        if (StringUtils.equalsIgnoreCase(deviceName, value)) {
            return
        }
        logger.info("Setting device name to \"{}\"", value)
        deviceName = value
    }

    override fun close() {
        Closeables.closeAll(device, subscriptions)
    }
}