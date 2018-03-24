package cob.github.ykiselev.lwjgl3.services;

import java.util.PriorityQueue;
import java.util.concurrent.TimeUnit;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class AppSchedule implements Schedule {

    private final PriorityQueue<Task> tasks = new PriorityQueue<>();

    @Override
    public void schedule(long interval, TimeUnit unit, Runnable task) {
        final Task t = new Task(
                System.currentTimeMillis() + unit.toMillis(interval),
                task
        );
        synchronized (tasks) {
            tasks.add(t);
        }
    }

    @Override
    public void processPendingTasks(long quota) {
        throw new UnsupportedOperationException("not implemented");
    }

    private static final class Task implements Comparable<Task> {

        private final long targetTime;

        private final Runnable task;

        Task(long targetTime, Runnable task) {
            this.targetTime = targetTime;
            this.task = task;
        }

        @Override
        public int compareTo(Task o) {
            throw new UnsupportedOperationException("not implemented");
        }
    }
}
