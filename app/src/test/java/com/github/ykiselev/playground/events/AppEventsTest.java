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

package com.github.ykiselev.playground.events;

import com.github.ykiselev.services.events.EventFilter;
import com.github.ykiselev.services.events.Events;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
class AppEventsTest {

    interface A {

    }

    class B implements A {

    }

    interface C {

    }

    private class D extends B implements C {

    }

    class E<V> {

    }

    private class F extends E<String> {

    }

    private class G extends E<Double> {

    }

    private final Events bus = new AppEvents();

    private <V> Consumer<V> fail() {
        return c -> Assertions.fail("Should not be called!");
    }

    @Test
    void shouldSubscribe() throws Exception {
        final List<String> journal = new ArrayList<>();
        final Consumer<String> handler = journal::add;
        AutoCloseable s1 = bus.subscribe(String.class, handler);
        bus.fire("1");
        bus.fire("2");
        bus.fire("3");
        s1.close();
        assertEquals(Arrays.asList("1", "2", "3"), journal);
    }

    @Test
    void shouldFindExactEventType() {
        final AtomicBoolean flag = new AtomicBoolean(false);
        bus.subscribe(A.class, fail());
        bus.subscribe(B.class, fail());
        bus.subscribe(C.class, fail());
        bus.subscribe(D.class, c -> flag.set(true));
        bus.fire(new D());
        assertTrue(flag.get());
    }

    @Test
    void shouldSupportParameterizedEventTypes() {
        final AtomicBoolean f = new AtomicBoolean(false);
        final AtomicBoolean e = new AtomicBoolean(false);
        bus.subscribe(E.class, fail());
        bus.subscribe(F.class, c -> f.set(true));
        bus.subscribe(G.class, c -> e.set(true));
        assertNotNull(bus.fire(new F()));
        assertNotNull(bus.fire(new G()));
        assertTrue(f.get());
        assertTrue(e.get());
    }

    @Test
    void shouldUnsubscribe() throws Exception {
        final AtomicBoolean f = new AtomicBoolean(false);
        AutoCloseable s2 = bus.subscribe(F.class, c -> f.set(true));
        bus.fire(new F());
        assertTrue(f.get());
        f.set(false);
        s2.close();
        bus.fire(new F());
        assertFalse(f.get());
    }

    @Test
    void shouldFilter() throws Exception {
        final List<String> journal = new ArrayList<>();
        final Consumer<String> handler = journal::add;
        final EventFilter<String> filter = s -> "x".equals(s) ? s : null;
        AutoCloseable s1 = bus.subscribe(String.class, handler);
        AutoCloseable s2 = bus.add(String.class, filter);
        bus.fire("a");
        bus.fire("x");
        bus.fire("c");
        s1.close();
        s2.close();
        assertEquals(Collections.singletonList("x"), journal);
    }

}