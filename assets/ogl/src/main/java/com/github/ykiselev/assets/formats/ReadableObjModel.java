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
