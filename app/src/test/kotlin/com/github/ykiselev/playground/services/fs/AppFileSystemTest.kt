package com.github.ykiselev.playground.services.fs

import com.github.ykiselev.spi.services.FileSystem
import org.junit.jupiter.api.*
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
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
            Assertions.assertNotNull(it)
        }
    }

    @Test
    @Throws(IOException::class)
    fun shouldOpenAllFromClassPath() {
        val list = fs!!.openAll("folder1/a").toList()
        Assertions.assertEquals(1, list.size)
        for (channel in list) {
            channel.close()
        }
    }

    @Test
    @Throws(IOException::class)
    fun shouldOpenFile() {
        fs!!.open("b.txt").use {
            Assertions.assertNotNull(it)
        }
    }

    @Test
    @Throws(IOException::class)
    fun shouldOpenAllFiles() {
        val list = fs!!.openAll("b.txt").toList()
        Assertions.assertEquals(2, list.size)
        for (channel in list) {
            channel.close()
        }
    }
}