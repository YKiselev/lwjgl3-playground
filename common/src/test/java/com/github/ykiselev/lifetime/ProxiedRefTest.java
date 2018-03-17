package com.github.ykiselev.lifetime;

import org.junit.jupiter.api.Test;

import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
class ProxiedRefTest {

    @Test
    void shouldProxy() throws Exception {
        final Consumer<B> disposer = mock(Consumer.class);
        final B target = mock(B.class);
        when(target.name()).thenReturn("xyz");

        final ProxiedRef<B> ref = new ProxiedRef<>(target, B.class, disposer);
        final B copy1 = ref.newRef();
        final B copy2 = ref.newRef();
        assertEquals("xyz", copy1.name());
        assertEquals("xyz", copy2.name());
        copy1.close();
        copy2.close();
        verify(target, never()).close();
        verify(disposer, never()).accept(any(B.class));
        ref.close();
        verify(disposer, only()).accept(target);

    }

    public interface B extends AutoCloseable {

        String name();
    }
}

