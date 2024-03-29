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

package com.github.ykiselev.opengl.buffers;

import com.github.ykiselev.opengl.textures.DefaultTexture2d;
import com.github.ykiselev.opengl.textures.Texture2d;

import static org.lwjgl.opengl.GL11.GL_NEAREST;
import static org.lwjgl.opengl.GL11.GL_RGBA;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE;
import static org.lwjgl.opengl.GL11.glTexImage2D;
import static org.lwjgl.opengl.GL11.glTexParameteri;
import static org.lwjgl.opengl.GL30.*;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class FrameBuffer implements AutoCloseable {

    private final FrameBufferObject fbo;

    private final Texture2d color;

    private final Texture2d depth;

    private final Texture2d normal;

    private int w, h;

    public Texture2d color() {
        return color;
    }

    public Texture2d depth() {
        return depth;
    }

    public Texture2d normal() {
        return normal;
    }

    public FrameBuffer() {
        fbo = new FrameBufferObject();
        color = new DefaultTexture2d();
        depth = new DefaultTexture2d();
        normal = new DefaultTexture2d();
    }

    /**
     * Should be called on un-bound frame buffer only!
     *
     * @param width  new width of frame buffer
     * @param height new height of frame buffer
     */
    public void size(int width, int height) {
        if (w != width || h != height) {
            bind();

            color.bind();
            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, 0);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
            glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, color.id(), 0);
            color.unbind();

            depth.bind();
            //glTexImage2D(GL_TEXTURE_2D, 0, GL_DEPTH_COMPONENT24, width, height, 0, GL_DEPTH_COMPONENT, GL_UNSIGNED_INT, 0);
            glTexImage2D(GL_TEXTURE_2D, 0, GL_DEPTH24_STENCIL8, width, height, 0, GL_DEPTH_STENCIL, GL_UNSIGNED_INT_24_8, 0);
            //glTexImage2D(GL_TEXTURE_2D, 0, GL_DEPTH_COMPONENT32F, width, height, 0, GL_DEPTH_COMPONENT, GL_FLOAT, 0);
            //glFramebufferTexture2D(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_TEXTURE_2D, depth.id(), 0);
            glFramebufferTexture2D(GL_FRAMEBUFFER, GL_DEPTH_STENCIL_ATTACHMENT, GL_TEXTURE_2D, depth.id(), 0);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
            depth.unbind();

            normal.bind();
            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, 0);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
            glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT1, GL_TEXTURE_2D, normal.id(), 0);
            normal.unbind();

            w = width;
            h = height;

            final int check = glCheckFramebufferStatus(GL_FRAMEBUFFER);
            if (check != GL_FRAMEBUFFER_COMPLETE) {
                throw new IllegalStateException("Frame buffer incomplete: " + check);
            }
            unbind();
        }
    }

    public void bind() {
        fbo.bind();
    }

    public void unbind() {
        fbo.unbind();
    }

    @Override
    public void close() {
        fbo.close();
        color.close();
        depth.close();
        normal.close();
    }
}
