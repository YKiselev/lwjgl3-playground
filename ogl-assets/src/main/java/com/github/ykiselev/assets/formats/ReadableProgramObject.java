package com.github.ykiselev.assets.formats;

import com.github.ykiselev.assets.Assets;
import com.github.ykiselev.assets.ReadableResource;
import com.github.ykiselev.assets.ResourceException;
import com.github.ykiselev.opengl.shaders.ProgramObject;
import com.github.ykiselev.opengl.shaders.ShaderException;
import com.github.ykiselev.opengl.shaders.ShaderObject;
import com.github.ykiselev.opengl.shaders.VertexAttributeLocation;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Y.Kiselev on 15.05.2016.
 */
public final class ReadableProgramObject implements ReadableResource {

    @Override
    public ProgramObject read(URI resource, Assets assets) throws ResourceException {
        final Config fallback = assets.load("/shaders/fallback-prog.conf");
        final Config config;
        try (Reader reader = new InputStreamReader(assets.open(resource), StandardCharsets.UTF_8)) {
            config = ConfigFactory.parseReader(reader)
                    .withFallback(fallback);
        } catch (IOException e) {
            throw new ResourceException(e);
        }
        final List<String> locations = config.getStringList("vertex-attribute-locations");
        final List<VertexAttributeLocation> vertexAttributeLocations = new ArrayList<>(locations.size());
        int i = 0;
        for (String location : locations) {
            vertexAttributeLocations.add(new VertexAttributeLocation(i, location));
            i++;
        }
        final List<ShaderObject> shaders = new ArrayList<>();
        final Config shader = config.getConfig("shader");
        final ShaderObject vertexShader = tryLoadShader(resource, shader.getString("vertex"), assets);
        if (vertexShader != null) {
            shaders.add(vertexShader);
        }
        final ShaderObject fragmentShader = tryLoadShader(resource, shader.getString("fragment"), assets);
        if (fragmentShader != null) {
            shaders.add(fragmentShader);
        }
        try {
            return new ProgramObject(
                    vertexAttributeLocations,
                    config.getStringList("samplers"),
                    shaders.toArray(new ShaderObject[0])
            );
        } catch (ShaderException e) {
            throw new ResourceException(e);
        }
    }

    private ShaderObject tryLoadShader(URI base, String shader, Assets assets) throws ResourceException {
        if (StringUtils.isEmpty(shader)) {
            return null;
        }
        return assets.load(base.resolve(shader), null);
    }
}
