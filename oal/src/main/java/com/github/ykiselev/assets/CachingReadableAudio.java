package com.github.ykiselev.assets;

import com.github.ykiselev.caching.Cached;
import com.github.ykiselev.caching.CachedReferences;
import com.github.ykiselev.openal.AudioSamples;

import java.nio.channels.ReadableByteChannel;
import java.util.Optional;
import java.util.function.Supplier;

import static java.util.Objects.requireNonNull;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class CachingReadableAudio implements ReadableResource<AudioSamples> {

    private final CachedReferences<AudioSamples> cache;

    private final ReadableResource<AudioSamples> delegate;

    public CachingReadableAudio(CachedReferences<AudioSamples> cache, ReadableResource<AudioSamples> delegate) {
        this.cache = requireNonNull(cache);
        this.delegate = requireNonNull(delegate);
    }

    @Override
    public Optional<AudioSamples> read(String resource, Assets assets) throws ResourceException {
        return delegate.read(resource, assets);
    }

    @Override
    public AudioSamples read(ReadableByteChannel channel, String resource, Assets assets) throws ResourceException {
        throw new UnsupportedOperationException();
    }

    /**
     * Cached audio samples
     */
    private static class CachedAudioSamples implements AudioSamples {

        private final CachedReferences<AudioSamples> cache;

        private final Supplier<AudioSamples> supplier;

        private volatile Cached<AudioSamples> cached;

        public CachedAudioSamples(Supplier<AudioSamples> supplier, CachedReferences<AudioSamples> cache) {
            this.cache = requireNonNull(cache);
            this.supplier = requireNonNull(supplier);
        }

        private AudioSamples ensureLoaded() {
            final AudioSamples existing = cached.get();
            if (existing != null) {
                return existing;
            }
            final AudioSamples loaded = requireNonNull(supplier.get());
            cached = cache.put(loaded);
            return loaded;
        }

        @Override
        public int format() {
            return ensureLoaded().format();
        }

        @Override
        public int sampleRate() {
            return ensureLoaded().sampleRate();
        }

        @Override
        public int length() {
            return ensureLoaded().length();
        }

        @Override
        public void buffer(int buffer) {
            ensureLoaded().buffer(buffer);
        }

        @Override
        public void close() {
            // no-op here, cache manages this for us
        }
    }
}
