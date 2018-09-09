package com.github.ykiselev.lwjgl3.config;

import java.util.Set;

final class Section {

    private final Set<String> names;

    Set<String> names() {
        return names;
    }

    Section(Set<String> names) {
        this.names = names;
    }
}
