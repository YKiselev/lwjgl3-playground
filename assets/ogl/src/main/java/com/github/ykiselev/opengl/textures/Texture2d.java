package com.github.ykiselev.opengl.textures;

import com.github.ykiselev.opengl.Bindable;
import com.github.ykiselev.opengl.Identified;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glDeleteTextures;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public interface Texture2d extends Identified, Bindable, AutoCloseable {

    @Override
    default void bind() {
        glBindTexture(GL_TEXTURE_2D, id());
    }

    @Override
    default void unbind() {
        glBindTexture(GL_TEXTURE_2D, 0);
    }

    @Override
    default void close() {
        glDeleteTextures(id());
    }
}