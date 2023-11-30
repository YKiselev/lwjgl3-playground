package com.github.ykiselev.opengl.assets.formats;

import com.github.ykiselev.assets.Assets;
import com.github.ykiselev.assets.ReadableAsset;
import com.github.ykiselev.assets.Recipe;
import com.github.ykiselev.assets.ResourceException;
import com.github.ykiselev.common.pools.ByteChannelAsByteBufferPool;
import com.github.ykiselev.opengl.textures.ImageData;
import com.github.ykiselev.wrap.Wrap;
import com.github.ykiselev.wrap.Wraps;
import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.channels.ReadableByteChannel;

import static org.lwjgl.stb.STBImage.stbi_failure_reason;
import static org.lwjgl.stb.STBImage.stbi_load_from_memory;
import static org.lwjgl.system.MemoryStack.stackPush;

public final class ReadableImageData implements ReadableAsset<ImageData, Void> {

    @Override
    public Wrap<ImageData> read(ReadableByteChannel channel, Recipe<?, ImageData, Void> recipe, Assets assets) throws ResourceException {
        final ByteBuffer image;
        final int width, height, components;
        try (Wrap<ByteBuffer> wrap = ByteChannelAsByteBufferPool.read(channel)) {
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
        return Wraps.of(new ImageData(image, width, height, components));
    }
}
