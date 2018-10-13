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

package com.github.ykiselev.opengl.sprites;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class SimpleColorTable implements ColorTable {

    private final int[] colors;

    public SimpleColorTable(int[] colors) {
        this.colors = colors;
    }

    @Override
    public int size() {
        return colors.length;
    }

    @Override
    public int color(int index) {
        return colors[index];
    }

}
