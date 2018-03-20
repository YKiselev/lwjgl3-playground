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

package com.github.ykiselev.assets;

import java.nio.channels.ReadableByteChannel;
import java.util.Optional;

/**
 * Low-level resource access api.
 * <p>
 * Created by Y.Kiselev on 15.05.2016.
 */
public interface Resources {

    /**
     * Opens new read-only channel for requested resource.
     * Caller is expected to close channel after use.
     *
     * @param resource the resource name
     * @return the readable byte channel
     * @throws ResourceException if {@code resource} does not exists or something goes wrong during channel opening
     */
    Optional<ReadableByteChannel> open(String resource) throws ResourceException;
}
