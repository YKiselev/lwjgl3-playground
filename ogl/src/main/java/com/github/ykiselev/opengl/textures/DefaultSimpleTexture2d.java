package com.github.ykiselev.opengl.textures;

import static org.lwjgl.opengl.GL11.glGenTextures;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class DefaultSimpleTexture2d implements SimpleTexture2d {

    private final int id;

    public DefaultSimpleTexture2d(int id) {
        this.id = id;
    }

    public DefaultSimpleTexture2d() {
        this(glGenTextures());
    }

    @Override
    public int id() {
        return id;
    }
}
