package com.github.ykiselev.lwjgl3.fs;

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