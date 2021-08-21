package cga.exercise.game

import cga.exercise.components.Collision.Collision
import cga.exercise.components.camera.TronCamera
import cga.exercise.components.geometry.*
import cga.exercise.components.light.PointLight
import cga.exercise.components.light.SpotLight
import cga.exercise.components.shader.ShaderProgram
import cga.exercise.components.texture.Texture2D
import cga.framework.GLError
import cga.framework.GameWindow
import cga.framework.ModelLoader
import cga.framework.OBJLoader
import org.joml.Math
import org.joml.Vector2f
import org.joml.Vector3f
import org.lwjgl.glfw.GLFW
import org.lwjgl.opengl.GL11.*
import org.lwjgl.opengl.GL14
import cga.exercise.components.sceneMovement.FloorMovement
import cga.exercise.components.sceneMovement.ObjectMovement
import org.lwjgl.opengl.ARBFramebufferSRGB.GL_FRAMEBUFFER_SRGB

/**
 * Created by Fabian on 16.09.2017.
 */
class Scene(private val window: GameWindow) {
    private val staticShader: ShaderProgram = ShaderProgram("assets/shaders/tron_vert.glsl", "assets/shaders/tron_frag.glsl")

    private val floorChecker = FloorMovement()
    private var colision = false
    var gametime = 0

    var cameratimer = 100

    private val obstacleChecker = ObjectMovement()

    private var jump = false
    private var jumpvector = 0.5f

    private var right = false
    private var rightvector = 0.05f
    private var left =false
    private var leftvector = 0.05f

    var laserRight = true
    var powerupTimer = 1000
    var powerUPidleTime = 400

    var laserCooldown = 300

    private var speedMultiplier:Double = 10.0

    val listOfFixObjects = arrayListOf<Renderable>()
    private val fixObjectList = RenderableList(listOfFixObjects)

    val boxList = arrayListOf<Renderable>()
    val tallBoxList = arrayListOf<Renderable>()
    val laserList = arrayListOf<Renderable>()

    val listOfObstacles = arrayListOf<Renderable>()
    private val obstacles = RenderableList(listOfObstacles)
    private val obstaclesLasers = RenderableList(laserList)

    private val coursePart1 = Renderable()
    private val coursePart2 = Renderable()
    private val coursePart3 = Renderable()
    private val coursePart4 = Renderable()
    private val coursePart5 = Renderable()
    private val coursePart6 = Renderable()
    private val coursePart7 = Renderable()
    private val coursePart8 = Renderable()
    private val coursePart9 = Renderable()
    private val coursePart10 = Renderable()

    private val dekolamp1 = Renderable()
    private val dekolamp2 = Renderable()

    private val collisionBox = Collision()
    private val powerup = Renderable()

    private val tronCamera = TronCamera()

    private val motorrad = ModelLoader.loadModel(
        "assets/models/Light Cycle/HQ_Movie cycle.obj",
        Math.toRadians(-90f),
        Math.toRadians(90f),
        0f
    )
    private val bikeColBox = Transformable()

    private val box1 = Renderable()
    private val box2 = Renderable()
    private val box3 = Renderable()
    private val box4 = Renderable()
    private val box5 = Renderable()
    private val box6 = Renderable()

    private val tallBox1 = Renderable()
    private val tallBox2 = Renderable()
    private val tallBox3 = Renderable()
    private val tallBox1in = Renderable()
    private val tallBox2in = Renderable()
    private val tallBox3in = Renderable()

    private val laser = Renderable()
    private val laser2 = Renderable()
    private val movableLaser = Renderable()

    private var texSpec : Texture2D
    private var texEmit : Texture2D
    private var texDiff : Texture2D

    private var texSpecNew : Texture2D
    private var texEmitNew : Texture2D
    private var texDiffNew : Texture2D


    private var texNormalMapNew : Texture2D
    private var texEmptyNormalMap : Texture2D
    private var texObstacleNormalMap : Texture2D

    private var texEmitLaser : Texture2D

    var cameraRight = false
    var cameraLeft = false
    var cameraFront = false
    var cameraBack = false

