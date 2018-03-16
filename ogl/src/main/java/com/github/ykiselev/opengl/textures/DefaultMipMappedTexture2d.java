package com.github.ykiselev.opengl.textures;

import static org.lwjgl.opengl.GL11.glGenTextures;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class DefaultMipMappedTexture2d implements MipMappedTexture2d {

    private final int id;

    public DefaultMipMappedTexture2d(int id) {
        this.id = id;
    }

    public DefaultMipMappedTexture2d() {
        this(glGenTextures());
    }

    @Override
    public int id() {
        return id;
    }
}
