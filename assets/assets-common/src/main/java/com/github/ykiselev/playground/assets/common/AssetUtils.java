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

package com.github.ykiselev.playground.assets.common;

import com.github.ykiselev.assets.Assets;
import com.github.ykiselev.assets.Recipe;
import com.github.ykiselev.wrap.Wrap;

import java.nio.channels.ReadableByteChannel;

/**
 * @author Yuriy Kiselev (uze@yandex.ru)
 * @since 13.04.2019
 */
public final class AssetUtils {

    public static <K, A, C> Wrap<A> read(ReadableByteChannel channel, Recipe<K, A, C> recipe, Assets assets) {
        return assets.resolve(recipe)
                .read(channel, recipe, assets);
    }

    public static <K, A, C> Wrap<A> read(String resource, Recipe<K, A, C> recipe, Assets assets) {
        return assets.open(resource)
                .map(ch -> assets.resolve(resource, recipe).read(ch, recipe, assets))
                .orElseThrow();
    }

}
