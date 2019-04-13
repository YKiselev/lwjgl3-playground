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

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

/**
 * @author Yuriy Kiselev (uze@yandex.ru)
 * @since 10.04.2019
 */
public class ReadableFontAtlasTest {

    @Test
    public void shouldReadCodePoints() {
        assertArrayEquals(new int[]{' ', 'a', 'b', 'c', 'd', 'e', 'f', 'Z', 43981, 66561},
                ReadableFontAtlas.readCodePoints(Arrays.asList("\u0020", "a-f", "Z", "\uabcd", "\ud801\udc01")).toArray());
    }

    @Test
    @Disabled
    public void shouldLoad() {
        Config cfg = ConfigFactory.load("font-atlas.conf");

        System.out.println(cfg.getConfig("fonts").root().entrySet());

        for (String s : cfg.getStringList("code-points")) {
            System.out.println(s);
        }
    }
}

