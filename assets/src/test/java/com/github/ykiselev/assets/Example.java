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

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.HashMap;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class Example {

    public static void main(String[] args) {
        // 1
        Resources resources = resource -> Optional.of(
                Channels.newChannel(
                        Example.class.getResourceAsStream(resource)
                )
        );
        // 2
        ReadableAssets byClass = new ReadableAssets() {
            @Override
            @SuppressWarnings("unchecked")
            public <T> ReadableAsset<T> resolve(String resource, Class<T> clazz) throws ResourceException {
                if (String.class.isAssignableFrom(clazz)) {
                    return (stream, assets) -> (T) readText(stream);
                } else {
                    throw new IllegalArgumentException("Unsupported resource class:" + clazz);
                }
            }
        };
        // 3
        ReadableAssets byExtension = new ReadableAssets() {
            @Override
            @SuppressWarnings("unchecked")
            public <T> ReadableAsset<T> resolve(String resource, Class<T> clazz) throws ResourceException {
                if (resource.endsWith("text")) {
                    return (stream, assets) -> (T) readText(stream);
                } else {
                    throw new IllegalArgumentException("Unsupported extension:" + resource);
                }
            }
        };
        // Create instance of ManagedAssets which will delegate real work to SimpleAssets
        ManagedAssets managedAssets = new ManagedAssets(
                new SimpleAssets(
                        resources,
                        new CompositeReadableAssets(
                                byClass,
                                byExtension
                        )
                ),
                new HashMap<>()
        );
        // Now we can load assets
        String AssetByClass = managedAssets.load("/sample.txt", String.class);
        String AssetByExtension = managedAssets.load("/sample.txt", null);
        assertEquals("Hello, World!", AssetByClass);
        assertSame(
                AssetByClass,
                AssetByExtension
        );
    }

    /**
     * Please note: this method replaces line endings with '\n' which is Ok here but please don't use it in production.
     */
    private static String readText(ReadableByteChannel channel) {
        try (BufferedReader br = new BufferedReader(Channels.newReader(channel, "UTF-8"))) {
            return br.lines().collect(Collectors.joining("\n"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
