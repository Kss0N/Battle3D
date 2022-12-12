#version 450 core

layout (location = 0) in vec3 aGeom;
layout (location = 1) in vec3 aNorm;
layout (location = 2) in vec2 aText;



uniform mat4 model = mat4(  
    1,0,0,0,
    0,1,0,0,
    0,0,1,0,
    0,0,0,1
);
uniform mat4 camera;



void main(){
    gl_Position = vec4(aGeom, 1.0);
}