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

package com.github.ykiselev.assets.formats;

import com.github.ykiselev.assets.Assets;
import com.github.ykiselev.assets.ReadableResource;
import com.github.ykiselev.assets.ResourceException;
import com.github.ykiselev.buffers.Buffers;
import com.github.ykiselev.opengl.textures.Texture2d;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryUtil;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL11.GL_LINEAR;
import static org.lwjgl.opengl.GL11.GL_RED;
import static org.lwjgl.opengl.GL11.GL_REPEAT;
import static org.lwjgl.opengl.GL11.GL_RGB;
import static org.lwjgl.opengl.GL11.GL_RGBA;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_S;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_T;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE;
import static org.lwjgl.opengl.GL11.glTexImage2D;
import static org.lwjgl.opengl.GL11.glTexParameteri;
import static org.lwjgl.opengl.GL30.glGenerateMipmap;

/**
 * Created by Y.Kiselev on 05.06.2016.
 */
public class ReadableTexture implements ReadableResource<Texture2d> {

    private final Buffers buffers;

    public ReadableTexture(Buffers buffers) {
        this.buffers = buffers;
    }

    @Override
    public Texture2d read(InputStream inputStream, URI resource, Assets assets) throws ResourceException {
        final BufferedImage image;
        try {
            image = ImageIO.read(inputStream);
        } catch (IOException e) {
            throw new ResourceException(e);
        }
        final Texture2d texture = new Texture2d();
        texture.bind();
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        loadTexture(image, texture);
        // todo - how to pass flags?
        if (true) {
            glGenerateMipmap(GL_TEXTURE_2D);
        }
        texture.unbind();
        return texture;

    }

    private void loadTexture(BufferedImage image, Texture2d dest) {
        final int width = image.getWidth();
        final int height = image.getHeight();
        switch (image.getType()) {
            case BufferedImage.TYPE_USHORT_GRAY:
            case BufferedImage.TYPE_BYTE_GRAY:
                readByteGrayImage(image, width, height);
                break;

            case BufferedImage.TYPE_4BYTE_ABGR:
            case BufferedImage.TYPE_4BYTE_ABGR_PRE:
            case BufferedImage.TYPE_INT_ARGB:
            case BufferedImage.TYPE_INT_ARGB_PRE:
                readArgbImage(image, width, height);
                break;

            default:
                readRgbImage(image, width, height);
                break;
        }
    }

//    private void readRgba8() {
//        // Get pixel data as array of int-s
//        final int[] pixels = new int[w * h];
//        image.getRGB(0, 0, w, h, pixels, 0, w);
//
//        final int texId = GL11.glGenTextures();
//        GL11.glBindTexture(GL11.GL_TEXTURE_2D, texId);
//        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
//        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
//
//        // Convert to byte buffer
//        try (Buffers.Pooled<ByteBuffer> pooled = this.buffers.byteBuffer(pixels.length * 4)) {
//            final ByteBuffer buffer = pooled.buffer();
//
//            // Flip bitmap
//            int offset = w * (h - 1);
//            while (offset >= 0) {
//                for (int x = 0; x < w; x++) {
//                    final int pixel = pixels[offset + x];
//                    // Red
//                    buffer.put((byte) ((pixel >> 16) & 0xFF));
//                    // Green
//                    buffer.put((byte) ((pixel >> 8) & 0xFF));
//                    // Blue
//                    buffer.put((byte) (pixel & 0xFF));
//                    // Alpha
//                    buffer.put((byte) ((pixel >> 24) & 0xFF));
//                }
//                offset -= w;
//            }
//            buffer.flip();
//            GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA8, w, h, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);
//            //return new Texture2d(texId);
//        }
//    }

    //public Texture2d(int width, int height, ByteBuffer pixels, int internalFormat, int format, boolean generateMipMaps)
    private void readByteGrayImage(BufferedImage image, int width, int height) {
        final ByteBuffer pixels = BufferUtils.createByteBuffer(width * height);
        // flip image vertically
        for (int y = height - 1; y >= 0; y--) {
            for (int x = 0; x < width; x++) {
                final int value = image.getRGB(x, y);

                pixels.put((byte) (0xff & value));
            }
        }
        pixels.flip();
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RED, width, height, 0, GL_RED, GL_UNSIGNED_BYTE, pixels);
    }

    private void readArgbImage(BufferedImage image, int width, int height) {
        final int[] argb = image.getRGB(0, 0, width, height, null, 0, width);
        final ByteBuffer rgba = BufferUtils.createByteBuffer(width * height * 4);

        // flip vertically and convert argb to rgba
        for (int y = 0; y < height; y++) {
            final int srcOffset = (height - 1 - y) * width;
            for (int x = 0; x < width; x++) {
                final int value = argb[x + srcOffset];
                // r
                rgba.put((byte) (0xff & (value >> 16)));
                // g
                rgba.put((byte) (0xff & (value >> 8)));
                // b
                rgba.put((byte) (0xff & value));
                // a
                rgba.put((byte) (0xff & (value >>> 24)));
            }
        }
        rgba.flip();
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, rgba);
    }

    private void readRgbImage(BufferedImage image, int width, int height) {
        final int[] argb = image.getRGB(0, 0, width, height, null, 0, width);
        final ByteBuffer rgb = BufferUtils.createByteBuffer(width * height * 3);

        // flip vertically and convert argb to rgb
        for (int y = 0; y < height; y++) {
            final int srcOffset = (height - 1 - y) * width;
            for (int x = 0; x < width; x++) {
                final int value = argb[x + srcOffset];
                // r
                rgb.put((byte) (0xff & (value >> 16)));
                // g
                rgb.put((byte) (0xff & (value >> 8)));
                // b
                rgb.put((byte) (0xff & value));
            }
        }
        rgb.flip();
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, width, height, 0, GL_RGB, GL_UNSIGNED_BYTE, rgb);
    }

}
