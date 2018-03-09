package com.github.ykiselev.opengl.textures;

import com.github.ykiselev.lifetime.Manageable;
import com.github.ykiselev.opengl.Bindable;
import com.github.ykiselev.opengl.Identified;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glBindTexture;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public interface Texture2d extends Identified, Bindable, AutoCloseable, Manageable<Texture2d> {

    @Override
    default void bind() {
        glBindTexture(GL_TEXTURE_2D, id());
    }

    @Override
    default void unbind() {
        glBindTexture(GL_TEXTURE_2D, 0);
    }

    @Override
    void close();
}
