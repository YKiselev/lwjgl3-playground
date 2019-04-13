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

package com.github.ykiselev.opengl.sandbox;

import com.github.ykiselev.common.io.ByteChannelAsByteBuffer;
import com.github.ykiselev.wrap.Wrap;
import org.lwjgl.stb.STBIEOFCallback;
import org.lwjgl.stb.STBIIOCallbacks;
import org.lwjgl.stb.STBIReadCallback;
import org.lwjgl.stb.STBISkipCallback;
import org.lwjgl.system.MemoryStack;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Paths;
import java.util.concurrent.atomic.AtomicInteger;

import static org.lwjgl.stb.STBImage.stbi_failure_reason;
import static org.lwjgl.stb.STBImage.stbi_image_free;
import static org.lwjgl.stb.STBImage.stbi_info_from_memory;
import static org.lwjgl.stb.STBImage.stbi_load_from_callbacks;
import static org.lwjgl.stb.STBImage.stbi_load_from_memory;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class StbImageApp {

    public static void main(String[] args) throws Exception {
        new StbImageApp().run();
    }

    private void run() throws Exception {
        final long t0 = System.currentTimeMillis();
        loadByChunksAndPrintInfo(new File("d:\\Myriad Web Pro.png").toURI());
        //loadAndPrintInfo(new File("d:\\Myriad Web Pro.png").toURI());
//        printInfo(res("/images/8bpp.png"));
//        printInfo(res("/images/8bpp2.png"));
//        printInfo(res("/images/8bpp3.png"));
//        printInfo(res("/images/24bpp.png"));
//        printInfo(res("/images/32bpp1.png"));
//        printInfo(res("/images/32bpp2.png"));
        //printInfo(new File("d:\\Myriad Web Pro.png").toURI());
        final long t1 = System.currentTimeMillis();
        System.out.println("Time (msec): " + (t1 - t0));
    }

    private URI res(String name) throws URISyntaxException {
        return getClass().getResource(name).toURI();
    }

    private void printInfo(URI resource) throws Exception {
        System.out.println("Checking image: " + resource);
        try (Wrap<ByteBuffer> wrap = loadImage(resource)) {
            try (MemoryStack ms = MemoryStack.stackPush()) {
                final IntBuffer wb = ms.callocInt(1);
                final IntBuffer hb = ms.callocInt(1);
                final IntBuffer compb = ms.callocInt(1);
                if (stbi_info_from_memory(wrap.value(), wb, hb, compb)) {
                    print(wb, hb, compb, null);
                } else {
                    System.out.println("Unable to read info: " + stbi_failure_reason());
                }
            }
        }
    }

    private void print(IntBuffer wb, IntBuffer hb, IntBuffer compb, ByteBuffer image) {
        System.out.println(
                "Dimensions: " + wb.get(0) + "x" + hb.get(0) + ", components: " + compb.get(0) + (
                        image != null
                                ? ", " + image.limit() + " byte(s)"
                                : ""
                )
        );
    }

    private void loadAndPrintInfo(URI resource) throws Exception {
        System.out.println("Checking image: " + resource);
        try (Wrap<ByteBuffer> wrap = loadImage(resource)) {
            try (MemoryStack ms = MemoryStack.stackPush()) {
                final IntBuffer wb = ms.callocInt(1);
                final IntBuffer hb = ms.callocInt(1);
                final IntBuffer compb = ms.callocInt(1);
                final ByteBuffer image = stbi_load_from_memory(wrap.value(), wb, hb, compb, 0);
                if (image != null) {
                    try {
                        print(wb, hb, compb, image);
                    } finally {
                        stbi_image_free(image);
                    }
                } else {
                    System.out.println("Unable to read info: " + stbi_failure_reason());
                }
            }
        }
    }

    private Wrap<ByteBuffer> loadImage(URI resource) throws Exception {
        try (ReadableByteChannel channel = FileChannel.open(Paths.get(resource))) {
            return new ByteChannelAsByteBuffer(
                    channel,
                    8 * 1024
            ).read();
        }
    }

    private void loadByChunksAndPrintInfo(URI resource) throws IOException {
        System.out.println("Loading image: " + resource);
        try (ReadableByteChannel channel = FileChannel.open(Paths.get(resource))) {
            try (MemoryStack ms = MemoryStack.stackPush()) {
                final IntBuffer wb = ms.callocInt(1);
                final IntBuffer hb = ms.callocInt(1);
                final IntBuffer compb = ms.callocInt(1);
                final STBIIOCallbacks callbacks = STBIIOCallbacks.calloc();
                try {
                    final AtomicInteger flag = new AtomicInteger();
                    final STBIReadCallback read = new STBIReadCallback() {
                        @Override
                        public int invoke(long user, long data, int size) {
                            final ByteBuffer buffer = getData(data, size);
                            try {
                                int read = channel.read(buffer);
                                if (read == -1) {
                                    flag.set(1);
                                    read = 0;
                                }
                                return read;
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            return 0;
                        }
                    };
                    final STBISkipCallback skip = new STBISkipCallback() {
                        @Override
                        public void invoke(long user, int n) {
                            //throw new UnsupportedOperationException("Not supported!");
                        }
                    };
                    final STBIEOFCallback eof = new STBIEOFCallback() {
                        @Override
                        public int invoke(long user) {
                            return flag.get();
                        }
                    };
                    callbacks.read(read);
                    callbacks.skip(skip);
                    callbacks.eof(eof);
                    final ByteBuffer image = stbi_load_from_callbacks(callbacks, 0L, wb, hb, compb, 0);
                    if (image == null) {
                        System.out.println("Unable to read info: " + stbi_failure_reason());
                    } else {
                        print(wb, hb, compb, image);
                        stbi_image_free(image);
                    }
                } finally {
                    callbacks.free();
                }
            }
        }
    }

}
