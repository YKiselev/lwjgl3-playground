package com.github.ykiselev.lwjgl3.services;

import java.util.PriorityQueue;
import java.util.concurrent.TimeUnit;
import java.util.function.LongSupplier;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class AppSchedule implements Schedule {

    private final PriorityQueue<Task> tasks = new PriorityQueue<>();

    private final LongSupplier clock;

    public AppSchedule(LongSupplier clock) {
        this.clock = clock;
    }

    public AppSchedule() {
        this(System::currentTimeMillis);
    }

    @Override
    public void schedule(long interval, TimeUnit unit, Runnable task) {
        final Task t = new Task(
                time() + unit.toMillis(interval),
                task
        );
        synchronized (tasks) {
            tasks.add(t);
        }
    }

    private long time() {
        return clock.getAsLong();
    }

    @Override
    public void processPendingTasks(long quota) {
        final long t0 = time();
        for (; ; ) {
            final long time = time();
            final long timeLeft = t0 + quota - time;
            if (timeLeft <= 0) {
                break;
            }
            synchronized (tasks) {
                final Task task = tasks.peek();
                if (task == null || task.targetTime > time + timeLeft) {
                    break;
                }
                tasks.poll();
                task.run();
            }
        }
    }

    private static final class Task implements Comparable<Task>, Runnable {

        private final long targetTime;

        private final Runnable task;

        Task(long targetTime, Runnable task) {
            this.targetTime = targetTime;
            this.task = task;
        }

        @Override
        public int compareTo(Task o) {
            return Long.compare(targetTime, o.targetTime);
        }

        @Override
        public void run() {
            task.run();
        }
    }
}
