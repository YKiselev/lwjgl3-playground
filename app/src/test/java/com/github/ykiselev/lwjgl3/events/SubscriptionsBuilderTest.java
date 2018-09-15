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

package com.github.ykiselev.lwjgl3.events;

import com.github.ykiselev.services.events.Events;
import com.github.ykiselev.services.events.SubscriptionsBuilder;
import org.junit.jupiter.api.Test;

import java.util.function.Consumer;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
class SubscriptionsBuilderTest {

    private final Events events = mock(Events.class);

    private final Consumer<String> handler = mock(Consumer.class);

    @Test
    void shouldSubscribe() {
        new SubscriptionsBuilder(events)
                .with(String.class, handler)
                .build();
        verify(events).subscribe(String.class, handler);
    }

    @Test
    void shouldSubscribeWithoutPredicate() {
        new SubscriptionsBuilder(events)
                .with(String.class, handler)
                .build();
        verify(events).subscribe(String.class, handler);
    }

    @Test
    void shouldUnsubscribe() throws Exception {
        final AutoCloseable ac = mock(AutoCloseable.class);
        when(events.subscribe(String.class, handler))
                .thenReturn(ac);
        new SubscriptionsBuilder(events)
                .with(String.class, handler)
                .build()
                .close();
        verify(ac).close();
    }

}