    private val pointLight : PointLight
    private val spotLight : SpotLight
    private val mouseXPos = window.mousePos.xpos
    private var colorization = Vector3f()

    private var lineNumber = 1
    private var timer = 0

    //scene setup
    init {
        //initial opengl state
        glClearColor(0.0f, 0.0f, 0.0f, 1.0f); GLError.checkThrow()
        glEnable(GL_CULL_FACE); GLError.checkThrow() //Cull-Facing wurde aktiviert
        glFrontFace(GL_CCW); GLError.checkThrow() // Alle Dreiecke, die zur Kamera gerichtet sind, sind entgegen des Uhrzeigersinns definiert.
        glCullFace(GL_BACK); GLError.checkThrow() // Es werden alle Dreiecke verworfen, die nach hinten zeigen
        glEnable(GL_DEPTH_TEST); GLError.checkThrow()
        //glEnable(GL_FRAMEBUFFER_SRGB)
        glDepthFunc(GL_LESS); GLError.checkThrow()

        val stride = 8 * 4
        val attrPos = VertexAttribute(3, GL_FLOAT, stride, 0)
        val attrTC = VertexAttribute(2, GL_FLOAT, stride, 3 * 4)
        val attrNorm = VertexAttribute(3, GL_FLOAT, stride, 5 * 4)
        val vertexAttributesObjects = arrayOf(attrPos, attrTC, attrNorm)

        texDiff = Texture2D.invoke("assets/textures/ground_diff.png", true)
        texDiff.setTexParams(GL14.GL_REPEAT, GL14.GL_REPEAT, GL14.GL_LINEAR_MIPMAP_LINEAR, GL14.GL_LINEAR)
        texEmit = Texture2D.invoke("assets/textures/ground_emit.png", true)
        texEmit.setTexParams(GL14.GL_REPEAT, GL14.GL_REPEAT, GL14.GL_LINEAR_MIPMAP_LINEAR, GL14.GL_LINEAR)
        texSpec = Texture2D.invoke("assets/textures/ground_spec4.png", true)
        texSpec.setTexParams(GL14.GL_REPEAT, GL14.GL_REPEAT, GL14.GL_LINEAR_MIPMAP_LINEAR, GL14.GL_LINEAR)

        texDiffNew = Texture2D.invoke("assets/textures/ground_diff_new.png", true)
        texDiffNew.setTexParams(GL14.GL_REPEAT, GL14.GL_REPEAT, GL14.GL_LINEAR_MIPMAP_LINEAR, GL14.GL_LINEAR)
        texEmitNew = Texture2D.invoke("assets/textures/ground_emit_new.png", true)
        texEmitNew.setTexParams(GL14.GL_REPEAT, GL14.GL_REPEAT, GL14.GL_LINEAR_MIPMAP_LINEAR, GL14.GL_LINEAR)
        texSpecNew = Texture2D.invoke("assets/textures/ground_spec_new.png", true)
        texSpecNew.setTexParams(GL14.GL_REPEAT, GL14.GL_REPEAT, GL14.GL_LINEAR_MIPMAP_LINEAR, GL14.GL_LINEAR)


        texNormalMapNew = Texture2D.invoke("assets/textures/NormalMap.png", true)
        texNormalMapNew.setTexParams(GL14.GL_REPEAT, GL14.GL_REPEAT, GL14.GL_LINEAR_MIPMAP_LINEAR, GL14.GL_LINEAR)
        texEmptyNormalMap = Texture2D.invoke("assets/textures/emptyNormalMap.png", true)
        texEmptyNormalMap.setTexParams(GL14.GL_REPEAT, GL14.GL_REPEAT, GL14.GL_LINEAR_MIPMAP_LINEAR, GL14.GL_LINEAR)
        texObstacleNormalMap = Texture2D.invoke("assets/textures/obstacleNormalMap.png", true)
        texObstacleNormalMap.setTexParams(GL14.GL_REPEAT, GL14.GL_REPEAT, GL14.GL_LINEAR_MIPMAP_LINEAR, GL14.GL_LINEAR)

        texEmitLaser = Texture2D.invoke("assets/textures/laser.png", true)
        texEmitLaser.setTexParams(GL14.GL_REPEAT, GL14.GL_REPEAT, GL14.GL_LINEAR_MIPMAP_LINEAR, GL14.GL_LINEAR)

        //COURSE PARTS
        val matCourse = Material(texDiff, texEmit, texSpec, texNormalMapNew, 10.0f, Vector2f(32.0f, 32.0f))
        val matCourseNew = Material(texDiffNew, texEmit, texSpecNew, texNormalMapNew, 0.0f, Vector2f(16.0f, 16.0f))

        val dekolampObj = OBJLoader.loadOBJ("assets/models/deko/lamp.obj").objects[0].meshes[0]
        dekolamp1.meshes.add(Mesh(dekolampObj.vertexData, dekolampObj.indexData, vertexAttributesObjects, matCourseNew))
        dekolamp2.meshes.add(Mesh(dekolampObj.vertexData, dekolampObj.indexData, vertexAttributesObjects, matCourseNew))

        dekolamp1.parent = coursePart4
        dekolamp2.parent = coursePart8

        val courseObj = OBJLoader.loadOBJ("assets/models/Obstacle/course_part_4.obj").objects[0].meshes[0]
        coursePart1.meshes.add(Mesh(courseObj.vertexData, courseObj.indexData, vertexAttributesObjects, matCourseNew))
        coursePart2.meshes.add(Mesh(courseObj.vertexData, courseObj.indexData, vertexAttributesObjects, matCourseNew))
        coursePart2.translateGlobal(Vector3f(0f,0f,-2*5.5f))
        coursePart3.meshes.add(Mesh(courseObj.vertexData, courseObj.indexData, vertexAttributesObjects, matCourseNew))
        coursePart3.translateGlobal(Vector3f(0f,0f,-4*5.5f))
        coursePart4.meshes.add(Mesh(courseObj.vertexData, courseObj.indexData, vertexAttributesObjects, matCourseNew))
        coursePart4.translateGlobal(Vector3f(0f,0f,-6*5.5f))
        coursePart5.meshes.add(Mesh(courseObj.vertexData, courseObj.indexData, vertexAttributesObjects, matCourseNew))
        coursePart5.translateGlobal(Vector3f(0f,0f,-8*5.5f))
        coursePart6.meshes.add(Mesh(courseObj.vertexData, courseObj.indexData, vertexAttributesObjects, matCourseNew))
        coursePart6.translateGlobal(Vector3f(0f,0f,-10*5.5f))
        coursePart7.meshes.add(Mesh(courseObj.vertexData, courseObj.indexData, vertexAttributesObjects, matCourseNew))
        coursePart7.translateGlobal(Vector3f(0f,0f,-12*5.5f))
        coursePart8.meshes.add(Mesh(courseObj.vertexData, courseObj.indexData, vertexAttributesObjects, matCourseNew))
        coursePart8.translateGlobal(Vector3f(0f,0f,-14*5.5f))
        coursePart9.meshes.add(Mesh(courseObj.vertexData, courseObj.indexData, vertexAttributesObjects, matCourseNew))
        coursePart9.translateGlobal(Vector3f(0f,0f,-16*5.5f))
        coursePart10.meshes.add(Mesh(courseObj.vertexData, courseObj.indexData, vertexAttributesObjects, matCourseNew))
        coursePart10.translateGlobal(Vector3f(0f,0f,-18*5.5f))

        listOfFixObjects.add(coursePart1)
        listOfFixObjects.add(coursePart2)
        listOfFixObjects.add(coursePart3)
        listOfFixObjects.add(coursePart4)
        listOfFixObjects.add(coursePart5)
        listOfFixObjects.add(coursePart6)
        listOfFixObjects.add(coursePart7)
        listOfFixObjects.add(coursePart8)
        listOfFixObjects.add(coursePart9)
        listOfFixObjects.add(coursePart10)
        //

        //BOXEN
        val matBox = Material(texDiff, texEmit, texSpecNew, texObstacleNormalMap, 60.0f, Vector2f(16f, 16f))
        val matLaser = Material(texEmitLaser, texEmitLaser, texEmitLaser, texEmptyNormalMap, 10000f, Vector2f(1f,1f))
        val objBox =  OBJLoader.loadOBJ("assets/models/Obstacle/box_12.obj").objects[0].meshes[0]
        val objBox2 =  OBJLoader.loadOBJ("assets/models/Obstacle/tall_box_fence.obj").objects[0].meshes[0]
        val objBox2in =  OBJLoader.loadOBJ("assets/models/Obstacle/tall_box_fence_in.obj").objects[0].meshes[0]
        val objLaser = OBJLoader.loadOBJ("assets/models/Obstacle/laser.obj").objects[0].meshes[0]
        val objMovableLaser = OBJLoader.loadOBJ("assets/models/Obstacle/laserVertical.obj").objects[0].meshes[0]

        box1.meshes.add(Mesh(objBox.vertexData, objBox.indexData, vertexAttributesObjects, matBox))
        box1.translateGlobal(Vector3f(-2.25f,0f,-16 * 5.5f- (1..150).random()))
        tallBox1.meshes.add(Mesh(objBox2.vertexData, objBox2.indexData, vertexAttributesObjects, matBox))
        tallBox1.translateGlobal(Vector3f(-2.25f,0f,-16 * 5.5f -(1..150).random()))
        tallBox1in.meshes.add(Mesh(objBox2in.vertexData, objBox2in.indexData, vertexAttributesObjects, matLaser))
        tallBox1in.parent = tallBox1
        laser2.meshes.add(Mesh(objLaser.vertexData, objLaser.indexData, vertexAttributesObjects, matLaser))
        laser2.translateGlobal(Vector3f(-7.5f,0f,-16 * 5.5f - (1..150).random()))
        box2.meshes.add(Mesh(objBox.vertexData, objBox.indexData, vertexAttributesObjects, matBox))
        box2.translateGlobal(Vector3f(-6.75f-0.449f,0f,-16 * 5.5f - (1..150).random()))
        box3.meshes.add(Mesh(objBox.vertexData, objBox.indexData, vertexAttributesObjects, matBox))
        box3.translateGlobal(Vector3f(2.25f + 0.449f,0f,-16 * 5.5f - (1..150).random()))
        tallBox2.meshes.add(Mesh(objBox2.vertexData, objBox2.indexData, vertexAttributesObjects, matBox))
        tallBox2.translateGlobal(Vector3f(-6.75f-0.449f,0f,-16 * 5.5f - (1..150).random()))
        tallBox2in.meshes.add(Mesh(objBox2in.vertexData, objBox2in.indexData, vertexAttributesObjects, matLaser))
        tallBox2in.parent = tallBox2
        box4.meshes.add(Mesh(objBox.vertexData, objBox.indexData, vertexAttributesObjects, matBox))
        box4.translateGlobal(Vector3f(2.25f+0.449f,0f,-16 * 5.5f - (1..150).random()))
        movableLaser.meshes.add(Mesh(objMovableLaser.vertexData, objMovableLaser.indexData, vertexAttributesObjects, matLaser))
        movableLaser.translateGlobal(Vector3f(-7f,0f,-16 * 5.5f - (1..150).random()))
        box5.meshes.add(Mesh(objBox.vertexData, objBox.indexData, vertexAttributesObjects, matBox))
        box5.translateGlobal(Vector3f(-6.75f-0.449f,0f,-16 * 5.5f - (1..150).random()))
        laser.meshes.add(Mesh(objLaser.vertexData, objLaser.indexData, vertexAttributesObjects, matLaser))
        laser.translateGlobal(Vector3f(-7.5f,2f,-16 * 5.5f - (1..150).random()))
        box6.meshes.add(Mesh(objBox.vertexData, objBox.indexData, vertexAttributesObjects, matBox))
        box6.translateGlobal(Vector3f(-2.25f,0f,-16 * 5.5f - (1..150).random()))
        tallBox3.meshes.add(Mesh(objBox2.vertexData, objBox2.indexData, vertexAttributesObjects, matBox))
        tallBox3.translateGlobal(Vector3f(2.25f+0.449f,0f,-16 * 5.5f - (1..150).random()))
        tallBox3in.meshes.add(Mesh(objBox2in.vertexData, objBox2in.indexData, vertexAttributesObjects, matLaser))
        tallBox3in.parent = tallBox3

        val powerUpObj = OBJLoader.loadOBJ("assets/models/Obstacle/collectible.obj").objects[0].meshes[0]
        powerup.meshes.add(Mesh(powerUpObj.vertexData, powerUpObj.indexData, vertexAttributesObjects, matLaser))
        powerup.translateGlobal(Vector3f(-2.25f,0f,10f))
        powerup.scaleLocal(Vector3f(0.5f,0.4f,0.5f))

        boxList.add(box1)
        boxList.add(box2)
        boxList.add(box3)
        boxList.add(box4)
        boxList.add(box5)
        boxList.add(box6)

        tallBoxList.add(tallBox1)
        tallBoxList.add(tallBox2)
        tallBoxList.add(tallBox3)

        laserList.add(laser)
        laserList.add(laser2)

        listOfObstacles.add(box1)
        listOfObstacles.add(box2)
        listOfObstacles.add(box3)
        listOfObstacles.add(box4)
        listOfObstacles.add(box5)
        listOfObstacles.add(box6)
        listOfObstacles.add(tallBox1)
        listOfObstacles.add(tallBox2)
        listOfObstacles.add(tallBox3)
        /*
        listOfObstacles.add(laser)
        listOfObstacles.add(laser2)
         */
        listOfObstacles.add(movableLaser)

        bikeColBox?.parent = motorrad
        bikeColBox?.translateLocal(Vector3f(0.5f,0.25f,-1.5f))

        pointLight = PointLight(Vector3f(0.0f, 1f, 0.0f), Vector3f(2.0f, 0.0f, 1.0f), Vector3f(1.0f, 0.5f, 0.1f))
        pointLight.parent = motorrad

        spotLight = SpotLight(Vector3f(0f, 1f, 0f), Vector3f(2.0f, 2.0f, 2.0f), Vector3f(0.25f, 0.25f, 0.005f), 1f, 15f)
        spotLight.parent = motorrad
        spotLight.rotateLocal(Math.toRadians(-5f), 0f, 0f)

        tronCamera.rotateLocal(Math.toRadians(-43f), 0f, 0f)
        tronCamera.translateLocal(Vector3f(0f, 0.0f, 4.0f))
        tronCamera.parent = motorrad
    }

