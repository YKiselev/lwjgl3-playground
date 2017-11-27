package com.github.ykiselev.assets.formats;

import com.github.ykiselev.assets.Assets;
import com.github.ykiselev.assets.ReadableResource;
import com.github.ykiselev.assets.ResourceException;
import com.github.ykiselev.opengl.shaders.ShaderException;
import com.github.ykiselev.opengl.shaders.ShaderObject;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.lwjgl.opengl.GL20;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;

/**
 * Created by Y.Kiselev on 15.05.2016.
 */
public class ReadableShaderObject implements ReadableResource<ShaderObject> {

    @Override
    public ShaderObject read(URI resource, Assets assets) throws ResourceException {
        final String text;
        try (InputStream is = assets.open(resource)) {
            text = IOUtils.toString(is, StandardCharsets.UTF_8);

        } catch (IOException e) {
            throw new ResourceException(e);
        }
        try {
            return new ShaderObject(resolveType(resource), text);
        } catch (ShaderException e) {
            throw new ResourceException(e);
        }
    }

    private int resolveType(URI resource) {
        final String path = resource.getPath();
        if (StringUtils.endsWithIgnoreCase(path, ".frag")) {
            return GL20.GL_FRAGMENT_SHADER;
        } else if (StringUtils.endsWithIgnoreCase(path, ".vert")) {
            return GL20.GL_VERTEX_SHADER;
        }
        throw new IllegalArgumentException("Unknown shader type: " + resource);
    }
}
