package com.github.ykiselev.lazy;

import com.github.ykiselev.test.ParallelRunner;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.BooleanSupplier;
import java.util.function.DoubleSupplier;
import java.util.function.IntSupplier;
import java.util.function.LongSupplier;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
class LazyTest {

    @Test
    void shouldInitOnce() {
        Supplier<String> delegate = Mockito.mock(Supplier.class);
        when(delegate.get()).thenReturn("x");
        Supplier<String> lazy = Lazy.of(delegate);
        assertEquals("x", lazy.get());
        assertEquals("x", lazy.get());
        assertEquals("x", lazy.get());
        verify(delegate, times(1)).get();
    }

    @Test
    void shouldInitIntOnce() {
        IntSupplier delegate = Mockito.mock(IntSupplier.class);
        when(delegate.getAsInt()).thenReturn(5);
        IntSupplier lazy = Lazy.of(delegate);
        assertEquals(5, lazy.getAsInt());
        assertEquals(5, lazy.getAsInt());
        assertEquals(5, lazy.getAsInt());
        verify(delegate, times(1)).getAsInt();
    }

    @Test
    void shouldInitLongOnce() {
        LongSupplier delegate = Mockito.mock(LongSupplier.class);
        when(delegate.getAsLong()).thenReturn(15L);
        LongSupplier lazy = Lazy.of(delegate);
        assertEquals(15, lazy.getAsLong());
        assertEquals(15, lazy.getAsLong());
        assertEquals(15, lazy.getAsLong());
        verify(delegate, times(1)).getAsLong();
    }

    @Test
    void shouldInitDoubleOnce() {
        DoubleSupplier delegate = Mockito.mock(DoubleSupplier.class);
        when(delegate.getAsDouble()).thenReturn(3.14);
        DoubleSupplier lazy = Lazy.of(delegate);
        assertEquals(3.14, lazy.getAsDouble());
        assertEquals(3.14, lazy.getAsDouble());
        assertEquals(3.14, lazy.getAsDouble());
        verify(delegate, times(1)).getAsDouble();
    }

    @Test
    void shouldInitBooleanOnce() {
        BooleanSupplier delegate = Mockito.mock(BooleanSupplier.class);
        when(delegate.getAsBoolean()).thenReturn(true);
        BooleanSupplier lazy = Lazy.of(delegate);
        assertTrue(lazy.getAsBoolean());
        assertTrue(lazy.getAsBoolean());
        assertTrue(lazy.getAsBoolean());
        verify(delegate, times(1)).getAsBoolean();
    }

    private <T> void runParallel(Supplier<T> lazy, Predicate<T> validator) {
        final AtomicLong errors = new AtomicLong();
        Supplier<Runnable> f = () -> () -> {
            if (!validator.test(lazy.get())) {
                errors.incrementAndGet();
            }
        };
        new ParallelRunner(500, f, f, f, f).run();
        assertEquals(0, errors.get());
    }

    @Test
    void shouldBeThreadSafe() {
        AtomicLong counter = new AtomicLong();
        Supplier<Long> lazy = Lazy.sync((Supplier<Long>) counter::incrementAndGet);
        runParallel(lazy, v -> v == 1L);
        assertEquals(1, counter.get());
    }

    @Test
    void intShouldBeThreadSafe() {
        AtomicInteger counter = new AtomicInteger();
        IntSupplier lazy = Lazy.sync(counter::incrementAndGet);
        runParallel(lazy::getAsInt, v -> v == 1);
        assertEquals(1, counter.get());
    }

    @Test
    void longShouldBeThreadSafe() {
        AtomicLong counter = new AtomicLong();
        LongSupplier lazy = Lazy.sync(counter::incrementAndGet);
        runParallel(lazy::getAsLong, v -> v == 1L);
        assertEquals(1, counter.get());
    }

    @Test
    void doubleShouldBeThreadSafe() {
        AtomicInteger counter = new AtomicInteger();
        DoubleSupplier lazy = Lazy.sync((DoubleSupplier) counter::incrementAndGet);
        runParallel(lazy::getAsDouble, v -> v == 1.0);
        assertEquals(1, counter.get());
    }

    @Test
    void booleanShouldBeThreadSafe() {
        AtomicLong counter = new AtomicLong();
        BooleanSupplier lazy = Lazy.sync(() -> (counter.incrementAndGet() & 1) != 0);
        runParallel(lazy::getAsBoolean, v -> v);
        assertEquals(1, counter.get());
    }

}