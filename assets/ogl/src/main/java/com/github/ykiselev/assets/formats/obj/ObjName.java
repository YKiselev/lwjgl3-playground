package com.github.ykiselev.assets.formats.obj;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static java.util.Objects.requireNonNull;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class ObjName implements Iterable<ObjFace> {

    private final String name;

    private final List<ObjFace> faces = new ArrayList<>();

    public String name() {
        return name;
    }

    public ObjName(String name) {
        this.name = requireNonNull(name);
    }

    public void addFace(ObjFace face) {
        faces.add(face);
    }

    @Override
    public Iterator<ObjFace> iterator() {
        return faces.iterator();
    }
}
