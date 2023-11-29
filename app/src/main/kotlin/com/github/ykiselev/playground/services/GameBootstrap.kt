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
package com.github.ykiselev.playground.services

import com.github.ykiselev.common.closeables.Closeables.close
import com.github.ykiselev.common.closeables.Closeables.newGuard
import com.github.ykiselev.spi.GameFactory
import com.github.ykiselev.spi.api.Updatable
import com.github.ykiselev.spi.components.Game
import java.util.*

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
class GameBootstrap(val context: AppContext) : Updatable, AutoCloseable {

    private var game: Game? = null
    private var closeable: AutoCloseable

    init {
        newGuard().use { guard ->
            guard.add(
                context.commands.add()
                    .with("new-game", ::newGame)
                    .build()
            )
            guard.add(
                context.configuration.wire()
                    .withBoolean("game.isPresent", { game != null }, false)
                    .build()
            )
            closeable = guard.detach()
        }
    }

    override fun update() {
        game?.update()
    }

    override fun close() {
        closeGame()
        close(closeable)
    }

    private fun newGame() {
        closeGame()
        game = ServiceLoader.load(GameFactory::class.java)
            .findFirst()
            .map { it.create(context.toGameFactoryArgs()) }
            .orElseThrow { IllegalStateException("Game factory service not found!") }
        with(context.uiLayers) {
            add(game!!)
            removePopups()
        }
    }

    private fun closeGame() {
        game = game?.let {
            context.uiLayers.remove(it)
            close(it)
            null
        }
    }
}
