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

package com.github.ykiselev.common.lifetime;

import com.github.ykiselev.wrap.Wrap;
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
public class RefTest {

    public interface A {

        String name();
    }

    @Test
    public void shouldCount() {
        final Consumer<A> disposer = mock(Consumer.class);
        final A target = mock(A.class);
        when(target.name()).thenReturn("xyz");

        final Ref<A> ref = new Ref<>(target, disposer);
        final Wrap<A> copy1 = ref.newRef();
        final Wrap<A> copy2 = ref.newRef();
        assertEquals("xyz", copy1.value().name());
        assertEquals("xyz", copy2.value().name());
        ref.release();
        ref.release();
        ref.close();
        verify(disposer, times(1)).accept(target);
    }

}