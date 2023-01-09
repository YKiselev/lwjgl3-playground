package com.github.ykiselev.playground.init;

public abstract class FrameTask implements Runnable {

    private final Runnable next;

    public FrameTask(Runnable next) {
        this.next = next;
    }

    protected abstract void onFrameStart();

    protected abstract void onFrameEnd();

    @Override
    public void run() {
        onFrameStart();
        if (next != null) {
            next.run();
        }
        onFrameEnd();
    }
}
