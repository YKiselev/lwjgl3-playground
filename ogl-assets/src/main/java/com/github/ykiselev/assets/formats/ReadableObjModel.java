package com.github.ykiselev.assets.formats;

import com.github.ykiselev.assets.Assets;
import com.github.ykiselev.assets.ReadableResource;
import com.github.ykiselev.assets.ResourceException;
import com.github.ykiselev.assets.formats.obj.ObjModelBuilder;
import com.github.ykiselev.io.ByteChannelAsLines;
import com.github.ykiselev.opengl.models.ObjModel;

import java.nio.channels.ReadableByteChannel;
import java.nio.charset.StandardCharsets;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class ReadableObjModel implements ReadableResource<ObjModel> {

    @Override
    public ObjModel read(ReadableByteChannel channel, String resource, Assets assets) throws ResourceException {
        final ObjModelBuilder builder = new ObjModelBuilder();
        try {
            new ByteChannelAsLines(channel, StandardCharsets.UTF_8).consume(builder);
        } catch (Exception e) {
            throw new ResourceException("Unable to load " + resource, e);
        }
        return builder.build();
    }

}
