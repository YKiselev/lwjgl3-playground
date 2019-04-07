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
import com.github.ykiselev.assets.ResourceException;
import com.github.ykiselev.io.ByteChannelAsByteBuffer;
import com.github.ykiselev.opengl.fonts.TrueTypeFontInfo;
import com.github.ykiselev.wrap.Wrap;
import com.github.ykiselev.wrap.Wraps;
import com.typesafe.config.Config;

import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;

/**
 * @author Yuriy Kiselev (uze@yandex.ru)
 * @since 08.04.2019
 */
public final class ReadableTrueTypeFontInfo implements ReadableAsset<TrueTypeFontInfo> {

    @Override
    public Wrap<TrueTypeFontInfo> read(ReadableByteChannel channel, Assets assets) throws ResourceException {
        final Config fontConfig;
        final ReadableAsset<Config> readableConfig = assets.resolve(Config.class);
        try (Wrap<Config> fallback = assets.load("fonts/default/font.conf", Config.class)) {
            try (Wrap<Config> config = readableConfig.read(channel, assets)) {
                fontConfig = config.value()
                        .withFallback(fallback.value());
            }
        }
        final Wrap<ByteBuffer> ttf = assets.open(fontConfig.getString("asset"))
                .map(ch -> new ByteChannelAsByteBuffer(ch, 512 * 1024))
                .map(ByteChannelAsByteBuffer::read)
                .orElseThrow(() -> new ResourceException("Unable to load font: \"" + fontConfig.getString("asset") + "\""));

        final float fontSize = (float) fontConfig.getDouble("size");

        return Wraps.of(TrueTypeFontInfo.load(ttf, fontSize));
    }
}
