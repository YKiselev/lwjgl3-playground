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

package com.github.ykiselev.lwjgl3.services.fs;

import com.github.ykiselev.services.FileSystem;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.io.IOException;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AppFileSystemTest {

    private FileSystem fs;

    private Path dir1;

    private Path dir2;

    private Path file1;

    private Path file2;

    @BeforeAll
    void setUp() throws IOException {
        dir1 = Files.createTempDirectory("dir1");
        dir2 = Files.createTempDirectory("dir2");
        file1 = Files.createFile(dir1.resolve("b.txt"));
        file2 = Files.createFile(dir2.resolve("b.txt"));
        fs = new AppFileSystem(
                new DiskResources(dir1, dir2),
                new ClassPathResources(getClass().getClassLoader())
        );
    }

    @AfterAll
    void tearDown() throws IOException {
        Files.delete(file1);
        Files.delete(file2);
        Files.delete(dir1);
        Files.delete(dir2);
    }

    @Test
    void shouldOpenFromClassPath() throws IOException {
        Optional<ReadableByteChannel> r = fs.open("folder1/a");
        assertTrue(r.isPresent());
        r.get().close();
    }

    @Test
    void shouldOpenAllFromClassPath() throws IOException {
        ReadableByteChannel[] array = fs.openAll("folder1/a")
                .toArray(ReadableByteChannel[]::new);
        assertEquals(1, array.length);
        for (ReadableByteChannel channel : array) {
            channel.close();
        }
    }

    @Test
    void shouldOpenFile() throws IOException {
        Optional<ReadableByteChannel> r = fs.open("b.txt");
        assertTrue(r.isPresent());
        r.get().close();
    }

    @Test
    void shouldOpenAllFiles() throws IOException {
        ReadableByteChannel[] array = fs.openAll("b.txt").toArray(ReadableByteChannel[]::new);
        assertEquals(2, array.length);
        for (ReadableByteChannel channel : array) {
            channel.close();
        }
    }

}