package com.github.ykiselev.assets.formats.obj;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.requireNonNull;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class ObjName {

    private final String name;

    private final List<ObjFace> faces = new ArrayList<>();

    public ObjName(String name) {
        this.name = requireNonNull(name);
    }

    public void addFace(ObjFace face) {
        faces.add(face);
    }
}
