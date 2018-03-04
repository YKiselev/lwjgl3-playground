package com.github.ykiselev.common;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
class CloseOnceTest {

    @Test
    void shouldCloseOnce() throws Exception {
        final AutoCloseable closeable = Mockito.mock(AutoCloseable.class);
        final CloseOnce once = new CloseOnce(closeable);
        assertFalse(once.isClosed());
        once.close();
        assertTrue(once.isClosed());
        once.close();
        assertTrue(once.isClosed());
        Mockito.verify(closeable).close();
    }

}