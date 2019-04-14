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

package com.github.ykiselev.opengl.assets.formats;

import com.github.ykiselev.assets.Assets;
import com.github.ykiselev.assets.DefaultRecipe;
import com.github.ykiselev.assets.ReadableAsset;
import com.github.ykiselev.opengl.assets.formats.obj.ObjModel;
import com.github.ykiselev.wrap.Wrap;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public class ReadableObjModelTest {

    private final ReadableAsset<ObjModel, Void> resource = new ReadableObjModel();

    private final Assets assets = Mockito.mock(Assets.class);

    private final URL url = getClass().getResource("/models/2cubes.obj");

    @Test
    public void shouldRead() throws IOException {
        final Wrap<ObjModel> model = resource.read(
                Channels.newChannel(
                        url.openStream()
                ),
                DefaultRecipe.of(ObjModel.class),
                assets
        );
        assertNotNull(model);
    }
}