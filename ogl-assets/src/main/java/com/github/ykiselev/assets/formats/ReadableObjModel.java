package com.github.ykiselev.assets.formats;

import com.github.ykiselev.assets.Assets;
import com.github.ykiselev.assets.ReadableResource;
import com.github.ykiselev.assets.ResourceException;
import com.github.ykiselev.assets.formats.obj.ObjModel;
import com.github.ykiselev.assets.formats.obj.ObjModelBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.StandardCharsets;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class ReadableObjModel implements ReadableResource<ObjModel> {

    @Override
    public ObjModel read(ReadableByteChannel channel, String resource, Assets assets) throws ResourceException {
        try (BufferedReader reader = new BufferedReader(
                Channels.newReader(
                        channel,
                        StandardCharsets.UTF_8.newDecoder(), -1
                )
        )) {
            return parse(reader);// new ParsedObjModel(reader).parse();
        } catch (Exception e) {
            throw new ResourceException("Unable to load " + resource, e);
        }
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
