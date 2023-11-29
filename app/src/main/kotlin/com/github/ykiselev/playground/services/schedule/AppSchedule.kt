package com.github.ykiselev.playground.services.schedule

import com.github.ykiselev.spi.services.schedule.Repeatable
import com.github.ykiselev.spi.services.schedule.Schedule
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.function.LongSupplier

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
class AppSchedule(
    private val clock: LongSupplier = LongSupplier { System.currentTimeMillis() }
) : Schedule, AutoCloseable {

    private val tasks = PriorityQueue<Task>()

    override fun close() {
        // no-op for now
    }

    private fun add(t: Task) {
        synchronized(tasks) { tasks.add(t) }
    }

    override fun schedule(interval: Long, unit: TimeUnit, task: Runnable) {
        add(task(unit.toMillis(interval), task))
    }

    override fun schedule(interval: Long, unit: TimeUnit, task: Repeatable) {
        add(task(unit.toMillis(interval), task))
    }

    private fun time(): Long =
        clock.asLong

    private fun task(interval: Long, r: Runnable): Task =
        Task(time() + interval, interval, r)

    private fun task(interval: Long, r: Repeatable): Task =
        Task(time() + interval, interval, r)

    override fun processPendingTasks(quota: Long) {
        val t0 = time()
        while (true) {
            val time = time()
            val timeLeft = t0 + quota - time
            if (timeLeft <= 0) {
                break
            }
            val task: Task?
            synchronized(tasks) {
                task = tasks.peek()
                if (task == null || task.after(time + timeLeft)) {
                    return
                }
                tasks.poll()
            }
            if (task?.run() == true) {
                add(task.reset(time()))
            }
        }
    }

    private class Task(
        private val targetTime: Long,
        private val interval: Long,
        private val repeatable: Repeatable
    ) : Comparable<Task>, Repeatable {

        constructor(targetTime: Long, interval: Long, runnable: Runnable) : this(
            targetTime,
            interval,
            Repeatable {
                runnable.run()
                false
            })

        fun reset(time: Long): Task =
            Task(time + interval, interval, repeatable)

        fun after(time: Long): Boolean =
            targetTime > time

        override fun compareTo(o: Task): Int =
            targetTime.compareTo(o.targetTime)

        override fun run(): Boolean =
            repeatable.run()
    }
}