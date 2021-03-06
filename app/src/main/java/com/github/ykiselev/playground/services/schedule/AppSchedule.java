/*
 * Copyright 2017 Yuriy Kiselev (uze@yandex.ru)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.ykiselev.playground.services.schedule;

import com.github.ykiselev.spi.services.schedule.Repeatable;
import com.github.ykiselev.spi.services.schedule.Schedule;

import java.util.PriorityQueue;
import java.util.concurrent.TimeUnit;
import java.util.function.LongSupplier;

import static java.util.Objects.requireNonNull;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class AppSchedule implements Schedule, AutoCloseable {

    private final PriorityQueue<Task> tasks = new PriorityQueue<>();

    private final LongSupplier clock;

    public AppSchedule(LongSupplier clock) {
        this.clock = requireNonNull(clock);
    }

    public AppSchedule() {
        this(System::currentTimeMillis);
    }

    @Override
    public void close() {
        // no-op for now
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
