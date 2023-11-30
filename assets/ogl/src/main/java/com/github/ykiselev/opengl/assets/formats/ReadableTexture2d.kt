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

package com.github.ykiselev.opengl.assets.formats;

import com.github.ykiselev.assets.Assets;
import com.github.ykiselev.assets.ReadableAsset;
import com.github.ykiselev.assets.Recipe;
import com.github.ykiselev.assets.ResourceException;
import com.github.ykiselev.opengl.OglRecipes;
import com.github.ykiselev.opengl.textures.DefaultTexture2d;
import com.github.ykiselev.opengl.textures.ImageData;
import com.github.ykiselev.opengl.textures.Texture2d;
import com.github.ykiselev.playground.assets.common.AssetUtils;
import com.github.ykiselev.wrap.Wrap;
import com.github.ykiselev.wrap.Wraps;

import java.nio.channels.ReadableByteChannel;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.GL_TEXTURE_BASE_LEVEL;
import static org.lwjgl.opengl.GL12.GL_TEXTURE_MAX_LEVEL;
import static org.lwjgl.opengl.GL30.glGenerateMipmap;

/**
 * Created by Y.Kiselev on 05.06.2016.
 */
public final class ReadableTexture2d implements ReadableAsset<Texture2d, ReadableTexture2d.Context> {

    public static final class Context {

        private final boolean mipMapped;

        public boolean isMipMapped() {
            return mipMapped;
        }

        public Context(boolean mipMapped) {
            this.mipMapped = mipMapped;
        }
    }

    @Override
    public Wrap<Texture2d> read(ReadableByteChannel channel, Recipe<?, Texture2d, Context> recipe, Assets assets) throws ResourceException {
        try (var image = AssetUtils.read(channel, OglRecipes.IMAGE_DATA, assets)) {
            final boolean isMipMapped = recipe.context() == null
                    || recipe.context().isMipMapped();
            return Wraps.of(loadTexture(image.value(), isMipMapped));
        }
    }

    private Texture2d loadTexture(ImageData imageData, boolean mipMapped) {
        final Texture2d texture = new DefaultTexture2d();
        texture.bind();
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST_MIPMAP_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glPixelStorei(GL_UNPACK_ALIGNMENT, imageData.alignment());
        glTexImage2D(GL_TEXTURE_2D, 0, imageData.bestInternalFormat(), imageData.width(), imageData.height(),
                0, imageData.bestFormat(), GL_UNSIGNED_BYTE, imageData.image());
        if (mipMapped) {
            glGenerateMipmap(GL_TEXTURE_2D);
        } else {
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_BASE_LEVEL, 0);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAX_LEVEL, 0);
        }
        texture.unbind();
        return texture;
    }
}
