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

package com.github.ykiselev.playground.ui.models.checkbox;

import java.util.function.Consumer;

import static java.util.Objects.requireNonNull;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class SimpleCheckBoxModel implements CheckBoxModel {

    private final Consumer<CheckBoxModel> onChange;

    private boolean checked;

    public SimpleCheckBoxModel(Consumer<CheckBoxModel> onChange, boolean checked) {
        this.onChange = requireNonNull(onChange);
        this.checked = checked;
    }

    @Override
    public boolean checked() {
        return checked;
    }

    @Override
    public void checked(boolean value) {
        if (checked != value) {
            checked = value;
            onChange.accept(this);
        }
    }
}