    fun render(dt: Float, t: Float) {
        staticShader.use()
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)

        pointLight.bind(staticShader, "PointLight")
        spotLight.bind(staticShader, "SpotLight", tronCamera.getCalculateViewMatrix())

        colorization = Vector3f(1f, 1f, 2f)
        staticShader.setUniform("colorization", colorization.x, colorization.y, colorization.z)

        fixObjectList.renderListOfObjects(staticShader)

        tronCamera.bind(staticShader)

        pointLight.col = colorization

        motorrad?.render(staticShader)
        dekolamp1.render(staticShader)
        dekolamp2.render(staticShader)
        colorization = Vector3f(7f, 2f, 2f)
        staticShader.setUniform("colorization", colorization.x, colorization.y, colorization.z)
        //obstaclePartController.respawnObstaclePart()
        obstacles.renderListOfObjects(staticShader)
        colorization = Vector3f(7f, 0f, 0f)
        staticShader.setUniform("colorization", colorization.x, colorization.y, colorization.z)
        obstaclesLasers.renderListOfObjects(staticShader)

        colorization = Vector3f(0.52f+Math.sin(t), 2.35f, 2.29f)
        staticShader.setUniform("colorization", colorization.x, colorization.y, colorization.z)
        tallBox1in.render(staticShader)
        tallBox2in.render(staticShader)
        tallBox3in.render(staticShader)

