package com.github.ykiselev.opengl.textures;

import com.github.ykiselev.common.closeables.Closeables;

import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_LINEAR;
import static org.lwjgl.opengl.GL30.glGenerateMipmap;
@Deprecated
public final class TextureAtlas2d implements AutoCloseable {

    public static final class Item {

        private int id, x0, y0, width, height;

        private float soff, toff;
    }

    private final int width, height;

    private final Texture2d texture;


    public TextureAtlas2d(int width, int height) {
        this.width = width;
        this.height = height;
        texture = new DefaultTexture2d();
        texture.bind();
        try {
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST_MIPMAP_LINEAR);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, (ByteBuffer) null);
            glGenerateMipmap(GL_TEXTURE_2D);
        } finally {
            texture.unbind();
        }
    }

    @Override
    public void close() throws Exception {
        Closeables.closeAll(texture);
    }
}
