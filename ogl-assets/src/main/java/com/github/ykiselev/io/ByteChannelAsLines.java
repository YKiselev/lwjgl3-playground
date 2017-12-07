package com.github.ykiselev.io;

import com.github.ykiselev.assets.ResourceException;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.Charset;
import java.util.function.Consumer;

import static java.util.Objects.requireNonNull;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class ByteChannelAsLines implements Consumable<String> {

    private final ReadableByteChannel channel;

    private final Charset charset;

    public ByteChannelAsLines(ReadableByteChannel channel, Charset charset) {
        this.channel = requireNonNull(channel);
        this.charset = requireNonNull(charset);
    }

    @Override
    public void consume(Consumer<String> consumer) throws ResourceException {
        try (BufferedReader reader = new BufferedReader(Channels.newReader(channel, charset.newDecoder(), -1))) {
            reader.lines()
                    .forEach(consumer);
        } catch (IOException e) {
            throw new ResourceException(e);
        }
    }
}