        colorization = Vector3f(0f, 1f, 0f)
        staticShader.setUniform("colorization", colorization.x, colorization.y, colorization.z)
        powerup.render(staticShader)
    }

    fun update(dt: Float, t: Float) {

        powerup.translateLocal(Vector3f(0f,0f+ Math.sin(t)/700,0f))
        if(cameratimer>50) {
            if (window.getKeyState(GLFW.GLFW_KEY_UP)) {
                cameraFront = true
                cameratimer = 0
            }
            if (window.getKeyState(GLFW.GLFW_KEY_DOWN)) {
                cameraBack = true
                cameratimer = 0
            }
            if (window.getKeyState(GLFW.GLFW_KEY_LEFT)) {
                cameraLeft = true
                cameratimer = 0
            }
            if (window.getKeyState(GLFW.GLFW_KEY_RIGHT)) {
               cameraRight = true
                cameratimer = 0
            }

        }

        if (cameraFront)
        {
            tronCamera.translateGlobal(Vector3f(0f, 0f, -0.04f))
            if (cameratimer>50)
            {
                cameraFront = false
            }
        }

        if (cameraBack)
        {
            tronCamera.translateGlobal(Vector3f(0f, 0f, 0.04f))
            if (cameratimer>50)
            {
                cameraBack = false
            }
        }

        if (cameraLeft)
        {
            tronCamera.translateGlobal(Vector3f(-0.01f, 0f, 0f))
            tronCamera.rotateLocal(Math.toRadians(0f), -Math.toRadians(0.04f), 0f)
            if (cameratimer>50)
            {
                cameraLeft = false
            }
        }

        if (cameraRight)
        {
            tronCamera.translateGlobal(Vector3f(0.01f, 0f, 0f))
            tronCamera.rotateLocal(Math.toRadians(0f), Math.toRadians(0.04f), 0f)
            if (cameratimer>50)
            {
                cameraRight = false
            }
        }

        cameratimer++

        if (colision && window.getKeyState(GLFW.GLFW_KEY_R))
        {
            box1.translateGlobal(Vector3f(0f,0f,-16 * 5.5f- (1..150).random()))
            tallBox1.translateGlobal(Vector3f(0f,0f,-16 * 5.5f -(1..150).random()))
            laser2.translateGlobal(Vector3f(0f,0f,-16 * 5.5f - 50))
            box2.translateGlobal(Vector3f(0f,0f,-16 * 5.5f - (1..150).random()))
            box3.translateGlobal(Vector3f(0f,0f,-16 * 5.5f - (1..150).random()))
            tallBox2.translateGlobal(Vector3f(0f,0f,-16 * 5.5f - (1..150).random()))
            box4.translateGlobal(Vector3f(0f,0f,-16 * 5.5f - (1..150).random()))
            movableLaser.translateGlobal(Vector3f(0f,0f,-16 * 5.5f - (1..150).random()))
            box5.translateGlobal(Vector3f(0f,0f,-16 * 5.5f - (1..150).random()))
            laser.translateGlobal(Vector3f(0f,0f,-16 * 5.5f - 100))
            box6.translateGlobal(Vector3f(0f,0f,-16 * 5.5f - (1..150).random()))
            tallBox3.translateGlobal(Vector3f(0f,0f,-16 * 5.5f - (1..150).random()))
            powerup.translateGlobal(Vector3f(0f,0f,-16 * 5.5f - (1..150).random()))
            colision = false
            speedMultiplier = 10.0
            gametime = 0
        }
        if (window.getKeyState(GLFW.GLFW_KEY_A)&&lineNumber!=0&&timer>50) {
            lineNumber--
            timer = 0

            if (!right)
            {
                left = true
                leftvector = 0.32f
            }
        }

        if (window.getKeyState(GLFW.GLFW_KEY_D)&&lineNumber!=2&&timer>50) {
            lineNumber++
            timer = 0
            if (!left)
            {
                right=true
                rightvector = 0.32f
            }
        }
        if (window.getKeyState(GLFW.GLFW_KEY_SPACE)&&!jump) {
            jump = true
            jumpvector = 0.07f
        }

        if (right){

            motorrad?.translateLocal(Vector3f(rightvector,0f,0f))
            rightvector -= 0.01f

            if (timer==40)
            {
                right = false
            }

        }

        if (left){
            motorrad?.translateLocal(Vector3f(-leftvector,0f,0f))
            leftvector -= 0.01f

            if (timer==40)
            {
                left = false
            }

        }

        if(jump)
        {
            if (speedMultiplier < 30)
            {
                motorrad?.translateGlobal(Vector3f(0f,0f+jumpvector,0f))
                jumpvector -= 0.001f
            }else{
                motorrad?.translateGlobal(Vector3f(0f,0f+jumpvector*1.5f,0f))
                jumpvector -= 0.001f*1.5f
            }
            if (motorrad?.getPosition()?.y!! <= 0f)
            {
                jump = false
                motorrad.translateGlobal(Vector3f(0f,0f-motorrad.getPosition().y,0f))
            }
        }

        when(gametime/100){
            in 10f..19f->{
                if (!colision) {
                    if (speedMultiplier<20)
                    {
                        speedMultiplier+= 0.1
                    }
                    println("SpeedUp1")
                }
            }
            in 25f..34f->{
                if (!colision) {
                    if (speedMultiplier<25)
                    {
                        speedMultiplier+= 0.1
                    }
                    println("SpeedUp2")
                }
            }
            in 35f..49f->{
                if (!colision) {
                    if (speedMultiplier<30)
                    {
                        speedMultiplier+= 0.1
                    }
                println("SpeedUp3")
                }
            }
            in 50f..59f->{
                if (!colision) {
                    if (speedMultiplier<40)
                    {
                        speedMultiplier+= 0.1
                    }
                    println("SpeedUp4")
                }
            }
        }

        fixObjectList.speedUpFixObject(speedMultiplier)
        fixObjectList.isFixObjectOnCam(floorChecker)

        obstacles.speedUpObstacles(speedMultiplier)
        obstacles.isObstacleOnCam(obstacleChecker)

        obstaclesLasers.speedUpObstacles(speedMultiplier)

        if ((!obstacleChecker.isObstacleOnCam(laser)||!obstacleChecker.isObstacleOnCam(laser2)))
        {
            if (laserCooldown<0) {
                if (obstacleChecker.respawnObject(laser)||obstacleChecker.respawnObject(laser2)) {
                    laserCooldown = 300
                }


            }
        }
        laserCooldown -= 1

        powerup.translateGlobal(Vector3f(0f,0f,(speedMultiplier * 0.01).toFloat()))
/*
        tallBox1in.translateGlobal(Vector3f(0f,0f,(speedMultiplier * 0.01).toFloat()))
        tallBox2in.translateGlobal(Vector3f(0f,0f,(speedMultiplier * 0.01).toFloat()))
        tallBox3in.translateGlobal(Vector3f(0f,0f,(speedMultiplier * 0.01).toFloat()))
 */
        obstacleChecker.respawnPowerUp(powerup)

        if(laserRight)
        {
            movableLaser.translateGlobal(Vector3f(-0.08f,0f,0f))
            if (movableLaser.getPosition().x < -7.2f)
            {
                laserRight = false
            }
        }else{
            movableLaser.translateGlobal(Vector3f(0.08f,0f,0f))
            if (movableLaser.getPosition().x > 5.2f)
            {
                laserRight = true
            }
        }
        //collision check
        if(collisionBox.checkCollision(bikeColBox, boxList, tallBoxList, laserList, movableLaser))
        {
            colision = true
            speedMultiplier = 0.0
        }

        //Power UP
        if(collisionBox.checkCollisionBikePowerUp(bikeColBox,powerup)&&powerUPidleTime<0)
        {
            powerupTimer = 1000
            powerUPidleTime = 400
            speedMultiplier *= 0.5f
            powerup.translateLocal(Vector3f(0f,0f,60f))
        }
        if (powerupTimer==0)
        {
            speedMultiplier *= 2
        }
        if (powerupTimer<0)
        {
            gametime++
        }else{
            powerupTimer--
        }
        powerUPidleTime--
        timer++
    }

    fun onKey(key: Int, scancode: Int, action: Int, mode: Int) {}

    fun onMouseMove(xpos: Double, ypos: Double) {
        //val diff = (mouseXPos - xpos).toFloat()
        //tronCamera.rotateAroundPoint(0f, Math.toRadians(diff * 0.002f), 0f, Vector3f(0f))
    }

    fun cleanup() {}

}

