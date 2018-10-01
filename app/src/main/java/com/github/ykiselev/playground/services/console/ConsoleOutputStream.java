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

package com.github.ykiselev.playground.services.console;

import java.io.ByteArrayOutputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class ConsoleOutputStream extends FilterOutputStream {

    private final ByteArrayOutputStream buffer = new ByteArrayOutputStream();

    public ConsoleOutputStream(OutputStream delegate) {
        super(delegate);
    }

    @Override
    public void write(int b) throws IOException {
        buffer.write(b);
        //super.write(b);
    }

    @Override
    public void write(byte[] b) throws IOException {
        buffer.write(b);
        //super.write(b);
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        buffer.write(b, off, len);
        //super.write(b, off, len);
    }

    @Override
    public void flush() throws IOException {
        if (buffer.size() > 0) {
            buffer.writeTo(out);
            buffer.reset();
        }
        super.flush();
    }

}
