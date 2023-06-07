#version 300 es
precision mediump float;

//uniform sampler2D u_Texture;

in vec4 color;
in vec3 position;

out vec4 fragColor;

void main(){
    fragColor = color ;//* texture2D(u_Texture, tex);
}