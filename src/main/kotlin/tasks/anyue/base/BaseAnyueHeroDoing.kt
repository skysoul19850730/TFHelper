package tasks.anyue.base

import data.HeroBean
import tasks.SimpleHeZuoHeroDoing
import java.awt.event.KeyEvent

abstract class BaseAnYueHeroDoing() : SimpleHeZuoHeroDoing() {

    //火灵攻击4次后，6秒后放无敌，4秒后解除；6秒时扔冰，两秒后再扔以后，共两个就可以等解除了，如果冰的等级高，第二个可以1.5秒后就扔

    override suspend fun onKeyDown(code: Int): Boolean {
        if (code == KeyEvent.VK_NUMPAD9) {//9强制改变waitting，防止waiting有逻辑错误不上卡
            waiting = !waiting
            return false
        }

        return false
    }

    override fun onGuanChange(guan: Int) {
        super.onGuanChange(guan)
//        if(guan in listOf(79,89,99)){
//            App.startAutoSave(200)
//        }else{
//            App.stopAutoSave()
//        }
    }

    fun add39() {

        An39(this).addToHeroDoing()
    }

    fun add69(qius69: List<HeroBean>){
        An69(this,qius69).addToHeroDoing()
    }

    fun add79(){
        Ay79(this).addToHeroDoing()
    }
    fun add89(){
        Ay89(this).addToHeroDoing()
    }
    fun add99(){
        Ay99(this).addToHeroDoing()
    }


    override fun onStart() {
        super.onStart()
    }

    override fun onStop() {
        super.onStop()
        App.stopAutoSave()
    }


}