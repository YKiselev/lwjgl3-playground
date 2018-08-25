package com.github.ykiselev.lwjgl3.services.schedule;

import com.github.ykiselev.services.schedule.Repeatable;
import com.github.ykiselev.services.schedule.Schedule;

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
        this.clock = requireNonNull(clock);
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
        add(task(unit.toMillis(interval), task));
    }

    @Override
    public void schedule(long interval, TimeUnit unit, Repeatable task) {
        add(task(unit.toMillis(interval), task));
    }

    private long time() {
        return clock.getAsLong();
    }

    private Task task(long interval, Runnable r) {
        return new Task(time() + interval, interval, r);
    }

    private Task task(long interval, Repeatable r) {
        return new Task(time() + interval, interval, r);
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
            final Task task;
            synchronized (tasks) {
                task = tasks.peek();
                if (task == null || task.after(time + timeLeft)) {
                    break;
                }
                tasks.poll();
            }
            if (task.run()) {
                add(task.reset(time()));
            }
        }
    }

    private static final class Task implements Comparable<Task>, Repeatable {

        private final long targetTime;

        private final long interval;

        private final Repeatable repeatable;

        Task(long targetTime, long interval, Repeatable repeatable) {
            this.targetTime = targetTime;
            this.interval = interval;
            this.repeatable = requireNonNull(repeatable);
        }

        Task(long targetTime, long interval, Runnable runnable) {
            this(targetTime, interval, () -> {
                runnable.run();
                return false;
            });
        }

        Task reset(long time) {
            return new Task(time + interval, interval, repeatable);
        }

        boolean after(long time) {
            return targetTime > time;
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
