package com.github.ykiselev.assets.formats;

import com.github.ykiselev.assets.Assets;
import com.github.ykiselev.assets.ReadableResource;
import com.github.ykiselev.assets.ResourceException;
import com.github.ykiselev.assets.formats.obj.ObjModel;
import com.github.ykiselev.assets.formats.obj.ParsedObjModel;
import com.github.ykiselev.io.ByteChannelAsLines;

import java.nio.channels.ReadableByteChannel;
import java.nio.charset.StandardCharsets;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class ReadableObjModel implements ReadableResource<ObjModel> {

    @Override
    public ObjModel read(ReadableByteChannel channel, String resource, Assets assets) throws ResourceException {
        try (ByteChannelAsLines lines = new ByteChannelAsLines(channel, StandardCharsets.UTF_8)) {
            return new ParsedObjModel(lines).parse();
        } catch (Exception e) {
            throw new ResourceException("Unable to load " + resource, e);
        }
    }
}
