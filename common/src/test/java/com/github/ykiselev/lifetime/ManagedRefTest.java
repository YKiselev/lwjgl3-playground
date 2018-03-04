package com.github.ykiselev.lifetime;

import org.junit.jupiter.api.Test;

import java.util.function.Consumer;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
class ManagedRefTest {

    @Test
    void shouldManage() {
        final Consumer<MyManageable> disposer = mock(Consumer.class);
        final ManagedRef<MyManageable> ref = new ManagedRef<>(
                new MyManageable(disposer)
        );
        final MyManageable copy1 = ref.newRef();
        final MyManageable copy2 = ref.newRef();
        copy1.close();
        copy2.close();
        verify(disposer, never()).accept(any(MyManageable.class));
        ref.close();
        verify(disposer, only()).accept(any(MyManageable.class));
    }

    private static class MyManageable implements Manageable<MyManageable>, AutoCloseable {

        private final Consumer<MyManageable> onClose;

        MyManageable(Consumer<MyManageable> onClose) {
            this.onClose = onClose;
        }

        @Override
        public MyManageable manage(Consumer<MyManageable> onClose) {
            return new MyManageable(onClose);
        }

        @Override
        public void close() {
            onClose.accept(MyManageable.this);
        }
    }
}

