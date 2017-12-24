package cob.github.ykiselev.lwjgl3.events;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public class AppEventsTest {

    interface A {

    }

    class B implements A {

    }

    interface C {

    }

    class D extends B implements C {

    }

    private final Events bus = new AppEvents();

    @Test
    public void shouldSubscribe() {
        final List<String> journal = new ArrayList<>();
        final Consumer<String> handler = journal::add;
        bus.subscribe(String.class, handler);
        bus.send("1");
        bus.send("2");
        bus.send("3");
        bus.unsubscribe(String.class, handler);
        assertEquals(Arrays.asList("1", "2", "3"), journal);
    }

    @Test
    public void shouldFindExactEventType() {
        final AtomicBoolean flag = new AtomicBoolean(false);
        bus.subscribe(A.class, c -> Assert.fail());
        bus.subscribe(B.class, c -> Assert.fail());
        bus.subscribe(C.class, c -> Assert.fail());
        bus.subscribe(D.class, c -> flag.set(true));
        bus.send(new D());
        assertTrue(flag.get());
    }

    @Test
    public void shouldFindInterfaceEventType() {
        final AtomicBoolean flag = new AtomicBoolean(false);
        bus.subscribe(A.class, c -> Assert.fail());
        bus.subscribe(B.class, c -> Assert.fail());
        bus.subscribe(C.class, c -> flag.set(true));
        bus.send(new D());
        assertTrue(flag.get());
    }

    @Test
    public void shouldFindSuperclassEventType() {
        final AtomicBoolean flag = new AtomicBoolean(false);
        bus.subscribe(A.class, c -> Assert.fail());
        bus.subscribe(B.class, c -> flag.set(true));
        bus.send(new D());
        assertTrue(flag.get());
    }

    @Test
    public void shouldFindSuperclassInterfaceEventType() {
        final AtomicBoolean flag = new AtomicBoolean(false);
        bus.subscribe(A.class, c -> flag.set(true));
        bus.send(new D());
        assertTrue(flag.get());
    }

}