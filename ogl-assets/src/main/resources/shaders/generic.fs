#version 150 core

in vec2 vs_TexCoord;

in vec4 vs_Normal;

uniform sampler2D tex;

void main()
{
    gl_FragColor = texture(tex, vs_TexCoord);
}