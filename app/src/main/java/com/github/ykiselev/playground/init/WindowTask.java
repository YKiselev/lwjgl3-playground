package com.github.ykiselev.playground.init;

import com.github.ykiselev.playground.app.window.AppWindow;

public final class WindowTask extends FrameTask {

    private final AppWindow window;

    public WindowTask(Runnable next, AppWindow window) {
        super(next);
        this.window = window;
    }

    @Override
    protected void onFrameStart() {
        window.makeCurrent();
        window.checkEvents();
    }

    @Override
    protected void onFrameEnd() {
        window.swapBuffers();
    }
}
