package com.github.ykiselev.playground.init;

import com.github.ykiselev.opengl.sprites.SpriteBatch;
import com.github.ykiselev.opengl.sprites.TextAttributes;
import com.github.ykiselev.spi.services.layers.DrawingContext;
import com.github.ykiselev.spi.services.layers.UiLayers;

public final class UiLayersTask extends FrameTask {

    private final UiLayers uiLayers;

    private final DrawingContext context;

    public UiLayersTask(Runnable next, UiLayers uiLayers, SpriteBatch spriteBatch) {
        super(next);
        this.uiLayers = uiLayers;
        this.context = new DrawingContext() {

            private final StringBuilder sb = new StringBuilder();

            private final TextAttributes attributes = new TextAttributes();

            @Override
            public SpriteBatch batch() {
                return spriteBatch;
            }

            @Override
            public StringBuilder stringBuilder() {
                return sb;
            }

            @Override
            public TextAttributes textAttributes() {
                return attributes;
            }
        };
    }

    @Override
    protected void onFrameStart() {
        uiLayers.draw(context);
    }

    @Override
    protected void onFrameEnd() {
        // no-op
    }
}
