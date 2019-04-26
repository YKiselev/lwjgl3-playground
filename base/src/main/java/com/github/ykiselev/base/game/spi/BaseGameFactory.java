/*
 * Copyright 2017 Yuriy Kiselev (uze@yandex.ru)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.ykiselev.base.game.spi;

import com.github.ykiselev.base.game.BaseGame;
import com.github.ykiselev.spi.components.Game;
import com.github.ykiselev.spi.GameFactory;
import com.github.ykiselev.spi.services.Services;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class BaseGameFactory implements GameFactory {

    @Override
    public String name() {
        return "base";
    }

    @Override
    public Game create(Services services) {
        return new BaseGame(services);
    }
}
