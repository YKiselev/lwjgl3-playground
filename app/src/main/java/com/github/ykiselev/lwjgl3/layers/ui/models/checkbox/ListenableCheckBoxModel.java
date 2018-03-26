package com.github.ykiselev.lwjgl3.layers.ui.models.checkbox;

import static java.util.Objects.requireNonNull;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class ListenableCheckBoxModel implements CheckBoxModel {

    private final CheckBoxEventListener listener;

    private boolean checked;

    public ListenableCheckBoxModel(CheckBoxEventListener listener) {
        this.listener = requireNonNull(listener);
    }

    @Override
    public boolean checked() {
        return checked;
    }

    @Override
    public void checked(boolean value) {
        if (this.checked != value) {
            checked = value;
            listener.onCheckBoxChanged(this);
        }
    }
}
