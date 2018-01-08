package cob.github.ykiselev.lwjgl3.events;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class Subscriptions implements AutoCloseable {

    private final AutoCloseable[] subscriptions;

    Subscriptions(AutoCloseable... subscriptions) {
        this.subscriptions = subscriptions.clone();
    }

    @Override
    public void close() throws Exception {
        for (AutoCloseable subscription : subscriptions) {
            subscription.close();
        }
    }
}
