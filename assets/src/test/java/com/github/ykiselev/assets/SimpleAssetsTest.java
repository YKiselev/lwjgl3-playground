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

package com.github.ykiselev.assets;

import org.junit.Before;
import org.junit.Test;

import java.nio.channels.ReadableByteChannel;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public class SimpleAssetsTest {

    private final Resources resources = mock(Resources.class);

    private final ReadableAssets readableAssets = mock(ReadableAssets.class);

    @SuppressWarnings("unchecked")
    private final ReadableAsset<Double> readableAsset = mock(ReadableAsset.class);

    private final Assets assets = new SimpleAssets(resources, readableAssets);

    @Before
    public void setUp() {
        when(resources.open(any(String.class)))
                .thenReturn(
                        Optional.of(mock(ReadableByteChannel.class))
                );
        when(readableAsset.read(any(ReadableByteChannel.class), any()))
                .thenReturn(Math.PI);
    }

    @Test
    public void shouldLoad() {
        when(readableAssets.resolve(any(String.class), eq(Double.class)))
                .thenReturn(readableAsset);
        when(readableAsset.read(any(ReadableByteChannel.class), eq(assets)))
                .thenReturn(Math.PI);
        assertEquals(Math.PI, assets.load("x", Double.class), 0.00001);
    }
}