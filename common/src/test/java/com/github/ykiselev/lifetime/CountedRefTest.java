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