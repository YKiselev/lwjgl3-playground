package com.github.ykiselev.playground.init;

import com.github.ykiselev.spi.services.schedule.Schedule;

public final class ScheduleTask extends FrameTask {

    private final Schedule schedule;

    public ScheduleTask(Runnable next, Schedule schedule) {
        super(next);
        this.schedule = schedule;
    }

    @Override
    protected void onFrameStart() {
        // no-op
    }

    @Override
    protected void onFrameEnd() {
        schedule.processPendingTasks(2);
    }
}
