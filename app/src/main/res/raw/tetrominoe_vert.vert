#version 300 es

uniform mat4 uMVP;
in vec3 vPosition;
in vec4 vColor;

out vec4 color;
out vec3 position;

void main() {
    position = vPosition;
    gl_Position = uMVP * vec4(vPosition, 1.0);
    color = vColor;
}
