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

package com.github.ykiselev.assets.formats;

import com.github.ykiselev.assets.Assets;
import com.github.ykiselev.assets.ReadableAsset;
import com.github.ykiselev.assets.ResourceException;
import com.github.ykiselev.assets.formats.obj.ObjModel;
import com.github.ykiselev.assets.formats.obj.ObjModelBuilder;
import com.github.ykiselev.wrap.Wrap;
import com.github.ykiselev.wrap.Wraps;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.StandardCharsets;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class ReadableObjModel implements ReadableAsset<ObjModel> {

    @Override
    public Wrap<ObjModel> read(ReadableByteChannel channel, Assets assets) throws ResourceException {
        try (BufferedReader reader = reader(channel)) {
            return Wraps.simple(
                    parse(reader)
            );
        } catch (Exception e) {
            throw new ResourceException(e);
        }
    }

    private BufferedReader reader(ReadableByteChannel channel) {
        return new BufferedReader(
                Channels.newReader(
                        channel,
                        StandardCharsets.UTF_8.newDecoder(), -1
                )
        );
    }

    private ObjModel parse(BufferedReader reader) throws IOException {
        final ObjModelBuilder builder = new ObjModelBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            builder.parseLine(line);
        }
        return builder.build();
    }
}
