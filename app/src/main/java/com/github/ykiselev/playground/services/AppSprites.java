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

package com.github.ykiselev.playground.services;

import com.github.ykiselev.assets.Assets;
import com.github.ykiselev.opengl.OglRecipes;
import com.github.ykiselev.opengl.sprites.ColorTable;
import com.github.ykiselev.opengl.sprites.DefaultSpriteBatch;
import com.github.ykiselev.opengl.sprites.SimpleColorTable;
import com.github.ykiselev.opengl.sprites.SpriteBatch;
import com.github.ykiselev.spi.services.layers.Sprites;

import static java.util.Objects.requireNonNull;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class AppSprites implements Sprites, AutoCloseable {

    private final Assets assets;

    /**
     * Xterm color table
     */
    private final ColorTable colorTable = new SimpleColorTable(
            new int[]{
                    0x000000ff, 0x800000ff, 0x008000ff, 0x808000ff, 0x000080ff, 0x800080ff, 0x008080ff, 0xc0c0c0ff,
                    0x808080ff, 0xff0000ff, 0x00ff00ff, 0xffff00ff, 0x0000ffff, 0xff00ffff, 0x00ffffff, 0xffffffff,
                    0x000000ff, 0x00005fff, 0x000087ff, 0x0000afff, 0x0000d7ff, 0x0000ffff,
                    0x005f00ff, 0x005f5fff, 0x005f87ff, 0x005fafff, 0x005fd7ff, 0x005fffff,
                    0x008700ff, 0x00875fff, 0x008787ff, 0x0087afff, 0x0087d7ff, 0x0087ffff,
                    0x00af00ff, 0x00af5fff, 0x00af87ff, 0x00afafff, 0x00afd7ff, 0x00afffff,
                    0x00d700ff, 0x00d75fff, 0x00d787ff, 0x00d7afff, 0x00d7d7ff, 0x00d7ffff,
                    0x00ff00ff, 0x00ff5fff, 0x00ff87ff, 0x00ffafff, 0x00ffd7ff, 0x00ffffff,
                    0x5f0000ff, 0x5f005fff, 0x5f0087ff, 0x5f00afff, 0x5f00d7ff, 0x5f00ffff,
                    0x5f5f00ff, 0x5f5f5fff, 0x5f5f87ff, 0x5f5fafff, 0x5f5fd7ff, 0x5f5fffff,
                    0x5f8700ff, 0x5f875fff, 0x5f8787ff, 0x5f87afff, 0x5f87d7ff, 0x5f87ffff,
                    0x5faf00ff, 0x5faf5fff, 0x5faf87ff, 0x5fafafff, 0x5fafd7ff, 0x5fafffff,
                    0x5fd700ff, 0x5fd75fff, 0x5fd787ff, 0x5fd7afff, 0x5fd7d7ff, 0x5fd7ffff,
                    0x5fff00ff, 0x5fff5fff, 0x5fff87ff, 0x5fffafff, 0x5fffd7ff, 0x5fffffff,
                    0x870000ff, 0x87005fff, 0x870087ff, 0x8700afff, 0x8700d7ff, 0x8700ffff,
                    0x875f00ff, 0x875f5fff, 0x875f87ff, 0x875fafff, 0x875fd7ff, 0x875fffff,
                    0x878700ff, 0x87875fff, 0x878787ff, 0x8787afff, 0x8787d7ff, 0x8787ffff,
                    0x87af00ff, 0x87af5fff, 0x87af87ff, 0x87afafff, 0x87afd7ff, 0x87afffff,
                    0x87d700ff, 0x87d75fff, 0x87d787ff, 0x87d7afff, 0x87d7d7ff, 0x87d7ffff,
                    0x87ff00ff, 0x87ff5fff, 0x87ff87ff, 0x87ffafff, 0x87ffd7ff, 0x87ffffff,
                    0xaf0000ff, 0xaf005fff, 0xaf0087ff, 0xaf00afff, 0xaf00d7ff, 0xaf00ffff,
                    0xaf5f00ff, 0xaf5f5fff, 0xaf5f87ff, 0xaf5fafff, 0xaf5fd7ff, 0xaf5fffff,
                    0xaf8700ff, 0xaf875fff, 0xaf8787ff, 0xaf87afff, 0xaf87d7ff, 0xaf87ffff,
                    0xafaf00ff, 0xafaf5fff, 0xafaf87ff, 0xafafafff, 0xafafd7ff, 0xafafffff,
                    0xafd700ff, 0xafd75fff, 0xafd787ff, 0xafd7afff, 0xafd7d7ff, 0xafd7ffff,
                    0xafff00ff, 0xafff5fff, 0xafff87ff, 0xafffafff, 0xafffd7ff, 0xafffffff,
                    0xd70000ff, 0xd7005fff, 0xd70087ff, 0xd700afff, 0xd700d7ff, 0xd700ffff,
                    0xd75f00ff, 0xd75f5fff, 0xd75f87ff, 0xd75fafff, 0xd75fd7ff, 0xd75fffff,
                    0xd78700ff, 0xd7875fff, 0xd78787ff, 0xd787afff, 0xd787d7ff, 0xd787ffff,
                    0xd7af00ff, 0xd7af5fff, 0xd7af87ff, 0xd7afafff, 0xd7afd7ff, 0xd7afffff,
                    0xd7d700ff, 0xd7d75fff, 0xd7d787ff, 0xd7d7afff, 0xd7d7d7ff, 0xd7d7ffff,
                    0xd7ff00ff, 0xd7ff5fff, 0xd7ff87ff, 0xd7ffafff, 0xd7ffd7ff, 0xd7ffffff,
                    0xff0000ff, 0xff005fff, 0xff0087ff, 0xff00afff, 0xff00d7ff, 0xff00ffff,
                    0xff5f00ff, 0xff5f5fff, 0xff5f87ff, 0xff5fafff, 0xff5fd7ff, 0xff5fffff,
                    0xff8700ff, 0xff875fff, 0xff8787ff, 0xff87afff, 0xff87d7ff, 0xff87ffff,
                    0xffaf00ff, 0xffaf5fff, 0xffaf87ff, 0xffafafff, 0xffafd7ff, 0xffafffff,
                    0xffd700ff, 0xffd75fff, 0xffd787ff, 0xffd7afff, 0xffd7d7ff, 0xffd7ffff,
                    0xffff00ff, 0xffff5fff, 0xffff87ff, 0xffffafff, 0xffffd7ff, 0xffffffff,
                    0x080808ff, 0x121212ff, 0x1c1c1cff, 0x262626ff, 0x303030ff, 0x3a3a3aff,
                    0x444444ff, 0x4e4e4eff, 0x585858ff, 0x606060ff, 0x666666ff, 0x767676ff,
                    0x808080ff, 0x8a8a8aff, 0x949494ff, 0x9e9e9eff, 0xa8a8a8ff, 0xb2b2b2ff,
                    0xbcbcbcff, 0xc6c6c6ff, 0xd0d0d0ff, 0xdadadaff, 0xe4e4e4ff, 0xeeeeeeff
            }
    );

    public AppSprites(Assets assets) {
        this.assets = requireNonNull(assets);
    }

    @Override
    public SpriteBatch newBatch() {
        return new DefaultSpriteBatch(
                assets.load("progs/sprite-batch.conf", OglRecipes.PROGRAM),
                assets.load("images/white.png", OglRecipes.SPRITE),
                colorTable
        );
    }

    @Override
    public void close() {
    }
}
