#version 150 core

in vec2 vs_TexCoord;

in vec4 vs_Color;

uniform sampler2D tex;

void main()
{
    gl_FragColor = vs_Color * texture(tex, vs_TexCoord);
}