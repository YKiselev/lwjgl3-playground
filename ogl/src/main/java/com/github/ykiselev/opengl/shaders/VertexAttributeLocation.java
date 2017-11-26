package com.github.ykiselev.opengl.shaders;

/**
 * Created by Y.Kiselev on 08.05.2016.
 */
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
