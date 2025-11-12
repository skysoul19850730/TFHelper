package tasks.daxuanwo

import data.Config
import data.HeroBean
import data.MPoint
import data.MRect
import getImage
import getImageFromRes
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import log
import logOnly
import saveTo
import tasks.HeroDoing
import tasks.SimpleHeZuoHeroDoing
import tasks.XueLiang
import ui.zhandou.UIKeyListenerManager
import utils.HBUtil
import utils.ImgUtil
import utils.ImgUtil.forEach4Result
import utils.MRobot
import java.awt.event.KeyEvent
import java.awt.image.BufferedImage
import java.io.File

abstract class BaseSimpleXWHeroDoing() : SimpleHeZuoHeroDoing(), UIKeyListenerManager.UIKeyListener {


    override suspend fun onKeyDown(code: Int): Boolean {
        //如果龙王识别出错可以按快捷下对应卡牌，但不知道快捷键按下得时间，所以不能延时进行上卡，只能快捷键9来恢复上卡

        if (code == KeyEvent.VK_NUMPAD9) {//9强制改变waitting，防止waiting有逻辑错误不上卡
            waiting = !waiting
            return true
        }

        if (code == KeyEvent.VK_NUMPAD0) {
            if (curGuan == 9){
                GlobalScope.launch {
                    MRobot.moveFullScreen()
                }
                return true
            }

        }
        return false


    }

    override fun onGuanChange(guan: Int) {
        super.onGuanChange(guan)

        if(guan == 9){
            GlobalScope.launch {
                delay(1000)
                MRobot.moveFullScreen()
            }
        }

//        if(guan%10==9){
//            App.startAutoSave(200)
//        }else{
//            App.stopAutoSave()
//        }
    }


    override fun onGuanFix(guan: Int) {
        guankaTask?.setCurGuanIndex(guan)
    }

    override fun onLongWangZsClick() {
    }

    override fun onLongWangFsClick() {
    }

    override fun onKey3Down() {
    }

    override fun onWaitingClick() {
        waiting = !waiting
    }


    override fun onStart() {
        super.onStart()
        UIKeyListenerManager.addKeyListener(this)
    }

    override fun onStop() {
        super.onStop()
        UIKeyListenerManager.removeKeyListener(this)
        App.stopAutoSave()
    }


    var job29:Job?=null
    fun start29(){
        var subFoler = "${Config.platName}/xuanwo/"
        val daxia = getImageFromRes("${subFoler}xw_daxia.png")
        val xw_pangxie = getImageFromRes("${subFoler}xw_pangxie.png")
        val xw_shaoji = getImageFromRes("${subFoler}xw_shaoji.png")
        val xw_shaoyu = getImageFromRes("${subFoler}xw_shaoyu.png")
        val xw_zhutou = getImageFromRes("${subFoler}xw_zhutou.png")

        job29?.cancel()
        job29 = GlobalScope.launch {
            while(curGuan==29){
                val img = getImage(MRect.createWH(675,210,54,30))
                val sim = 0.95
              val point =   if(ImgUtil.isImageSim(img,daxia,sim)){
                    MPoint(650,300)
                }else  if(ImgUtil.isImageSim(img,xw_pangxie,sim)){
                  MPoint(410,310)
              }else if(ImgUtil.isImageSim(img,xw_shaoji,sim)){
                  MPoint(470,410)
              }else if(ImgUtil.isImageSim(img,xw_shaoyu,sim)){
                  MPoint(580,410)
              }else if(ImgUtil.isImageSim(img,xw_zhutou,sim)){
                  MPoint(530,270)
              }else{
                  null
              }
                if(point!=null){
                    point.clickPc()
                    delay(5000)
                }else{
                    delay(100)
                }
            }
        }
    }
    fun stop29(){
        job29?.cancel()
    }
}