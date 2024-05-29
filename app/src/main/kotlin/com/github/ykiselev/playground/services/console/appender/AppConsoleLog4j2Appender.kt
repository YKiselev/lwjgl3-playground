package com.github.ykiselev.playground.services.console.appender

import com.github.ykiselev.common.circular.ArrayCircularBuffer
import com.github.ykiselev.common.circular.CircularBuffer
import com.github.ykiselev.common.circular.SynchronizedCircularBuffer
import com.github.ykiselev.playground.services.console.CommandLine
import org.apache.logging.log4j.core.*
import org.apache.logging.log4j.core.appender.AbstractAppender
import org.apache.logging.log4j.core.config.Property
import org.apache.logging.log4j.core.config.plugins.Plugin
import org.apache.logging.log4j.core.config.plugins.PluginAttribute
import org.apache.logging.log4j.core.config.plugins.PluginElement
import org.apache.logging.log4j.core.config.plugins.PluginFactory
import java.io.Serializable


/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
@Plugin(name = "AppConsole", category = Core.CATEGORY_NAME, elementType = Appender.ELEMENT_TYPE)
class AppConsoleLog4j2Appender private constructor(
    name: String,
    filter: Filter?,
    layout: Layout<out Serializable>,
    bufferSize: Int
) : AbstractAppender(name, filter, layout, true, Property.EMPTY_ARRAY) {

    private val buffer: CircularBuffer<String> = SynchronizedCircularBuffer(
        ArrayCircularBuffer(String::class.java, bufferSize)
    )

    fun buffer(): CircularBuffer<String> {
        return buffer
    }

    override fun append(logEvent: LogEvent) {
        val marker = logEvent.marker
        if (marker != null) {
            if (marker.isInstanceOf(CommandLine.MARKER.name)) {
                buffer.write(logEvent.message.formattedMessage)
                return
            }
        }
        buffer.write(layout.toSerializable(logEvent).toString())
    }

    companion object {
        @PluginFactory
        @JvmStatic
        fun createAppender(
            @PluginAttribute("name") name: String,
            @PluginElement("Filter") filter: Filter?,
            @PluginElement("PatternLayout") layout: Layout<out Serializable>,
            @PluginAttribute("bufferSize") bufferSize: Int
        ): AppConsoleLog4j2Appender =
            AppConsoleLog4j2Appender(name, filter, layout, bufferSize)
    }
}