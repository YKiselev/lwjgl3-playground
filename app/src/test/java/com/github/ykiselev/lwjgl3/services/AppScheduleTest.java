package com.github.ykiselev.lwjgl3.services;

import org.junit.jupiter.api.Test;
import org.mockito.stubbing.Answer;

import java.util.concurrent.TimeUnit;

import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
class AppScheduleTest {

    private long clock;

    private final Schedule schedule = new AppSchedule(() -> clock);

    private Answer<Void> advanceTime(long millis) {
        return inv -> {
            clock += millis;
            return null;
        };
    }

    @Test
    void shouldSchedule() {
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
}