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

import com.github.ykiselev.spi.services.commands.Commands.*

/**
 * This class is a collection of command handler adaptors.
 *
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
internal object Handlers {
    private fun assertArgs(args: List<String>, max: Int) {
        require(args.isNotEmpty()) { "At least one argument is expected!" }
        require(args.size <= max) { "Too many arguments!" }
    }

    private fun List<String>.tryGet(index: Int): String =
        if (this.size > index) this[index] else ""

    @JvmStatic
    fun command(name: String, handler: H0): Command {
        return object : Command(name) {
            @Throws(Exception::class)
            override fun run(args: List<String>) {
                assertArgs(args, 1)
                handler.handle()
            }
        }
    }

    @JvmStatic
    fun command(name: String, handler: H1): Command {
        return object : Command(name) {
            @Throws(Exception::class)
            override fun run(args: List<String>) {
                assertArgs(args, 2)
                handler.handle(args.tryGet(1))
            }
        }
    }

    @JvmStatic
    fun command(name: String, handler: H2): Command {
        return object : Command(name) {
            @Throws(Exception::class)
            override fun run(args: List<String>) {
                assertArgs(args, 3)
                handler.handle(args.tryGet(1), args.tryGet(2))
            }
        }
    }

    @JvmStatic
    fun command(name: String, handler: H3): Command {
        return object : Command(name) {
            @Throws(Exception::class)
            override fun run(args: List<String>) {
                assertArgs(args, 4)
                handler.handle(args.tryGet(1), args.tryGet(2), args.tryGet(3))
            }
        }
    }

    @JvmStatic
    fun command(name: String, handler: H4): Command {
        return object : Command(name) {
            @Throws(Exception::class)
            override fun run(args: List<String>) {
                assertArgs(args, 5)
                handler.handle(args.tryGet(1), args.tryGet(2), args.tryGet(3), args.tryGet(4))
            }
        }
    }

    @JvmStatic
    fun command(name: String, handler: H5): Command {
        return object : Command(name) {
            @Throws(Exception::class)
            override fun run(args: List<String>) {
                assertArgs(args, 6)
                handler.handle(args.tryGet(1), args.tryGet(2), args.tryGet(3), args.tryGet(4), args.tryGet(5))
            }
        }
    }

    @JvmStatic
    fun command(name: String, handler: H6): Command {
        return object : Command(name) {
            @Throws(Exception::class)
            override fun run(args: List<String>) {
                assertArgs(args, 7)
                handler.handle(
                    args.tryGet(1),
                    args.tryGet(2),
                    args.tryGet(3),
                    args.tryGet(4),
                    args.tryGet(5),
                    args.tryGet(6)
                )
            }
        }
    }
}
