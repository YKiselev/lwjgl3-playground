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

import org.junit.jupiter.api.Test;

import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
class CountedRefTest {

    public interface A {

        String name();
    }

    @Test
    void shouldCount() {
        final Consumer<A> disposer = mock(Consumer.class);
        final A target = mock(A.class);
        when(target.name()).thenReturn("xyz");

        final CountedRef<A> ref = new CountedRef<>(target, disposer);
        final A copy1 = ref.newRef();
        final A copy2 = ref.newRef();
        assertEquals("xyz", copy1.name());
        assertEquals("xyz", copy2.name());
        ref.release();
        ref.release();
        ref.close();
        verify(disposer, times(1)).accept(target);
    }

}