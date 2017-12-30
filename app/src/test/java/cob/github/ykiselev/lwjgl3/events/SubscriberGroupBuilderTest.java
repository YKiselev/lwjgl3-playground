package cob.github.ykiselev.lwjgl3.events;

import org.junit.Test;

import java.util.function.Consumer;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public class SubscriberGroupBuilderTest {

    private final Events events = mock(Events.class);

    private final Consumer<String> handler = mock(Consumer.class);

    @Test
    public void shouldSubscribe() {
        new SubscriberGroupBuilder()
                .add(String.class, handler)
                .build(events);
        verify(events).subscribe(String.class, handler);
    }

    @Test
    public void shouldUnsubscribe() {
        new SubscriberGroupBuilder()
                .add(String.class, handler)
                .build(events)
                .close();
        verify(events).unsubscribe(String.class, handler);
    }

}