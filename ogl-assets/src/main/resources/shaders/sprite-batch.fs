#version 150 core

in vec2 in_TexCoord;

in vec4 vs_Color;

out vec4 outColor;

uniform sampler2D tex;

void main()
{
    outColor = vs_Color * texture(tex, in_TexCoord);
}