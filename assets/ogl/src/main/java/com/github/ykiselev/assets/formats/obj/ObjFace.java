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

package com.github.ykiselev.assets.formats.obj;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
final class ObjFace {

    private final String material;

    private final int[] indices;

    public int indexAt(int index) {
        return indices[index];
    }

    public int size() {
        return indices.length;
    }

    public String material() {
        return material;
    }

    ObjFace(String material, int[] indices) {
        this.material = material;
        this.indices = indices;
    }
}
