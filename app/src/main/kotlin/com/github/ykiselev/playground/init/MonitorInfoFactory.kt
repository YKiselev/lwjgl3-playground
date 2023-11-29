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
package com.github.ykiselev.playground.init

import com.github.ykiselev.spi.MonitorInfo
import org.lwjgl.glfw.GLFW
import org.lwjgl.system.MemoryStack

/**
 * @author Yuriy Kiselev (uze@yandex.ru)
 * @since 05.05.2019
 */
object MonitorInfoFactory {
    fun fromIndex(monitorIndex: Int): MonitorInfo {
        return fromHandle(getMonitor(monitorIndex))
    }

    fun fromHandle(monitor: Long): MonitorInfo {
        MemoryStack.stackPush().use { stack ->
            val xs = stack.mallocFloat(1)
            val ys = stack.mallocFloat(1)
            GLFW.glfwGetMonitorContentScale(monitor, xs, ys)
            return MonitorInfo(monitor, xs[0], ys[0])
        }
    }

    private fun getMonitor(index: Int): Long {
        if (index < 0) {
            return GLFW.glfwGetPrimaryMonitor()
        }
        val monitors = GLFW.glfwGetMonitors()
        return if (monitors == null || monitors.remaining() <= index) {
            GLFW.glfwGetPrimaryMonitor()
        } else monitors[index]
    }
}
