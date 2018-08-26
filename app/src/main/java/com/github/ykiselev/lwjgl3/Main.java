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

package com.github.ykiselev.lwjgl3;

import com.github.ykiselev.lwjgl3.app.AppBuilder;
import com.github.ykiselev.lwjgl3.app.Host;
import com.github.ykiselev.lwjgl3.app.MainLoop;
import com.github.ykiselev.lwjgl3.host.ProgramArguments;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class Main {

    public static void main(String[] args) {
        final ProgramArguments programArguments = new ProgramArguments(args);
        new AppBuilder(
                new Host(
                        programArguments,
                        services -> new MainLoop(
                                programArguments, services
                        ).run()
                )
        ).withGlfw()
                .withErrorCallback()
                .withExceptionCatching()
                .withLogging()
                .run();
    }
}
