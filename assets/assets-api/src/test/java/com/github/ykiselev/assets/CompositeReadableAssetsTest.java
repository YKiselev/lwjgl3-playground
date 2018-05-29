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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
class CompositeReadableAssetsTest {

    @Test
    void shouldResolve() {
        final ReadableAsset<String> rr = (stream, assets) -> null;
        final ReadableAssets delegate1 = mock(ReadableAssets.class);
        final ReadableAssets delegate2 = mock(ReadableAssets.class);
        when(delegate1.resolve(eq("a"), eq(String.class)))
                .thenReturn(null);
        when(delegate2.resolve(eq("a"), eq(String.class)))
                .thenReturn(rr);
        final ReadableAssets readableAssets = new CompositeReadableAssets(
                delegate1,
                delegate2
        );
        Assertions.assertEquals(
                rr,
                readableAssets.resolve("a", String.class)
        );
    }
}