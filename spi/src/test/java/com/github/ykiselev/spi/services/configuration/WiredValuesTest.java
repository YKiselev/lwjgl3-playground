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

package com.github.ykiselev.spi.services.configuration;

import com.github.ykiselev.spi.services.configuration.values.ConfigValue;
import com.github.ykiselev.spi.services.configuration.values.LongFormat;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Collection;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public class WiredValuesTest {

    private final PersistedConfiguration cfg = mock(PersistedConfiguration.class);

    @Test
    public void shouldWireIntAndCheckForOverflow() {
        final AtomicInteger value = new AtomicInteger();
        new WiredValues(cfg)
                .withInt("a", () -> 1, value::set, false, LongFormat.DECIMAL)
                .build();
        ArgumentCaptor<Collection<ConfigValue>> captor = ArgumentCaptor.forClass(Collection.class);
        verify(cfg, times(1)).wire(captor.capture());
        Collection<ConfigValue> r = captor.getValue();
        ConfigValue v = r.iterator().next();
        assertEquals("a", v.name());
        assertThrows(ArithmeticException.class, () ->
                v.setString("999999999999999999"));
        v.setString("2694881535");
    }
}