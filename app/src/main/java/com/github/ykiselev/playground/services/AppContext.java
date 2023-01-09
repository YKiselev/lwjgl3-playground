package com.github.ykiselev.playground.services;

import com.github.ykiselev.assets.Assets;
import com.github.ykiselev.common.fps.FrameInfo;
import com.github.ykiselev.opengl.sprites.SpriteBatch;
import com.github.ykiselev.spi.GameFactoryArgs;
import com.github.ykiselev.spi.ProgramArguments;
import com.github.ykiselev.spi.services.FileSystem;
import com.github.ykiselev.spi.services.SoundEffects;
import com.github.ykiselev.spi.services.commands.Commands;
import com.github.ykiselev.spi.services.configuration.PersistedConfiguration;
import com.github.ykiselev.spi.services.layers.UiLayers;
import com.github.ykiselev.spi.services.schedule.Schedule;
import com.github.ykiselev.spi.window.Window;

public record AppContext(ProgramArguments arguments,
                         FileSystem fileSystem,
                         Commands commands,
                         PersistedConfiguration configuration,
                         Schedule schedule,
                         UiLayers uiLayers,
                         Assets assets,
                         SpriteBatch spriteBatch,
                         SoundEffects soundEffects,
                         Window window,
                         FrameInfo frameInfo) {

    public GameFactoryArgs toGameFactoryArgs() {
        return new GameFactoryArgs(arguments, fileSystem, commands, configuration,
                schedule, uiLayers, assets, spriteBatch, soundEffects, window, frameInfo);
    }
}
