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

package com.github.ykiselev.playground.services.config;

import java.util.List;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;

final class ConstantList {

    private final List<?> list;

    List<?> list() {
        return list;
    }

    ConstantList(List<?> list) {
        this.list = requireNonNull(list);
    }

    <T> List<T> toUniformList(Class<T> itemClass) {
        return list.stream()
                .map(itemClass::cast)
                .collect(Collectors.toList());
    }
}

