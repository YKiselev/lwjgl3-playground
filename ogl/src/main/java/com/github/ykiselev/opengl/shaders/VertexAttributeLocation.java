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

package com.github.ykiselev.opengl.shaders;

/**
 * Created by Y.Kiselev on 08.05.2016.
 */
@Deprecated
public final class VertexAttributeLocation {

    private final int index;

    private final String name;

    public int index() {
        return index;
    }

    public String name() {
        return name;
    }

    public VertexAttributeLocation(int index, String name) {
        this.index = index;
        this.name = name;
    }
}
