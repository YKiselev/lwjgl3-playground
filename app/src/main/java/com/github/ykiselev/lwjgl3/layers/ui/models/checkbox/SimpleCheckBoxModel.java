package com.github.ykiselev.lwjgl3.layers.ui.models.checkbox;

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
