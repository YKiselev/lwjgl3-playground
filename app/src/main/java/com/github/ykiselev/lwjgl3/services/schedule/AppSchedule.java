package com.github.ykiselev.lwjgl3.services.schedule;

import java.util.PriorityQueue;
import java.util.concurrent.TimeUnit;
import java.util.function.LongSupplier;

import static java.util.Objects.requireNonNull;

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

    private void add(Task t) {
        synchronized (tasks) {
            tasks.add(t);
        }
    }

    @Override
    public void schedule(long interval, TimeUnit unit, Runnable task) {
        add(
                new Task(
                        time() + unit.toMillis(interval),
                        task
                )
        );
    }

    @Override
    public void schedule(long interval, TimeUnit unit, Repeatable task) {
        add(
                new Task(
                        time() + unit.toMillis(interval),
                        task
                )
        );
    }

    private long time() {
        return clock.getAsLong();
    }

    /**
     * Runs scheduled tasks if current time >= task run time
     *
     * @param quota the maximum number of tasks to run
     */
    public void processPendingTasks(long quota) {
        final long t0 = time();
        for (; ; ) {
            final long time = time();
            final long timeLeft = t0 + quota - time;
            if (timeLeft <= 0) {
                break;
            }
            final Task task;
            synchronized (tasks) {
                task = tasks.peek();
                if (task == null || task.targetTime > time + timeLeft) {
                    break;
                }
                tasks.poll();
            }
            task.run();
        }
    }

    private static final class Task implements Comparable<Task>, Repeatable {

        private final long targetTime;

        private final Repeatable repeatable;

        Task(long targetTime, Repeatable repeatable) {
            this.targetTime = targetTime;
            this.repeatable = requireNonNull(repeatable);
        }

        Task(long targetTime, Runnable runnable) {
            this(targetTime, () -> {
                runnable.run();
                return false;
            });
        }

        @Override
        public int compareTo(Task o) {
            return Long.compare(targetTime, o.targetTime);
        }

        @Override
        public boolean run() {
            return repeatable.run();
        }
    }
}
