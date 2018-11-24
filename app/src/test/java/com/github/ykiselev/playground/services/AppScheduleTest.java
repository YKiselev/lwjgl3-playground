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

package com.github.ykiselev.playground.services;

import com.github.ykiselev.playground.services.schedule.AppSchedule;
import com.github.ykiselev.services.schedule.Repeatable;
import org.junit.jupiter.api.Test;
import org.mockito.stubbing.Answer;

import java.util.concurrent.TimeUnit;

import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public class AppScheduleTest {

    private long clock;

    private final AppSchedule schedule = new AppSchedule(() -> clock);

    private Answer<Void> advanceTime(long millis) {
        return inv -> {
            clock += millis;
            return null;
        };
    }

    @Test
    public void shouldSchedule() {
        clock = 0;
        final Runnable t1 = mock(Runnable.class);
        final Runnable t2 = mock(Runnable.class);
        final Runnable t3 = mock(Runnable.class);
        final Runnable t4 = mock(Runnable.class);
        schedule.schedule(2, TimeUnit.MILLISECONDS, t1);
        schedule.schedule(3, TimeUnit.MILLISECONDS, t2);
        schedule.schedule(4, TimeUnit.MILLISECONDS, t3);
        schedule.schedule(5, TimeUnit.MILLISECONDS, t4);
        doAnswer(advanceTime(1)).when(t1).run();
        doAnswer(advanceTime(5)).when(t2).run();
        doAnswer(advanceTime(5)).when(t3).run();
        doAnswer(advanceTime(2)).when(t4).run();
        schedule.processPendingTasks(7);
        verify(t1, times(1)).run();
        verify(t2, times(1)).run();
        verify(t3, times(1)).run();
        verify(t4, never()).run();
        schedule.processPendingTasks(2);
        verify(t4, times(1)).run();
    }

    @Test
    public void shouldScheduleRepeatable() {
        clock = 0;
        final Repeatable r1 = mock(Repeatable.class);
        final Repeatable r2 = mock(Repeatable.class);
        when(r1.run()).thenReturn(true);
        when(r2.run()).thenReturn(true);
        schedule.schedule(5, TimeUnit.MILLISECONDS, r1);
        schedule.schedule(25, TimeUnit.MILLISECONDS, r2);
        schedule.processPendingTasks(2);
        verify(r1, never()).run();
        verify(r2, never()).run();
        clock = 5;
        schedule.processPendingTasks(2);
        verify(r1, times(1)).run();
        verify(r2, never()).run();
        clock = 10;
        schedule.processPendingTasks(2);
        verify(r1, times(2)).run();
        verify(r2, never()).run();
        clock = 17;
        schedule.processPendingTasks(2);
        verify(r1, times(3)).run();
        verify(r2, never()).run();
        clock = 25;
        schedule.processPendingTasks(2);
        verify(r1, times(4)).run();
        verify(r2, times(1)).run();
    }

}