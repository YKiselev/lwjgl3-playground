package com.github.ykiselev.opengl.textures;

import com.github.ykiselev.opengl.Bindable;
import com.github.ykiselev.opengl.Identified;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glGenTextures;

/**
 * Created by Y.Kiselev on 05.06.2016.
 */
public class Texture implements Identified, Bindable {

    private final int id;

    public Texture(int id) {
        this.id = id;
    }

    public Texture() {
        this(glGenTextures());
    }

    @Override
    public void bind() {
        glBindTexture(GL_TEXTURE_2D, id);
        //todo Util.checkGLError();
    }

    @Override
    public void unbind() {
        glBindTexture(GL_TEXTURE_2D, 0);
    }

    @Override
    public int id() {
        return id;
    }
}
