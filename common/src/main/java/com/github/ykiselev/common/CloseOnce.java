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

package com.github.ykiselev.common;

import static java.util.Objects.requireNonNull;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 * @deprecated seems to be unused
 */
@Deprecated
public final class CloseOnce implements AutoCloseable {

    private volatile AutoCloseable closeable;

    public boolean isClosed() {
        return closeable == null;
    }

    public CloseOnce(AutoCloseable delegate) {
        this.closeable = requireNonNull(delegate);
    }

    @Override
    public synchronized void close() throws Exception {
        final AutoCloseable c = closeable;
        if (c != null) {
            try {
                c.close();
            } finally {
                closeable = null;
            }
        }
    }
}
