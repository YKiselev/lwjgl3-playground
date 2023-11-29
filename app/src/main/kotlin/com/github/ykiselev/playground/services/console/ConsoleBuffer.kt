package com.github.ykiselev.playground.services.console

import com.github.ykiselev.common.circular.CircularBuffer
import com.github.ykiselev.opengl.sprites.SpriteBatch
import com.github.ykiselev.opengl.sprites.TextAttributes
import com.github.ykiselev.opengl.text.Font
import com.github.ykiselev.spi.services.layers.DrawingContext
import kotlin.math.max

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
class ConsoleBuffer(private val buffer: CircularBuffer<String?>) {

    internal enum class ScrollAction {
        NONE,
        PG_UP,
        PG_DOWN,
        SCROLL_UP,
        SCROLL_DOWN
    }

    private val snapshot: Array<String?> = arrayOfNulls(buffer.capacity())
    private var offset = 0
    private var scrollAction = ScrollAction.NONE

    fun pageUp() {
        scrollAction = ScrollAction.PG_UP
    }

    fun pageDown() {
        scrollAction = ScrollAction.PG_DOWN
    }

    fun scroll(delta: Double) {
        if (delta > 0) {
            scrollAction = ScrollAction.SCROLL_UP
        } else if (delta < 0) {
            scrollAction = ScrollAction.SCROLL_DOWN
        }
    }

    private fun calculateOffset(font: Font, lines: Array<String?>, count: Int, viewHeight: Int, width: Int) {
        val scrollSize = max(1.0, (viewHeight / 5).toDouble()).toInt()
        var totalHeight = 0
        for (i in count - 1 downTo 0) {
            totalHeight += font.height(lines[i], width)
        }
        val maxOffset = max(0.0, (totalHeight - viewHeight).toDouble()).toInt()
        when (scrollAction) {
            ScrollAction.NONE -> {}
            ScrollAction.PG_UP -> offset += viewHeight
            ScrollAction.PG_DOWN -> offset -= viewHeight
            ScrollAction.SCROLL_UP -> offset += scrollSize
            ScrollAction.SCROLL_DOWN -> offset -= scrollSize
        }
        if (offset > maxOffset) {
            offset = maxOffset
        }
        if (offset < 0) {
            offset = 0
        }
        scrollAction = ScrollAction.NONE
    }

    fun draw(ctx: DrawingContext, x0: Int, y0: Int, width: Int, height: Int) {
        val textAttributes: TextAttributes = ctx.textAttributes
        val font: Font = ctx.textAttributes.font()
        val lines = buffer.copyTo(snapshot)
        calculateOffset(font, snapshot, lines, height, width)
        val batch: SpriteBatch = ctx.batch
        var i = lines - 1
        var skipped = 0
        var y = y0 + font.lineSpace()
        while (i >= 0) {
            val line = snapshot[i]
            val lineHeight = font.height(line, width)
            if (skipped >= offset) {
                y += lineHeight
                batch.draw(x0, y, width, line, textAttributes)
            } else {
                skipped += lineHeight
            }
            if (y >= height) {
                break
            }
            i--
        }
    }
}