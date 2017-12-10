package com.github.ykiselev.assets.formats;

import com.github.ykiselev.assets.Assets;
import com.github.ykiselev.assets.ReadableResource;
import com.github.ykiselev.assets.formats.obj.ObjModel;
import org.junit.Test;

import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public class ReadableObjModelTest {

    private final ReadableResource<ObjModel> resource = new ReadableObjModel();

    private final Assets assets = mock(Assets.class);

    private final URL url = getClass().getResource("/models/2cubes.obj");

    @Test
    public void shouldRead() throws IOException {
        final ObjModel model = resource.read(
                Channels.newChannel(
                        url.openStream()
                ),
                url.toString(),
                assets
        );
        assertNotNull(model);
    }
}