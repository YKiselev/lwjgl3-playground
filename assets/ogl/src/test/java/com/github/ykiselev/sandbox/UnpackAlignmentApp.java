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

package com.github.ykiselev.sandbox;

import org.lwjgl.opengl.GL11;
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;

import static org.lwjgl.glfw.GLFW.GLFW_CONTEXT_VERSION_MAJOR;
import static org.lwjgl.glfw.GLFW.GLFW_CONTEXT_VERSION_MINOR;
import static org.lwjgl.glfw.GLFW.GLFW_FALSE;
import static org.lwjgl.glfw.GLFW.GLFW_RESIZABLE;
import static org.lwjgl.glfw.GLFW.GLFW_VISIBLE;
import static org.lwjgl.glfw.GLFW.glfwWindowHint;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_LINEAR;
import static org.lwjgl.opengl.GL11.GL_MODELVIEW;
import static org.lwjgl.opengl.GL11.GL_NEAREST;
import static org.lwjgl.opengl.GL11.GL_PROJECTION;
import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.GL_REPEAT;
import static org.lwjgl.opengl.GL11.GL_RGB;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_S;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_T;
import static org.lwjgl.opengl.GL11.GL_UNPACK_ALIGNMENT;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glGenTextures;
import static org.lwjgl.opengl.GL11.glLoadIdentity;
import static org.lwjgl.opengl.GL11.glMatrixMode;
import static org.lwjgl.opengl.GL11.glOrtho;
import static org.lwjgl.opengl.GL11.glPixelStorei;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glScalef;
import static org.lwjgl.opengl.GL11.glTexCoord2f;
import static org.lwjgl.opengl.GL11.glTexImage2D;
import static org.lwjgl.opengl.GL11.glTexParameteri;
import static org.lwjgl.opengl.GL11.glVertex2f;
import static org.lwjgl.opengl.GL11.glViewport;

/**
 * Experimenting with {@link GL11#GL_UNPACK_ALIGNMENT}.
 *
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class UnpackAlignmentApp extends SandboxApp {

    private int texture;

    private int wh;

    private int ww;

    private int w;

    private int h;

    public static void main(String[] args) {
        new UnpackAlignmentApp().run();
    }

    @Override
    protected void update() {
        glEnable(GL_TEXTURE_2D);
        glClearColor(0.3f, 0.3f, 0.3f, 0f);

        glBindTexture(GL_TEXTURE_2D, texture);
        glClear(GL_COLOR_BUFFER_BIT);

        float scaleFactor = 1.0f;

        glPushMatrix();
        //glTranslatef(ww * 0.5f, wh * 0.5f, 0.0f);
        glScalef(scaleFactor, scaleFactor, 1f);
        //glTranslatef(-w * 0.5f, -h * 0.5f, 0.0f);

        glBegin(GL_QUADS);
        {
            glTexCoord2f(0.0f, 0.0f);
            glVertex2f(0.0f, 0.0f);

            glTexCoord2f(1.0f, 0.0f);
            glVertex2f(ww, 0.0f);

            glTexCoord2f(1.0f, 1.0f);
            glVertex2f(ww, wh);

            glTexCoord2f(0.0f, 1.0f);
            glVertex2f(0.0f, wh);
        }
        glEnd();

        glPopMatrix();
        glBindTexture(GL_TEXTURE_2D, 0);
    }

    @Override
    protected void frameBufferSizeChanged(long window, int width, int height) {
        glViewport(0, 0, width, height);
    }

    @Override
    protected void windowSizeChanged(long window, int width, int height) {
        this.ww = width;
        this.wh = height;
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        glOrtho(0.0, width, height, 0.0, -1.0, 1.0);
        glMatrixMode(GL_MODELVIEW);
    }

    @Override
    protected void setWindowHints() {
        glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE);
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 2);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 1);
    }

    @Override
    protected void init(String title, int width, int height) {
        super.init(title, width, height);
        w = 7;
        h = 8;
        final int pixels = w * h;
        final ByteBuffer buffer = MemoryUtil.memAlloc(3 * pixels + 100);
        try {
            for (int j = 0; j < h; j++) {
                for (int i = 0; i < w; i++) {
                    final boolean white = ((i & 1) != 0) ^ ((j & 1) != 0);
                    buffer.put((byte) (white ? 255 : 0));
                    buffer.put((byte) 0);
                    buffer.put((byte) 0);
                }
            }
            buffer.flip();
            texture = loadTexture(w, h, alignment(3 * w), buffer);
        } finally {
            MemoryUtil.memFree(buffer);
        }
    }

    private int alignment(int widthInBytes) {
        int result = 8;
        if ((widthInBytes & 1) != 0) {
            result = 1;
        } else if ((widthInBytes & 2) != 0) {
            result = 2;
        } else if ((widthInBytes & 4) != 0) {
            result = 4;
        }
        System.out.println("Unpack alignment is " + result);
        return result;
    }

    private int loadTexture(int width, int height, int alignment, ByteBuffer data) {
        final int id = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, id);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glPixelStorei(GL_UNPACK_ALIGNMENT, alignment);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, width, height, 0, GL_RGB, GL_UNSIGNED_BYTE, data);
        glBindTexture(GL_TEXTURE_2D, 0);
        return id;
    }

    private void run() {
        init("Unpack Alignment App", 400, 400);
        try {
            show();
            loop();
        } finally {
            destroy();
        }
    }
}
