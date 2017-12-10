package com.github.ykiselev.io;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.NoSuchElementException;

import static java.util.Objects.requireNonNull;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class ByteChannelAsLines implements Iterable<String>, Closeable {

    private final ReadableByteChannel channel;

    private final Charset charset;

    public ByteChannelAsLines(ReadableByteChannel channel, Charset charset) {
        this.channel = requireNonNull(channel);
        this.charset = requireNonNull(charset);
    }

    @Override
    public Iterator<String> iterator() {
        return new Iterator<>() {

            final BufferedReader reader = new BufferedReader(
                    Channels.newReader(
                            channel,
                            charset.newDecoder(), -1
                    )
            );

            private String line;

            @Override
            public boolean hasNext() {
                if (line == null) {
                    try {
                        line = reader.readLine();
                    } catch (IOException e) {
                        throw new UncheckedIOException(e);
                    }
                }
                return line != null;
            }

            @Override
            public String next() {
                if (line != null || hasNext()) {
                    final String result = line;
                    line = null;
                    return result;
                }
                throw new NoSuchElementException();
            }
        };
    }

    @Override
    public void close() throws IOException {
        channel.close();
    }
}
