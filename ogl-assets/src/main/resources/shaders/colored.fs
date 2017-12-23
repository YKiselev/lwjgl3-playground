#version 150 core

in vec3 vs_Color;

void main()
{
    gl_FragColor = vec4(vs_Color, 1);
}