package com.github.ykiselev.lwjgl3.services.schedule;

import java.util.concurrent.TimeUnit;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public interface Schedule {

    /**
     * Schedule supplied {@link Runnable} to be run once after specified interval.
     *
     * @param interval the time interval
     * @param unit     the time unit of {@code interval}
     * @param task     the task to run once
     */
    void schedule(long interval, TimeUnit unit, Runnable task);

    /**
     * Schedules {@link Repeatable} task to be run after specified interval until {@link Repeatable#run()} returns {@code true}.
     *
     * @param interval the time interval
     * @param unit     the time unit of {@code interval}
     * @param task     the task to run
     */
    void schedule(long interval, TimeUnit unit, Repeatable task);

    /**
     * Runs scheduled tasks if current time >= task run time
     *
     * @param quota the maximum number of tasks to run
     */
    void processPendingTasks(long quota);
}
