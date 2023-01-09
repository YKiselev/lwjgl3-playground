package com.github.ykiselev.playground.init;

import com.github.ykiselev.playground.services.GameBootstrap;

public final class GameTask extends FrameTask {

    private final GameBootstrap game;

    public GameTask(Runnable next, GameBootstrap game) {
        super(next);
        this.game = game;
    }

    @Override
    protected void onFrameStart() {
        game.update();
    }

    @Override
    protected void onFrameEnd() {
        // no-op
    }
}
