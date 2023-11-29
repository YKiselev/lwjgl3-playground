package com.github.ykiselev.playground.services.console

import com.github.ykiselev.assets.Assets
import com.github.ykiselev.common.circular.CircularBuffer
import com.github.ykiselev.playground.services.console.appender.AppConsoleLog4j2Appender
import com.github.ykiselev.spi.api.Named
import com.github.ykiselev.spi.services.commands.Commands
import com.github.ykiselev.spi.services.configuration.PersistedConfiguration
import com.github.ykiselev.spi.services.layers.UiLayers
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.core.LoggerContext
import java.util.stream.Collectors
import java.util.stream.Stream

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
object ConsoleFactory {
    fun create(
        configuration: PersistedConfiguration,
        commands: Commands,
        uiLayers: UiLayers,
        assets: Assets
    ): AppConsole {
        val search = { fragment: String ->
            Stream.concat(
                configuration.values(),
                commands.commands()
            ).filter { v: Named -> v.name.startsWith(fragment) }
                .sorted(Comparator.comparing(Named::name))
                .collect(Collectors.toList())
        }
        val console = AppConsole(
            commands,
            configuration,
            ConsoleBuffer(buffer),
            DefaultCommandLine(configuration, commands, 20, search),
            assets,
            uiLayers
        )
        uiLayers.add(console)
        return console
    }

    private val buffer: CircularBuffer<String?>
        get() {
            val context = LogManager.getContext(false) as LoggerContext
            val appender = context.configuration.getAppender<AppConsoleLog4j2Appender>("AppConsole")
                ?: throw IllegalStateException("Appender not found: \"AppConsole\". Check log4j2 configuration!")
            return appender.buffer()
        }
}