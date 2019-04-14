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

package com.github.ykiselev.opengl;

import com.github.ykiselev.assets.DefaultRecipe;
import com.github.ykiselev.assets.Recipe;
import com.github.ykiselev.opengl.assets.formats.ReadableTexture2d;
import com.github.ykiselev.opengl.assets.formats.obj.ObjModel;
import com.github.ykiselev.opengl.fonts.FontAtlas;
import com.github.ykiselev.opengl.fonts.TrueTypeFontInfo;
import com.github.ykiselev.opengl.shaders.ProgramObject;
import com.github.ykiselev.opengl.shaders.ShaderObject;
import com.github.ykiselev.opengl.text.SpriteFont;
import com.github.ykiselev.opengl.textures.Texture2d;
import com.typesafe.config.Config;

/**
 * @author Yuriy Kiselev (uze@yandex.ru)
 * @since 14.04.2019
 */
public final class OglRecipes {

    public static final Recipe<ShaderObject, Void> SHADER = DefaultRecipe.of(ShaderObject.class);

    public static final Recipe<Texture2d, ReadableTexture2d.Context> MIP_MAP_TEXTURE = new DefaultRecipe<>("mip-map-texture", Texture2d.class, new ReadableTexture2d.Context(true));

    public static final Recipe<Texture2d, ReadableTexture2d.Context> SPRITE = new DefaultRecipe<>("sprite", Texture2d.class, new ReadableTexture2d.Context(false));

    public static final Recipe<SpriteFont, Void> SPRITE_FONT = DefaultRecipe.of(SpriteFont.class);

    public static final Recipe<Config, Void> CONFIG = DefaultRecipe.of(Config.class);

    public static final Recipe<ProgramObject, Void> PROGRAM = DefaultRecipe.of(ProgramObject.class);

    public static final Recipe<FontAtlas, Void> FONT_ATLAS = DefaultRecipe.of(FontAtlas.class);

    public static final Recipe<TrueTypeFontInfo, Void> TRUE_TYPE_FONT_INFO = DefaultRecipe.of(TrueTypeFontInfo.class);

    public static final Recipe<ObjModel, Void> OBJ_MODEL = DefaultRecipe.of(ObjModel.class);
}
