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

package com.github.ykiselev.spi;

import com.github.ykiselev.common.fps.FrameInfo;
import com.github.ykiselev.spi.services.Services;

import static java.util.Objects.requireNonNull;

/**
 * @author Yuriy Kiselev (uze@yandex.ru)
 * @since 03.05.2019
 */
public final class GameHost {

    public final ProgramArguments arguments;

    public final Services services;

    public final FrameInfo frameInfo;

    public GameHost(ProgramArguments arguments, Services services, FrameInfo frameInfo) {
        this.arguments = requireNonNull(arguments);
        this.services = requireNonNull(services);
        this.frameInfo = requireNonNull(frameInfo);
    }
}
