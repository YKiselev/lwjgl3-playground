package cob.github.ykiselev.lwjgl3.events;

import org.junit.jupiter.api.Test;

import java.util.function.Consumer;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
class SubscriptionsBuilderTest {

    private final Events events = mock(Events.class);

    private final Consumer<String> handler = mock(Consumer.class);

    @Test
    void shouldSubscribe() {
        new SubscriptionsBuilder()
                .add(String.class, handler)
                .build(events);
        verify(events).subscribe(String.class, handler);
    }

    @Test
    void shouldSubscribeWithoutPredicate() {
        new SubscriptionsBuilder()
                .add(String.class, handler)
                .build(events);
        verify(events).subscribe(String.class, handler);
    }

    @Test
    void shouldUnsubscribe() throws Exception {
        final AutoCloseable ac = mock(AutoCloseable.class);
        when(events.subscribe(String.class, handler))
                .thenReturn(ac);
        new SubscriptionsBuilder()
                .add(String.class, handler)
                .build(events)
                .close();
        verify(ac).close();
    }

}