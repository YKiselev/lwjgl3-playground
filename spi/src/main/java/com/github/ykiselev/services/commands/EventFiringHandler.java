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

package com.github.ykiselev.services.commands;

import com.github.ykiselev.services.Services;
import com.github.ykiselev.services.events.EventFromCommand;
import com.github.ykiselev.services.events.Events;

import java.util.List;

import static java.util.Objects.requireNonNull;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class EventFiringHandler<E> implements Command {

    private final String name;

    private final Services services;

    private final EventFromCommand<E> factory;

    public EventFiringHandler(String name, Services services, EventFromCommand<E> factory) {
        this.name = requireNonNull(name);
        this.services = requireNonNull(services);
        this.factory = requireNonNull(factory);
    }

    @Override
    public void run(List<String> args) throws Exception {
        services.resolve(Events.class)
                .fire(factory.fromCommand(args));
    }

    @Override
    public String name() {
        return name;
    }
}
