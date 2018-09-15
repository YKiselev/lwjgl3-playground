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

package com.github.ykiselev.services.schedule;

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
     * @param quota the soft time limit for processing in milliseconds. There is no guarantee that this limit will not be breached.
     */
    void processPendingTasks(long quota);
}
