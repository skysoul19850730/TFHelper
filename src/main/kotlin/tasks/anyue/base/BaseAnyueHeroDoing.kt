package tasks.anyue.base

import MainData.job79TimeDt
import data.HeroBean
import getImage
import kotlinx.coroutines.*
import log
import logOnly
import saveTo
import tasks.HeroDoing
import tasks.SimpleHeZuoHeroDoing
import tasks.XueLiang
import ui.zhandou.UIKeyListenerManager
import utils.AYUtil
import java.awt.event.KeyEvent
import java.awt.image.BufferedImage
import java.io.File
import kotlin.math.abs

abstract class BaseAnYueHeroDoing() : SimpleHeZuoHeroDoing() {

    //69可以扔的球
    private var qiu69 = arrayListOf<HeroBean>()

    override suspend fun onKeyDown(code: Int): Boolean {
        if (code == KeyEvent.VK_NUMPAD9) {//9强制改变waitting，防止waiting有逻辑错误不上卡
            waiting = !waiting
            return false
        }

        return false
    }


    fun add39(heros39Up4: List<HeroBean>) {
        An39(this,heros39Up4).addToHeroDoing()
    }

    fun add69(qius69: List<HeroBean>){
        qiu69.addAll(qius69)
        addGuanDeal(69){
            ov
        }

    }


    override fun onStart() {
        super.onStart()
    }

    override fun onStop() {
        super.onStop()
        App.stopAutoSave()
    }


}