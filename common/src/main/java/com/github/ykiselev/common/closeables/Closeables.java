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

package com.github.ykiselev.common.closeables;

import com.github.ykiselev.wrap.Wrap;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class Closeables {

    public static void closeAll(AutoCloseable... closeables) {
        RuntimeException ex = null;
        for (AutoCloseable closeable : closeables) {
            if (closeable == null) {
                continue;
            }
            var re = closeSilently(closeable);
            if (re != null) {
                if (ex != null) {
                    ex.addSuppressed(re);
                } else {
                    ex = re;
                }
            }
        }
        if (ex != null) {
            throw new RuntimeException("Failed to close all delegates!", ex);
        }
    }

    public static void closeIfNeeded(Object obj) throws RuntimeException {
        if (obj instanceof AutoCloseable) {
            close((AutoCloseable) obj);
        }
    }

    public static void close(AutoCloseable obj) throws RuntimeException {
        try {
            obj.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static RuntimeException closeSilently(AutoCloseable obj) throws RuntimeException {
        try {
            obj.close();
            return null;
        } catch (Exception e) {
            return new RuntimeException(e);
        }
    }

    /**
     *
     */
    public interface Guard extends AutoCloseable {

        void add(AutoCloseable closeable);

        <T> T add(Wrap<? extends T> wrap);

        AutoCloseable detach();

        @Override
        void close();
    }

    /**
     *
     * @return new guard instance
     */
    public static Guard newGuard() {
        return new Guard() {

            private final List<AutoCloseable> closeables = new ArrayList<>();

            @Override
            public void add(AutoCloseable closeable) {
                closeables.add(closeable);
            }

            @Override
            public <T> T add(Wrap<? extends T> wrap) {
                closeables.add(wrap);
                return wrap.value();
            }

            @Override
            public AutoCloseable detach() {
                final AutoCloseable[] ca = closeables.toArray(new AutoCloseable[0]);
                closeables.clear();
                return new CompositeAutoCloseable(ca);
            }

            @Override
            public void close() {
                if (!closeables.isEmpty()) {
                    Closeables.closeAll(closeables.toArray(new AutoCloseable[0]));
                }
            }
        };
    }
}
