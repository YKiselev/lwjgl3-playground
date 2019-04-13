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

package com.github.ykiselev.common.memory;

import com.github.ykiselev.wrap.Wrap;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class MemAllocFloat extends Wrap<FloatBuffer> {

    public MemAllocFloat(int size) {
        super(MemoryUtil.memAllocFloat(size));
    }

    @Override
    public void close() {
        MemoryUtil.memFree(value());
    }
}
