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
package com.github.ykiselev.spi

import java.io.IOException
import java.io.UncheckedIOException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

/**
 * Supported program arguments includes:
 *
 *  * Base folder, containing base mod assets (read-only)
 *  * User folder, to store persisted configs (writable)
 *
 *
 *
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
class ProgramArguments(rawArgs: Array<String>) {

    private val args: List<String> = rawArgs.toList()

    private val pairs: Map<String, String> = args.asSequence()
        .mapNotNull {
            val k = it.indexOf("=")
            if (k > 0) {
                it.substring(0, k) to it.substring(k + 1)
            } else null
        }.toMap()

    /**
     * Searches arguments for key=value pair.
     *
     * @param key the key name
     * @return the value or `null`
     */
    private fun value(key: String): String? = pairs[key]

    private fun hasSwitch(key: String): Boolean {
        for (arg in args) {
            if (arg == key) {
                return true
            }
        }
        return false
    }

    /**
     * Ordered list of asset directories. First element is always [ProgramArguments.home] path, the rest is
     * directories specified via "asset.paths" program argument.
     *
     * @return ordered list of directories to search for assets.
     */
    fun assetPaths(): Collection<Path> =
        (sequenceOf(home) + (value("assets")
            ?.splitToSequence(",".toRegex())
            ?.filter { it.isNotEmpty() }
            ?.map {
                Paths.get(it)
            } ?: emptySequence()))
            .filter {
                Files.isDirectory(it)
            }.toList()


    val fullScreen: Boolean = hasSwitch("-fullscreen")

    val debug: Boolean = hasSwitch("-debug")

    val swapInterval: Int = value("swap.interval")?.toIntOrNull() ?: 1

    /**
     * @return the index of monitor to use
     */
    val monitor: Int = value("monitor")?.toInt() ?: 0

    /**
     * Writable folder to store mod configs, caches, etc.
     *
     * @return the path to write-enabled folder.
     */
    val home: Path by lazy {
        val path = value("home")?.let { Paths.get(it) } ?: defaultHomePath
        if (!Files.exists(path)) {
            try {
                Files.createDirectories(path)
            } catch (e: IOException) {
                throw UncheckedIOException(e)
            }
        }
        path
    }

    private val defaultHomePath: Path
        /**
         * @return default home path - ${user.home}/${app.folder}/${mod}
         */
        get() = Paths.get(
            System.getProperty("user.home"),
            System.getProperty("app.folder", "lwjgl3-playground"),
            System.getProperty("mod", "base")
        )
}
