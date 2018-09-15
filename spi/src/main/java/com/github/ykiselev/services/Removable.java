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

package com.github.ykiselev.services;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public interface Removable {

    /**
     * Implementing classes may use this method to decide if object is not currently in use and may be safely unloaded.
     *
     * @return {@code true} if service is not used at the moment and may be safely removed.
     */
    boolean canBeRemoved();
}
