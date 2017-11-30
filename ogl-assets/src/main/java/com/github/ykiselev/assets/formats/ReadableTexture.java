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
import org.lwjgl.opengl.GL11;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.ByteBuffer;

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

        final int w = image.getWidth();
        final int h = image.getHeight();

        // Get pixel data as array of int-s
        final int[] pixels = new int[w * h];
        image.getRGB(0, 0, w, h, pixels, 0, w);

        final int texId = GL11.glGenTextures();
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, texId);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);

        // Convert to byte buffer
        try (Buffers.Pooled<ByteBuffer> pooled = this.buffers.byteBuffer(pixels.length * 4)) {
            final ByteBuffer buffer = pooled.buffer();

            // Flip bitmap
            int offset = w * (h - 1);
            while (offset >= 0) {
                for (int x = 0; x < w; x++) {
                    final int pixel = pixels[offset + x];
                    // Red
                    buffer.put((byte) ((pixel >> 16) & 0xFF));
                    // Green
                    buffer.put((byte) ((pixel >> 8) & 0xFF));
                    // Blue
                    buffer.put((byte) (pixel & 0xFF));
                    // Alpha
                    buffer.put((byte) ((pixel >> 24) & 0xFF));
                }
                offset -= w;
            }
            buffer.flip();
            GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA8, w, h, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);
            return new Texture2d(texId);
        }
    }
}
