package com.github.ykiselev.playground.services

import com.github.ykiselev.playground.services.schedule.AppSchedule
import com.github.ykiselev.spi.services.schedule.Repeatable
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.mockito.kotlin.*
import java.util.concurrent.TimeUnit

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
class AppScheduleTest {

    private var clock: Long = 0
    private val schedule = AppSchedule { clock }

    @Test
    fun shouldSchedule() {
        clock = 0
        val t1 = mock<Runnable>()
        val t2 = mock<Runnable>()
        val t3 = mock<Runnable>()
        val t4 = mock<Runnable>()
        schedule.schedule(2, TimeUnit.MILLISECONDS, t1)
        schedule.schedule(3, TimeUnit.MILLISECONDS, t2)
        schedule.schedule(4, TimeUnit.MILLISECONDS, t3)
        schedule.schedule(5, TimeUnit.MILLISECONDS, t4)
        doAnswer { clock += 1 }.`when`(t1).run()
        doAnswer { clock += 5 }.`when`(t2).run()
        doAnswer { clock += 5 }.`when`(t3).run()
        doAnswer { clock += 2 }.`when`(t4).run()
        schedule.processPendingTasks(7)
        verify(t1, times(1)).run()
        verify(t2, times(1)).run()
        verify(t3, times(1)).run()
        verify(t4, never()).run()
        schedule.processPendingTasks(2)
        verify(t4, times(1)).run()
    }

    @Test
    fun shouldScheduleRepeatable() {
        clock = 0
        val r1 = mock<Repeatable>()
        val r2 = mock<Repeatable>()
        Mockito.`when`(r1.run()).thenReturn(true)
        Mockito.`when`(r2.run()).thenReturn(true)
        schedule.schedule(5, TimeUnit.MILLISECONDS, r1)
        schedule.schedule(25, TimeUnit.MILLISECONDS, r2)
        schedule.processPendingTasks(2)
        verify(r1, never()).run()
        verify(r2, never()).run()
        clock = 5
        schedule.processPendingTasks(2)
        verify(r1, times(1)).run()
        verify(r2, never()).run()
        clock = 10
        schedule.processPendingTasks(2)
        verify(r1, times(2)).run()
        verify(r2, never()).run()
        clock = 17
        schedule.processPendingTasks(2)
        verify(r1, times(3)).run()
        verify(r2, never()).run()
        clock = 25
        schedule.processPendingTasks(2)
        verify(r1, times(4)).run()
        verify(r2, times(1)).run()
    }
}