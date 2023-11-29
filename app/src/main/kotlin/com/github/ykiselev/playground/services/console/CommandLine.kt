package com.github.ykiselev.playground.services.console

import com.github.ykiselev.spi.services.layers.DrawingContext
import org.slf4j.Marker
import org.slf4j.MarkerFactory

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
interface CommandLine {
    fun add(codePoint: Int)
    fun removeLeft()
    fun remove()
    fun left()
    fun right()
    fun draw(ctx: DrawingContext, x0: Int, y0: Int, width: Int, height: Int)
    fun begin()
    fun end()
    fun complete()

    /**
     * Clears command buffer, moves caret to initial position, cancels history search, clears fragments
     */
    fun reset()
    fun execute()
    fun searchHistoryBackward()
    fun searchHistory()

    companion object {
        @JvmField
        val MARKER: Marker = MarkerFactory.getMarker("COMMAND_LINE")
    }
}