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
import com.github.ykiselev.memory.MemAlloc;
import com.github.ykiselev.opengl.fonts.Bitmap;
import com.github.ykiselev.opengl.fonts.CodePoints;
import com.github.ykiselev.opengl.fonts.TrueTypeFont;
import com.github.ykiselev.wrap.Wrap;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.net.URL;
import java.util.Map;

/**
 * @author Yuriy Kiselev (uze@yandex.ru)
 * @since 10.04.2019
 */
public class ReadableFontAtlasTest {

    private final int bw = 32, bh = 32;

    private final ReadableAsset<Map<String, Wrap<TrueTypeFont>>> resource = new ReadableFontAtlas(() -> new Bitmap<>(bw, bh, new MemAlloc(bw * bh)));

    private final Assets assets = Mockito.mock(Assets.class);

    private final URL url = getClass().getResource("/font-atlas.conf");

    @Test
    public void shouldLoad() {
        Config cfg = ConfigFactory.load("font-atlas.conf");

        System.out.println(cfg.getConfig("fonts").root().entrySet());

        for (String s : cfg.getStringList("code-points")) {
            System.out.println(s);
        }

    }
}

