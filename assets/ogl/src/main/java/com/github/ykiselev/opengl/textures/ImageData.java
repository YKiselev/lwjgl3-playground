package com.github.ykiselev.opengl.textures;

import com.github.ykiselev.assets.ResourceException;

import java.nio.ByteBuffer;
import java.util.Objects;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.stb.STBImage.stbi_image_free;

public final class ImageData implements AutoCloseable {

    private final ByteBuffer image;

    private final int width, height, components;

    public ImageData(ByteBuffer image, int width, int height, int components) {
        this.image = Objects.requireNonNull(image);
        this.width = width;
        this.height = height;
        this.components = components;
    }

    public ByteBuffer image() {
        return image;
    }

    public int width() {
        return width;
    }

    public int height() {
        return height;
    }

    public int components() {
        return components;
    }

    public int bestFormat() {
        return bestFormat(components);
    }

    public int bestInternalFormat() {
        return bestInternalFormat(components);
    }

    public static int bestInternalFormat(int components) {
        return switch (components) {
            case 1 -> GL_RED;
            case 3 -> GL_RGBA8;
            case 4 -> GL_RGBA8;
            default -> throw new ResourceException("Unsupported number of components: " + components);
        };
    }

    public static int bestFormat(int components) {
        return switch (components) {
            case 1 -> GL_RED;
            case 3 -> GL_RGB;
            case 4 -> GL_RGBA;
            default -> throw new ResourceException("Unsupported number of components: " + components);
        };
    }

    @Override
    public void close() {
        stbi_image_free(image);
    }

    @Override
    public String toString() {
        return "ImageData{" +
                "width=" + width +
                ", height=" + height +
                ", components=" + components +
                '}';
    }

    public int alignment() {
        return calcAlignment(width * components);
    }

    public static int calcAlignment(int widthInBytes) {
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
}
