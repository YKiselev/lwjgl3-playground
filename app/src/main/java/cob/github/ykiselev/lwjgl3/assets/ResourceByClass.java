package cob.github.ykiselev.lwjgl3.assets;

import com.github.ykiselev.assets.ReadableResource;
import com.github.ykiselev.assets.ReadableResources;
import com.github.ykiselev.assets.ResourceException;
import com.github.ykiselev.assets.formats.ReadableConfig;
import com.github.ykiselev.assets.formats.ReadableObjModel;
import com.github.ykiselev.assets.formats.ReadableProgramObject;
import com.github.ykiselev.assets.formats.ReadableShaderObject;
import com.github.ykiselev.assets.formats.ReadableSpriteFont;
import com.github.ykiselev.assets.formats.ReadableTexture2d;
import com.github.ykiselev.assets.formats.obj.ObjModel;
import com.github.ykiselev.opengl.shaders.ProgramObject;
import com.github.ykiselev.opengl.shaders.ShaderObject;
import com.github.ykiselev.opengl.text.SpriteFont;
import com.github.ykiselev.opengl.textures.Texture2d;
import com.google.common.collect.ImmutableMap;
import com.typesafe.config.Config;

import java.util.Map;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class ResourceByClass implements ReadableResources {

    private final Map<Class, ReadableResource> map;

    public ResourceByClass(Map<Class, ReadableResource> map) {
        this.map = map;
    }

    public ResourceByClass() {
        this(
                ImmutableMap.<Class, ReadableResource>builder()
                        .put(Config.class, new ReadableConfig())
                        .put(ProgramObject.class, new ReadableProgramObject())
                        .put(ShaderObject.class, new ReadableShaderObject())
                        .put(SpriteFont.class, new ReadableSpriteFont())
                        .put(Texture2d.class, new ReadableTexture2d())
                        .put(ObjModel.class, new ReadableObjModel())
                        .build()
        );
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> ReadableResource<T> resolve(String resource, Class<T> clazz) throws ResourceException {
        return map.get(clazz);
    }
}
