package com.github.ykiselev.opengl.vertices;

import com.github.ykiselev.opengl.vbo.IndexBufferObject;
import com.github.ykiselev.opengl.vbo.VertexArrayObject;
import com.github.ykiselev.opengl.vbo.VertexBufferObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.lwjgl.opengl.GL11.GL_FLOAT;

public final class VaoConfigurer {

    private final VertexArrayObject vao;

    private IndexBufferObject ebo;

    private int index;

    VaoConfigurer(VertexArrayObject vao) {
        this.vao = Objects.requireNonNull(vao);
        vao.bind();
    }

    public VboConfigurer with(VertexBufferObject vbo) {
        return new VboConfigurer(vbo);
    }

    public VaoConfigurer with(IndexBufferObject ebo) {
        this.ebo = Objects.requireNonNull(ebo);
        ebo.bind();
        return this;
    }

    public static VaoConfigurer of(VertexArrayObject vao) {
        return new VaoConfigurer(vao);
    }

    public void end() {
        vao.unbind();
        if (ebo != null) {
            ebo.unbind();
        }
    }

    enum Components {
        FLOAT(GL_FLOAT, Float.BYTES);

        private final int type;

        private final int sizeInBytes;

        int type() {
            return type;
        }

        int sizeInBytes() {
            return sizeInBytes;
        }

        Components(int type, int sizeInBytes) {
            this.type = type;
            this.sizeInBytes = sizeInBytes;
        }
    }

    record Attribute(int components, Components component, int divisor) {

        int sizeInBytes() {
            return components * component.sizeInBytes();
        }
    }

    public final class VboConfigurer {

        private final VertexBufferObject vbo;

        private final List<Attribute> attributes = new ArrayList<>();

        VboConfigurer(VertexBufferObject vbo) {
            this.vbo = Objects.requireNonNull(vbo);
            vbo.bind();
        }

        public VboConfigurer floats(int components) {
            return floats(components, 0);
        }

        public VboConfigurer floats(int components, int divisor) {
            attributes.add(new Attribute(components, Components.FLOAT, divisor));
            return this;
        }

        public VaoConfigurer endVbo() {
            final int stride = attributes.stream()
                    .mapToInt(Attribute::sizeInBytes)
                    .sum();
            long offset = 0;
            for (Attribute attr : attributes) {
                VertexDefinitions.attribute(index, attr.components(), attr.component().type(), stride, offset);
                VertexDefinitions.divisor(index, attr.divisor());
                offset += attr.sizeInBytes();
                index += 1 + attr.sizeInBytes() / 16;
            }
            vbo.unbind();
            return VaoConfigurer.this;
        }
    }
}
