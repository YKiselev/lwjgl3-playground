package com.github.ykiselev.assets.formats;

import com.github.ykiselev.assets.Assets;
import com.github.ykiselev.assets.ReadableResource;
import com.github.ykiselev.assets.ResourceException;
import com.github.ykiselev.opengl.shaders.ShaderException;
import com.github.ykiselev.opengl.shaders.ViewShader;

import java.net.URI;

/**
 * Created by Y.Kiselev on 15.06.2016.
 */
public final class ReadableViewShader implements ReadableResource<ViewShader> {

    @Override
    public ViewShader read(URI resource, Assets assets) throws ResourceException {
        try {
            return new ViewShader(
                    assets.load(resource, null)
            );
        } catch (ShaderException e) {
            throw new ResourceException("Failed to load " + resource, e);
        }
    }
}
