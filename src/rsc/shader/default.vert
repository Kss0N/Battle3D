#version 450 core

layout (location = 0) in vec3 aGeom;
layout (location = 1) in vec3 aNorm;
layout (location = 2) in vec2 aText;

uniform mat4 cam;

out vec2 texCoord;
out vec3 normal;

uniform mat4 model = mat4(  
    1,0,0,0,
    0,1,0,0,
    0,0,1,0,
    0,0,0,1
);
uniform mat4 camera;



void main(){
    gl_Position = camera * model * vec4(aGeom, 1.0);

    normal  = aNorm;
    texCoord= aText;
}