package cga.exercise.components.Collision

import cga.exercise.components.geometry.Renderable
import cga.exercise.components.geometry.RenderableList
import cga.exercise.components.geometry.Transformable
import cga.framework.OBJLoader

class Collision {

    fun checkCollision(t1: Transformable, box: ArrayList<Renderable>, tallBox: ArrayList<Renderable>, laser: ArrayList<Renderable>, vertLaser: Renderable): Boolean{
        return checkCollisionBikeBox(t1, box) || checkCollisionBikeTallBox(t1, tallBox) || checkCollisionBikeLaser(t1, laser) || checkCollisionBikeVerticalLaser(t1, vertLaser)
    }

    fun checkCollisionBikeBox(t1: Transformable, tList: ArrayList<Renderable>): Boolean {
        tList.forEach {
            if (t1.getWorldPosition().z + 3f >= it.getWorldPosition().z && it.getWorldPosition().z + 1f >= t1.getWorldPosition().z){
                if (t1.getWorldPosition().x + 1f >= it.getWorldPosition().x && it.getWorldPosition().x + 4.5f >= t1.getWorldPosition().x){
                    if (t1.getWorldPosition().y + 1f >= it.getWorldPosition().y && it.getWorldPosition().y + 1.5f >= t1.getWorldPosition().y) {
                        return true
                    }
                }
            }
        }
        return false
    }

    fun checkCollisionBikeTallBox(t1: Transformable, tList: ArrayList<Renderable>): Boolean {
        tList.forEach {
            if (t1.getWorldPosition().z + 3f >= it.getWorldPosition().z && it.getWorldPosition().z + 1f >= t1.getWorldPosition().z){
                if (t1.getWorldPosition().x + 1f >= it.getWorldPosition().x && it.getWorldPosition().x + 4.5f >= t1.getWorldPosition().x){
                    if (t1.getWorldPosition().y + 1f >= it.getWorldPosition().y && it.getWorldPosition().y + 3f >= t1.getWorldPosition().y) {
                        return true
                    }
                }
            }
        }
        return false
    }

    fun checkCollisionBikeLaser(t1: Transformable, tList: ArrayList<Renderable>): Boolean {
        tList.forEach {
            if (t1.getWorldPosition().z + 3f >= it.getWorldPosition().z && it.getWorldPosition().z + 0.3f >= t1.getWorldPosition().z) {
                if (t1.getWorldPosition().x + 1f >= it.getWorldPosition().x && it.getWorldPosition().x + 15f >= t1.getWorldPosition().x) {
                    if (t1.getWorldPosition().y + 1f >= it.getWorldPosition().y && it.getWorldPosition().y + 2f >= t1.getWorldPosition().y) {
                        return true
                    }
                }
            }
        }
        return false
    }

    fun checkCollisionBikeVerticalLaser(t1: Transformable, tObj: Renderable): Boolean {
        if (t1.getWorldPosition().z + 3f >= tObj.getWorldPosition().z && tObj.getWorldPosition().z + 0.3f >= t1.getWorldPosition().z){
            if (t1.getWorldPosition().x + 1f >= tObj.getWorldPosition().x && tObj.getWorldPosition().x + 2f >= t1.getWorldPosition().x){
                if (t1.getWorldPosition().y + 1f >= tObj.getWorldPosition().y && tObj.getWorldPosition().y + 15f >= t1.getWorldPosition().y) {
                    return true
                }
            }
        }
        return false
    }

    fun checkCollisionBikePowerUp(t1: Transformable, tObj: Renderable): Boolean {
        if (t1.getWorldPosition().z + 3f >= tObj.getWorldPosition().z && tObj.getWorldPosition().z + 1f >= t1.getWorldPosition().z){
            if (t1.getWorldPosition().x + 1f >= tObj.getWorldPosition().x && tObj.getWorldPosition().x + 2f >= t1.getWorldPosition().x){
                if (t1.getWorldPosition().y + 1f >= tObj.getWorldPosition().y && tObj.getWorldPosition().y + 1.5f >= t1.getWorldPosition().y) {
                    return true
                }
            }
        }
        return false
    }
}