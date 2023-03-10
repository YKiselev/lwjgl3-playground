#version 330 core

in vec2 vs_TexCoord;

in vec4 vs_Normal;

layout (location = 0) out vec4 FragColor;

layout (location = 1) out vec4 FragNormal;

uniform sampler2D tex;

void main()
{
    FragColor = texture(tex, vs_TexCoord);
    FragNormal = vec4(1,0,1,1);// vs_Normal;
}