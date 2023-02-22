package com.github.ykiselev.opengl.assets.formats;

import com.github.ykiselev.assets.Assets;
import com.github.ykiselev.assets.ReadableAsset;
import com.github.ykiselev.assets.Recipe;
import com.github.ykiselev.assets.ResourceException;
import com.github.ykiselev.opengl.OglRecipes;
import com.github.ykiselev.opengl.materials.Material;
import com.github.ykiselev.opengl.materials.MaterialAtlas;
import com.github.ykiselev.opengl.materials.Materials;
import com.github.ykiselev.opengl.textures.DefaultTexture2d;
import com.github.ykiselev.opengl.textures.ImageData;
import com.github.ykiselev.opengl.textures.Texture2d;
import com.github.ykiselev.playground.assets.common.AssetUtils;
import com.github.ykiselev.wrap.Wrap;
import com.github.ykiselev.wrap.Wraps;
import com.typesafe.config.Config;

import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.glGenerateMipmap;

public final class ReadableMaterials implements ReadableAsset<Materials, Void> {

    private static final int IMAGE_WIDTH = 16 * 3;

    private static final int IMAGE_HEIGHT = 16;

    @Override
    public Wrap<Materials> read(ReadableByteChannel channel, Recipe<?, Materials, Void> recipe, Assets assets) throws ResourceException {
        final List<MaterialAtlas> atlases = new ArrayList<>();
        try (Wrap<Config> mc = assets.load("materials/default/material.conf", OglRecipes.CONFIG);
             Wrap<Config> cfg = AssetUtils.read(channel, OglRecipes.CONFIG, assets)) {
            var builder = new MaterialAtlasBuilder(1024, 1024);
            cfg.value().getConfigList("blocks")
                    .stream()
                    .map(e -> e.withFallback(mc.value()))
                    .forEach(e -> {
                        try (Wrap<ImageData> wrp = assets.load(e.getString("asset"), OglRecipes.IMAGE_DATA)) {
                            if (!builder.add(wrp.value(), e.getBoolean("opaque"))) {
                                atlases.add(builder.build());
                            }
                        }
                    });

            if (!builder.isEmpty()) {
                atlases.add(builder.build());
            }
        }
        return Wraps.of(new Materials(atlases));
    }

    static final class MaterialAtlasBuilder {

        private Texture2d tex;

        private int width, height, x, y, index;

        private final float sscale, tscale;

        private final List<Material> materials = new ArrayList<>();

        MaterialAtlasBuilder(int width, int height) {
            this.width = width;
            this.height = height;
            this.sscale = 1f / width;
            this.tscale = 1f / height;
        }

        boolean isEmpty() {
            return x == 0 && y == 0;
        }

        private Texture2d newTexture() {
            Texture2d tex = new DefaultTexture2d();
            tex.bind();
            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, 0);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
            return tex;
        }

        boolean add(ImageData imageData, boolean opaque) {
            if (imageData.width() != IMAGE_WIDTH || imageData.height() != IMAGE_HEIGHT) {
                throw new IllegalArgumentException("Wrong image size: " + imageData.width() + "x" + imageData.height());
            }
            if (tex == null) {
                tex = newTexture();
            }
            if (x + imageData.width() > width) {
                y += IMAGE_HEIGHT;
                x = 0;
            }
            if (y + imageData.height() > height) {
                return false;
            }
            glTexSubImage2D(GL_TEXTURE_2D, 0, x, y, imageData.width(), imageData.height(),
                    imageData.bestFormat(), GL_UNSIGNED_BYTE, imageData.image());
            materials.add(new Material(opaque, x * sscale, y * tscale));
            x += imageData.width();
            return true;
        }

        MaterialAtlas build() {
            if (tex == null) {
                throw new IllegalStateException("Nothing to build!");
            }
            glGenerateMipmap(GL_TEXTURE_2D);
            tex.unbind();
            var atlas = new MaterialAtlas(tex, index, materials, sscale, tscale);
            tex = null;
            x = y = 0;
            index += materials.size();
            materials.clear();
            return atlas;
        }
    }
}
