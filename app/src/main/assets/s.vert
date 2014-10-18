attribute vec3 position;

uniform vec2  resolution;
uniform float time;

varying vec2  iResolution;
varying float iGlobalTime;

void main()
{
    iGlobalTime = time;
    iResolution = resolution;
    gl_Position =  vec4(position, 1.0);

}