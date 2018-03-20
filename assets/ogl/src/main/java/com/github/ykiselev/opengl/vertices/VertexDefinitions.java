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
