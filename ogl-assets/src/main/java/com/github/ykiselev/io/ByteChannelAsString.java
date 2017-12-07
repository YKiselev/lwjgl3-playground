package com.github.ykiselev.io;

import com.github.ykiselev.assets.ResourceException;
import org.apache.commons.io.IOUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.Charset;

import static java.util.Objects.requireNonNull;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class ByteChannelAsString {

    private final ReadableByteChannel channel;

    private final Charset charset;

    public ByteChannelAsString(ReadableByteChannel channel, Charset charset) {
        this.channel = requireNonNull(channel);
        this.charset = requireNonNull(charset);
    }

    public String read() throws ResourceException {
        try (Reader reader = new BufferedReader(Channels.newReader(channel, charset.newDecoder(), -1))) {
            return IOUtils.toString(reader);
        } catch (IOException e) {
            throw new ResourceException(e);
        }
    }
}
