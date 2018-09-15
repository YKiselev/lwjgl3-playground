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

package com.github.ykiselev.opengl.vertices;

import com.github.ykiselev.opengl.vbo.VertexBufferObject;

import static org.lwjgl.opengl.GL11.GL_FLOAT;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public enum VertexDefinitions implements VertexDefinition {

    POSITION_COLOR {
        @Override
        public void apply(VertexBufferObject target) {
            final int stride = (3 + 3) * Float.BYTES;
            target.attribute(0, 3, GL_FLOAT, false, stride, 0);
            target.attribute(1, 3, GL_FLOAT, false, stride, (Float.BYTES * 3));

        }
    },
    POSITION2_TEXTURE2 {
        @Override
        public void apply(VertexBufferObject target) {
            final int stride = (2 + 2) * Float.BYTES;
            target.attribute(0, 2, GL_FLOAT, false, stride, 0);
            target.attribute(1, 2, GL_FLOAT, false, stride, (Float.BYTES * 2));
        }
    },
    POSITION_TEXTURE_NORMAL {
        @Override
        public void apply(VertexBufferObject target) {
            final int stride = (3 + 2 + 3) * Float.BYTES;
            target.attribute(0, 3, GL_FLOAT, false, stride, 0);
            target.attribute(1, 2, GL_FLOAT, false, stride, (Float.BYTES * 3));
            target.attribute(2, 3, GL_FLOAT, false, stride, (Float.BYTES * (3 + 2)));
        }
    };
}
