package com.github.ykiselev.playground.init

import com.github.ykiselev.spi.services.schedule.Schedule

class ScheduleTask(next: Runnable?, private val schedule: Schedule) : FrameTask(next) {

    override fun onFrameEnd() {
        schedule.processPendingTasks(2)
    }
}
