package com.github.ykiselev.playground.init;

import com.github.ykiselev.common.fps.FrameInfo;

import static org.lwjgl.glfw.GLFW.glfwGetTime;

public final class FrameInfoTracker extends FrameTask {

    private final FrameInfo frameInfo;

    private double t0;

    public FrameInfoTracker(Runnable next, FrameInfo frameInfo) {
        super(next);
        this.frameInfo = frameInfo;
    }

    @Override
    protected void onFrameStart() {
        t0 = glfwGetTime();
    }

    @Override
    protected void onFrameEnd() {
        final double t1 = glfwGetTime();
        frameInfo.add((t1 - t0) * 1000.0);
    }
}
