package tasks.anyue.base

import data.HeroBean
import tasks.SimpleHeZuoHeroDoing
import java.awt.event.KeyEvent

abstract class BaseAnYueHeroDoing() : SimpleHeZuoHeroDoing() {

    override suspend fun onKeyDown(code: Int): Boolean {
        if (code == KeyEvent.VK_NUMPAD9) {//9强制改变waitting，防止waiting有逻辑错误不上卡
            waiting = !waiting
            return false
        }

        return false
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