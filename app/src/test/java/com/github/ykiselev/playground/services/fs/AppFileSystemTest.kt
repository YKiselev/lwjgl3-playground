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
package com.github.ykiselev.playground.services.fs

import com.github.ykiselev.spi.services.FileSystem
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.util.*
import kotlin.io.path.deleteIfExists

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AppFileSystemTest {

    private var fs: FileSystem? = null
    private var dir1: Path? = null
    private var dir2: Path? = null
    private var file1: Path? = null
    private var file2: Path? = null

    @BeforeAll
    @Throws(IOException::class)
    fun setUp() {
        dir1 = Files.createTempDirectory("dir1")
        dir2 = Files.createTempDirectory("dir2")
        file1 = Files.createFile(dir1!!.resolve("b.txt"))
        file2 = Files.createFile(dir2!!.resolve("b.txt"))
        fs = AppFileSystem(
            DiskResources(dir1!!, dir2!!),
            ClassPathResources(javaClass.getClassLoader())
        )
    }

    @AfterAll
    @Throws(IOException::class)
    fun tearDown() {
        file1?.deleteIfExists()
        file2?.deleteIfExists()
        dir1?.deleteIfExists()
        dir2?.deleteIfExists()
    }

    @Test
    @Throws(IOException::class)
    fun shouldOpenFromClassPath() {
        fs!!.open("folder1/a").use {
            assertNotNull(it)
        }
    }

    @Test
    @Throws(IOException::class)
    fun shouldOpenAllFromClassPath() {
        val list = fs!!.openAll("folder1/a").toList()
        assertEquals(1, list.size)
        for (channel in list) {
            channel.close()
        }
    }

    @Test
    @Throws(IOException::class)
    fun shouldOpenFile() {
        fs!!.open("b.txt").use {
            assertNotNull(it)
        }
    }

    @Test
    @Throws(IOException::class)
    fun shouldOpenAllFiles() {
        val list = fs!!.openAll("b.txt").toList()
        assertEquals(2, list.size)
        for (channel in list) {
            channel.close()
        }
    }
}