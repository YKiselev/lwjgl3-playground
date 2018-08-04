package com.github.ykiselev.closeables;

import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;

import static org.mockito.Mockito.inOrder;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
class CompositeAutoCloseableTest {

    private final AutoCloseable ac1 = Mockito.mock(AutoCloseable.class);

    private final AutoCloseable ac2 = Mockito.mock(AutoCloseable.class);

    private final AutoCloseable ac3 = Mockito.mock(AutoCloseable.class);

    private final CompositeAutoCloseable ac = new CompositeAutoCloseable(ac1, ac2, ac3);

    @Test
    void shouldCallInDirectOrder() throws Exception {
        ac.close();
        InOrder inOrder = inOrder(ac1, ac2, ac3);
        inOrder.verify(ac1).close();
        inOrder.verify(ac2).close();
        inOrder.verify(ac3).close();
    }

    @Test
    void shouldCallInReversedOrder() throws Exception {
        ac.reverse().close();
        InOrder inOrder = inOrder(ac1, ac2, ac3);
        inOrder.verify(ac3).close();
        inOrder.verify(ac2).close();
        inOrder.verify(ac1).close();
    }
}