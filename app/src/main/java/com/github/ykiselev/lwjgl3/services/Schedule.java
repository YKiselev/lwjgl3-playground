package com.github.ykiselev.lwjgl3.services;

import java.util.concurrent.TimeUnit;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public interface Schedule {

    void schedule(long interval, TimeUnit unit, Runnable task);

    void processPendingTasks(long quota);
}
