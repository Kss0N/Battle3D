package se.lth.cs.student.battle3d.gfx

import jglm.{
    Jglm,
    Mat4,
    Vec3,
}



object Camera:
    private var _matrix: Mat4 = new Mat4()

    var fov = (45.0f * math.Pi/180).toFloat
    var nearPlane = 0.1f
    var  farPlane = 100.0f

    def matrix: Mat4 = Jglm.perspective( 
        fov,
        Display.dim(0)/Display.dim(1),  //Aspect Ratio width/height
        nearPlane,                      //clip near
        farPlane)                       //clip far
        .mult(_matrix)             

    def move(pos : Vec3): Unit = 
        _matrix = _matrix.mult(Mat4.translate(pos))
    def rotateX(angle: Float): Unit = 
        _matrix = _matrix.mult(Mat4.rotationX(angle))
    def rotateY(angle: Float): Unit = 
        _matrix = _matrix.mult(Mat4.rotationY(angle))
    def rotateZ(angle: Float): Unit = 
        _matrix = _matrix.mult(Mat4.rotationZ(angle))


