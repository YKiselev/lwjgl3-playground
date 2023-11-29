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

import com.github.ykiselev.assets.Assets
import com.github.ykiselev.common.closeables.Closeables.close
import com.github.ykiselev.common.closeables.Closeables.closeAll
import com.github.ykiselev.playground.layers.menu.Menu
import com.github.ykiselev.spi.services.commands.Commands
import com.github.ykiselev.spi.services.configuration.PersistedConfiguration
import com.github.ykiselev.spi.services.layers.UiLayers
import com.github.ykiselev.spi.services.schedule.Repeatable
import com.github.ykiselev.spi.services.schedule.Schedule
import java.util.concurrent.TimeUnit
import java.util.function.Supplier

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
class AppMenu(
    assets: Assets,
    configuration: PersistedConfiguration,
    commands: Commands,
    private val uiLayers: UiLayers,
    private val schedule: Schedule
) : AutoCloseable {

    private val ac: AutoCloseable
    private val menuSupplier: Supplier<Menu>
    private var menu: Menu? = null

    init {
        ac = commands.add("show-menu", ::showMenu)
        menuSupplier = Supplier {
            Menu(
                assets, configuration, commands, uiLayers
            )
        }
    }

    private fun recycle(): Boolean {
        val m = menu
        if (m != null && m.canBeRemoved()) {
            close(m)
            menu = null
            return false
        }
        return true
    }

    override fun close() {
        closeAll(menu, ac)
        menu = null
    }

    private fun showMenu() {
        if (menu == null) {
            menu = menuSupplier.get()
            schedule.schedule(10, TimeUnit.SECONDS, Repeatable { recycle() })
        }
        uiLayers.add(menu!!)
    }
}
