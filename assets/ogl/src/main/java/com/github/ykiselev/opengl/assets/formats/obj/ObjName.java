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

package com.github.ykiselev.opengl.assets.formats.obj;

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
