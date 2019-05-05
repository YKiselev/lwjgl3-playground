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

package com.github.ykiselev.playground.init;

import com.github.ykiselev.spi.MonitorInfo;
import org.lwjgl.PointerBuffer;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.system.MemoryStack;

import java.nio.FloatBuffer;

/**
 * @author Yuriy Kiselev (uze@yandex.ru)
 * @since 05.05.2019
 */
final class MonitorInfoFactory {

    static MonitorInfo fromIndex(int monitorIndex) {
        return fromHandle(getMonitor(monitorIndex));
    }

    static MonitorInfo fromHandle(long monitor) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            final FloatBuffer xs = stack.mallocFloat(1);
            final FloatBuffer ys = stack.mallocFloat(1);
            GLFW.glfwGetMonitorContentScale(monitor, xs, ys);
            return new MonitorInfo(monitor, xs.get(0), ys.get(0));
        }
    }

    private static long getMonitor(int index) {
        if (index < 0) {
            return GLFW.glfwGetPrimaryMonitor();
        }
        final PointerBuffer monitors = GLFW.glfwGetMonitors();
        if (monitors == null || monitors.remaining() <= index) {
            return GLFW.glfwGetPrimaryMonitor();
        }
        return monitors.get(index);
    }
}
