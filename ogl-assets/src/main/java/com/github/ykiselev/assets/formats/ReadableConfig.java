package com.github.ykiselev.assets.formats;

import com.github.ykiselev.assets.Assets;
import com.github.ykiselev.assets.ReadableResource;
import com.github.ykiselev.assets.ResourceException;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URI;
import java.nio.charset.StandardCharsets;

/**
 * Created by Y.Kiselev on 14.06.2016.
 */
public final class ReadableConfig implements ReadableResource<Config> {

    @Override
    public Config read(URI resource, Assets assets) throws ResourceException {
        try (Reader reader = new InputStreamReader(assets.open(resource), StandardCharsets.UTF_8)) {
            return ConfigFactory.parseReader(reader);
        } catch (IOException e) {
            throw new ResourceException(e);
        }
    }
}
