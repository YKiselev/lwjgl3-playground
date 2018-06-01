package com.github.ykiselev.lwjgl3.events;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
    @Disabled
    void shouldFindInterfaceEventType() {
        final AtomicBoolean flag = new AtomicBoolean(false);
        bus.subscribe(A.class, fail());
        bus.subscribe(B.class, fail());
        bus.subscribe(C.class, c -> flag.set(true));
        bus.fire(new D());
        assertTrue(flag.get());
    }

    @Test
    @Disabled
    void shouldFindSuperclassEventType() {
        final AtomicBoolean flag = new AtomicBoolean(false);
        bus.subscribe(A.class, fail());
        bus.subscribe(B.class, c -> flag.set(true));
        bus.fire(new D());
        assertTrue(flag.get());
    }

    @Test
    @Disabled
    void shouldFindSuperclassInterfaceEventType() {
        final AtomicBoolean flag = new AtomicBoolean(false);
        bus.subscribe(A.class, c -> flag.set(true));
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
        bus.fire(new F());
        bus.fire(new G());
        assertTrue(f.get());
        assertTrue(e.get());
    }

    @Test
    void shouldUnsubscribe() throws Exception {
        final AtomicBoolean f = new AtomicBoolean(false);
        AutoCloseable s2 = bus.subscribe(F.class, c -> f.set(true));
        bus.fire(new F());
        assertTrue(f.get());
        s2.close();
        assertThrows(
                IllegalStateException.class,
                () -> bus.fire(new F())
        );
    }

}