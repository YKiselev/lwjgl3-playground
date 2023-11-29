package com.github.ykiselev.playground.services.config

import com.github.ykiselev.spi.services.FileSystem
import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory
import com.typesafe.config.ConfigMergeable
import com.typesafe.config.ConfigRenderOptions
import org.slf4j.LoggerFactory
import java.io.IOException
import java.io.UncheckedIOException
import java.nio.channels.Channels
import java.nio.channels.ReadableByteChannel
import java.nio.charset.StandardCharsets
import kotlin.concurrent.Volatile

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
internal class AppFileConfig(private val fileSystem: FileSystem) : FileConfig {

    private val options = ConfigRenderOptions.defaults()
        .setFormatted(true)
        .setOriginComments(false)
        .setJson(false)
    private val logger = LoggerFactory.getLogger(javaClass)

    @Volatile
    private var config = ConfigFactory.empty()

    override fun getValue(name: String): Any? {
        return if (config.hasPath(name)) {
            config.getAnyRef(name)
        } else null
    }

    override fun persist(name: String, config: Map<String, Any>) {
        logger.info("Saving config to {}...", name)
        try {
            fileSystem.truncate("app.conf").use { channel ->
                Channels.newWriter(channel, StandardCharsets.UTF_8).use { writer ->
                    writer.write(
                        ConfigFactory.parseMap(config)
                            .root()
                            .render(options)
                    )
                }
            }
        } catch (e: IOException) {
            throw UncheckedIOException(e)
        }
    }

    override fun load(name: String) {
        fileSystem.open(name)
            ?.let { channel: ReadableByteChannel -> readFromFile(channel) }
            ?.also { set(it) }
    }

    override fun loadAll(name: String) {
        set(fileSystem.openAll(name)
            .map { channel: ReadableByteChannel -> readFromFile(channel) }
            .fold(ConfigFactory.empty()) { obj: Config, configMergeable: ConfigMergeable? ->
                obj.withFallback(
                    configMergeable
                )
            })
    }

    private fun set(value: Config) {
        config = value.resolve()
    }

    private fun readFromFile(channel: ReadableByteChannel): Config =
        Channels.newReader(channel, StandardCharsets.UTF_8).use { reader ->
            ConfigFactory.parseReader(reader)
        }
}