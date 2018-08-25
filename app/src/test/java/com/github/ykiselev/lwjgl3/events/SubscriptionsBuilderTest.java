package com.github.ykiselev.lwjgl3.events;

import com.github.ykiselev.services.events.EventHandler;
import com.github.ykiselev.services.events.Events;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
class SubscriptionsBuilderTest {

    private final Events events = mock(Events.class);

    private final EventHandler<String> handler = mock(EventHandler.class);

    @Test
    void shouldSubscribe() {
        new SubscriptionsBuilder(events)
                .with(String.class, handler)
                .build();
        verify(events).subscribe(String.class, handler);
    }

    @Test
    void shouldSubscribeWithoutPredicate() {
        new SubscriptionsBuilder(events)
                .with(String.class, handler)
                .build();
        verify(events).subscribe(String.class, handler);
    }

    @Test
    void shouldUnsubscribe() throws Exception {
        final AutoCloseable ac = mock(AutoCloseable.class);
        when(events.subscribe(String.class, handler))
                .thenReturn(ac);
        new SubscriptionsBuilder(events)
                .with(String.class, handler)
                .build()
                .close();
        verify(ac).close();
    }

}