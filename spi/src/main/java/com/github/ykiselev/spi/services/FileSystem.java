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

package com.github.ykiselev.spi.services;

import com.github.ykiselev.assets.Resources;

import java.nio.channels.WritableByteChannel;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public interface FileSystem extends Resources {

    /**
     * @param name   the file name that will be resolved relative to first writable folder.
     * @param append {@code true} to append to file or {@code false} to truncate file if it exists.
     * @return writable channel
     */
    WritableByteChannel openForWriting(String name, boolean append);

}
