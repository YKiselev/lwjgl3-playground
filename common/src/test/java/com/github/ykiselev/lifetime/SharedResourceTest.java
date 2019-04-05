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

package com.github.ykiselev.lifetime;

import com.github.ykiselev.wrap.Wrap;
import org.junit.jupiter.api.Test;

import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * @author Yuriy Kiselev (uze@yandex.ru)
 * @since 07.04.2019
 */
public class SharedResourceTest {

    @Test
    public void shouldIncrementReference() {
        final AutoCloseable target = mock(AutoCloseable.class);
        final Consumer<AutoCloseable> disposer = mock(Consumer.class);
        final SharedResource<AutoCloseable> res = new SharedResource<>(target, (sr, v) -> disposer.accept(v));
        final Wrap<AutoCloseable> share1 = res.share();
        final Wrap<AutoCloseable> share2 = res.share();
        assertSame(target, share1.value());
        assertSame(target, share1.value());

        share1.close();
        share2.close();
        res.close();

        verify(disposer, times(1)).accept(target);
    }
}