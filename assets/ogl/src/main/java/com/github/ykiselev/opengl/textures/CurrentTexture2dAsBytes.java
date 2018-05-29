package com.github.ykiselev.opengl.textures;

import com.github.ykiselev.memory.MemAlloc;
import com.github.ykiselev.wrap.Wrap;
import org.lwjgl.stb.STBIWriteCallback;

import java.nio.ByteBuffer;
import java.util.function.Consumer;

import static org.lwjgl.opengl.GL11.GL_DEPTH_COMPONENT;
import static org.lwjgl.opengl.GL11.GL_NONE;
import static org.lwjgl.opengl.GL11.GL_PACK_ALIGNMENT;
import static org.lwjgl.opengl.GL11.GL_RGB;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_HEIGHT;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WIDTH;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE;
import static org.lwjgl.opengl.GL11.glGetInteger;
import static org.lwjgl.opengl.GL11.glGetTexImage;
import static org.lwjgl.opengl.GL11.glGetTexLevelParameteri;
import static org.lwjgl.opengl.GL30.GL_TEXTURE_DEPTH_TYPE;
import static org.lwjgl.stb.STBImage.stbi_failure_reason;
import static org.lwjgl.stb.STBImageWrite.stbi_write_png_to_func;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class CurrentTexture2dAsBytes {

    public void write(Consumer<ByteBuffer> consumer) {
        final int width = glGetTexLevelParameteri(GL_TEXTURE_2D, 0, GL_TEXTURE_WIDTH);
        final int height = glGetTexLevelParameteri(GL_TEXTURE_2D, 0, GL_TEXTURE_HEIGHT);
        final int depthType = glGetTexLevelParameteri(GL_TEXTURE_2D, 0, GL_TEXTURE_DEPTH_TYPE);
        final int packAlignment = glGetInteger(GL_PACK_ALIGNMENT);
        final boolean isRgb = depthType == GL_NONE;
        final int format = isRgb ? GL_RGB : GL_DEPTH_COMPONENT;
        final int comps = isRgb ? 3 : 1;
        final int strideInBytes = packAlignment * ((width * comps + packAlignment - 1) / packAlignment);
        try (Wrap<ByteBuffer> wrap = new MemAlloc(strideInBytes * height)) {
            glGetTexImage(GL_TEXTURE_2D, 0, format, GL_UNSIGNED_BYTE, wrap.value());
            final STBIWriteCallback callback = new STBIWriteCallback() {
                @Override
                public void invoke(long context, long data, int size) {
                    consumer.accept(
                            getData(data, size)
                    );
                }
            };
            if (!stbi_write_png_to_func(callback, 0, width, height, comps, wrap.value(), strideInBytes)) {
                throw new IllegalStateException("Write failed: " + stbi_failure_reason());
            }
        }
    }
}
