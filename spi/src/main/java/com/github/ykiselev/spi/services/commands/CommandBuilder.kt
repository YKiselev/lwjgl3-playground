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
package com.github.ykiselev.spi.services.commands

import com.github.ykiselev.common.closeables.Closeables
import com.github.ykiselev.spi.services.commands.Commands.*
import com.github.ykiselev.spi.services.commands.Handlers.command
import java.util.function.Consumer

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
data class CommandBuilder(
    private val commands: Commands,
    private val ac: Closeables.Composite = Closeables.compose()
) {

    fun with(handler: Command): CommandBuilder =
        copy(ac = ac.and(commands.add(handler)))

    fun with(command: String, handler: Consumer<List<String>>): CommandBuilder =
        copy(ac = ac.and(commands.add(command, handler)))

    fun with(command: String, handler: H0): CommandBuilder =
        copy(ac = ac.and(commands.add(command(command, handler))))

    fun with(command: String, handler: H1): CommandBuilder =
        copy(ac = ac.and(commands.add(command(command, handler))))

    fun with(command: String, handler: H2): CommandBuilder =
        copy(ac = ac.and(commands.add(command(command, handler))))

    fun with(command: String, handler: H3): CommandBuilder =
        copy(ac = ac.and(commands.add(command(command, handler))))

    fun with(command: String, handler: H4): CommandBuilder =
        copy(ac = ac.and(commands.add(command(command, handler))))

    fun with(command: String, handler: H5): CommandBuilder =
        copy(ac = ac.and(commands.add(command(command, handler))))

    fun with(command: String, handler: H6): CommandBuilder =
        copy(ac = ac.and(commands.add(command(command, handler))))

    fun build(): Closeables.Composite = ac

}
