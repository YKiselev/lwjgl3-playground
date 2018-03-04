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
class ManageableTest {

    @Test
    void shouldManage() {
        final Consumer<MyManageable> disposer = mock(Consumer.class);
        final MyManageable master = new MyManageable(disposer);
        final Counter counter = new Counter(master);
        final MyManageable copy1 = counter.newRef();
        final MyManageable copy2 = counter.newRef();
        copy1.close();
        copy2.close();
        verify(disposer, never()).accept(any(MyManageable.class));
        counter.close();
        verify(disposer, only()).accept(any(MyManageable.class));
    }

}

final class Counter implements Consumer<MyManageable>, AutoCloseable {

    private final CountedRef<MyManageable> countedRef;

    Counter(MyManageable master) {
        countedRef = new CountedRef<>(master, MyManageable::close);
    }

    public MyManageable newRef() {
        countedRef.addRef();
        return countedRef.reference().manage(this);
    }

    @Override
    public void accept(MyManageable manageable) {
        countedRef.release();
    }

    @Override
    public void close() {
        countedRef.release();
    }
}

final class MyManageable implements Manageable<MyManageable>, AutoCloseable {

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
        onClose.accept(this);
    }
}
