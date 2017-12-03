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
import com.github.ykiselev.io.ByteChannelAsMemoryUtilByteBuffer;
import com.github.ykiselev.io.Wrap;
import com.github.ykiselev.opengl.textures.Texture2d;
import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.channels.ReadableByteChannel;

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
import static org.lwjgl.opengl.GL11.GL_UNPACK_ALIGNMENT;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE;
import static org.lwjgl.opengl.GL11.glPixelStorei;
import static org.lwjgl.opengl.GL11.glTexImage2D;
import static org.lwjgl.opengl.GL11.glTexParameteri;
import static org.lwjgl.opengl.GL30.glGenerateMipmap;
import static org.lwjgl.stb.STBImage.stbi_failure_reason;
import static org.lwjgl.stb.STBImage.stbi_image_free;
import static org.lwjgl.stb.STBImage.stbi_load_from_memory;
import static org.lwjgl.system.MemoryStack.stackPush;

/**
 * Created by Y.Kiselev on 05.06.2016.
 */
public final class ReadableTexture2d implements ReadableResource<Texture2d> {

    @Override
    public Texture2d read(ReadableByteChannel channel, String resource, Assets assets) throws ResourceException {
        final ByteBuffer image;
        final int width, height, components;
        try (Wrap<ByteBuffer> wrap = readResource(channel)) {
            try (MemoryStack stack = stackPush()) {
                final IntBuffer xb = stack.callocInt(1);
                final IntBuffer yb = stack.callocInt(1);
                final IntBuffer compb = stack.callocInt(1);
                image = stbi_load_from_memory(wrap.value(), xb, yb, compb, 0);
                if (image == null) {
                    throw new ResourceException("Unable to read image: " + stbi_failure_reason());
                }
                width = xb.get(0);
                height = yb.get(0);
                components = compb.get(0);
            }
        }
        try {
            return loadTexture(
                    image,
                    width,
                    height,
                    components
            );
        } finally {
            stbi_image_free(image);
        }
    }

    private Texture2d loadTexture(ByteBuffer image, int width, int height, int components) {
        final Texture2d texture = new Texture2d();
        texture.bind();
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        final int format;
        switch (components) {
            case 1:
                format = GL_RED;
                break;
            case 3:
                format = GL_RGB;
                break;
            case 4:
                format = GL_RGBA;
                break;
            default:
                throw new ResourceException("Unsupported number of components: " + components);
        }
        glPixelStorei(GL_UNPACK_ALIGNMENT, alignment(width * components));
        glTexImage2D(GL_TEXTURE_2D, 0, format, width, height, 0, format, GL_UNSIGNED_BYTE, image);
        // todo - how to pass flags?
        if (false) {
            glGenerateMipmap(GL_TEXTURE_2D);
        }
        texture.unbind();
        return texture;
    }

    /**
     * Choose best possible alignment
     */
    private int alignment(int widthInBytes) {
        int result = 8;
        if ((widthInBytes & 1) != 0) {
            result = 1;
        } else if ((widthInBytes & 2) != 0) {
            result = 2;
        } else if ((widthInBytes & 4) != 0) {
            result = 4;
        }
        return result;
    }

    private Wrap<ByteBuffer> readResource(ReadableByteChannel channel) {
        return new ByteChannelAsMemoryUtilByteBuffer(
                channel,
                8 * 1024
        ).read();
    }
}
