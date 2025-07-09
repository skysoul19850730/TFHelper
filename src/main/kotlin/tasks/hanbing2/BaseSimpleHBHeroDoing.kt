package tasks.hanbing2

import data.Config
import data.HeroBean
import data.MPoint
import data.MRect
import getImage
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import log
import logOnly
import tasks.HeroDoing
import tasks.SimpleHeZuoHeroDoing
import ui.zhandou.UIKeyListenerManager
import utils.ImgUtil.forEach4Result
import java.awt.event.KeyEvent
import java.awt.image.BufferedImage

abstract class BaseSimpleHBHeroDoing() : SimpleHeZuoHeroDoing(), UIKeyListenerManager.UIKeyListener {

    var beimu = false

    var heroDoNotDown129:HeroBean?=null


    var chuanzhangDownLoadPositionFromKey = -1
    var longWangDownLoadPositionFromKey = -1

    var position199 = -1

    override fun onChuanzhangClick(position: Int) {
        chuanzhangDownLoadPositionFromKey = position
    }

    override suspend fun onKeyDown(code: Int): Boolean {
        //如果龙王识别出错可以按快捷下对应卡牌，但不知道快捷键按下得时间，所以不能延时进行上卡，只能快捷键9来恢复上卡

        if (code == KeyEvent.VK_NUMPAD9) {//9强制改变waitting，防止waiting有逻辑错误不上卡
            waiting = !waiting
            return true
        }

        if (guankaTask?.currentGuanIndex == 129 ||guankaTask?.currentGuanIndex == 128 ||guankaTask?.currentGuanIndex == 127 ||
            guankaTask?.currentGuanIndex == 99||guankaTask?.currentGuanIndex == 98 ||guankaTask?.currentGuanIndex == 97) {//船长
            chuanzhangDownLoadPositionFromKey = -1
            chuanzhangDownLoadPositionFromKey = when (code) {
                KeyEvent.VK_NUMPAD2 -> 0
                KeyEvent.VK_NUMPAD1 -> 1
                KeyEvent.VK_NUMPAD5 -> 2
                KeyEvent.VK_NUMPAD4 -> 3
                KeyEvent.VK_NUMPAD8 -> 4
                KeyEvent.VK_NUMPAD7 -> 5
                KeyEvent.VK_NUMPAD0 -> 6
                KeyEvent.VK_NUMPAD3 -> 100
                else -> {
                    return false
                }
            }
            return true
        }


        if (guankaTask?.currentGuanIndex == 199 ||guankaTask?.currentGuanIndex == 198 ) {
            position199 = when (code) {
                KeyEvent.VK_NUMPAD2 -> 0
                KeyEvent.VK_NUMPAD1 -> 1
                KeyEvent.VK_NUMPAD5 -> 2
                KeyEvent.VK_NUMPAD4 -> 3
                KeyEvent.VK_NUMPAD8 -> 4
                KeyEvent.VK_NUMPAD7 -> 5
                KeyEvent.VK_NUMPAD0 -> 6
                else -> -1
            }

            if(position199>-1){
                return true
            }
        }


        return false
    }

    var chuanZhangObeserver = false
    var chuanzhangDownCount = 0


    open fun onHeroPointByChuanzhang(hero: HeroBean): Boolean {
        return hero!=heroDoNotDown129
    }

    var mChuanZhangJob:Job?=null
    fun stopChuanZhangOberserver(){
        chuanZhangObeserver= false
        mChuanZhangJob?.cancel()
        mChuanZhangJob = null
    }
    fun startChuanZhangOberserver() {
        if (chuanZhangObeserver) return
        chuanZhangObeserver = true

        mChuanZhangJob = GlobalScope.launch {
            while (chuanZhangObeserver) {
                //4 100 300 370


                var img = getImage(App.rectWindow, null)

                if (chuanzhangDownLoadPositionFromKey > -1) {
                    log("船长 ：接收到快捷键事件")
                    if (chuanzhangDownLoadPositionFromKey < 100) {//100代表对面车
                        var hero = carDoing.carps[chuanzhangDownLoadPositionFromKey].mHeroBean
                        if (hero != null && onHeroPointByChuanzhang(hero)) {
                            carDoing.downHero(hero)
                            waiting = false
                        }
                    }
                    onChuanZhangPoint(img)
                    chuanzhangDownLoadPositionFromKey = -1
                } else {

                    var index = carDoing.getChuanZhangMax(img)
                    var index2 = otherCarDoing.getChuanZhangMax(img)
//                    var index2:Pair<Int, Float>? = null
                    if (index != null || index2 != null) {
                        if (index != null && (index2 == null || index.second > index2.second)) {
                            var hero = carDoing.carps.get(index.first).mHeroBean
                            log("检测到被标记  位置：$index  英雄：${hero?.heroName}")
                            if (hero != null && onHeroPointByChuanzhang(hero)) {
                                carDoing.downHero(hero)
                                waiting = false
                            }
                        }
                        onChuanZhangPoint(img)
                    } else {
                        var fitCount = 0
                        MRect.createWH(4, 100, 300, 370).forEach4Result { x, y ->
                            if (img.getRGB(x, y) == Config.Color_ChuangZhang.rgb) {
                                fitCount++
                            }
                            fitCount > 1000
                        }
                        if (fitCount > 1000) {
                            onChuanZhangPoint(img)
                        }
                    }
                }
            }
        }
    }
    suspend fun onChuanZhangPoint(img2: BufferedImage? = null) {
//        var img = img2 ?: getImage(App.rectWindow)
        logOnly("船长点名啦")
//        img.saveTo(File(App.caijiPath, "${System.currentTimeMillis()}.png"))//目前稳定，不用再采集了
        chuanzhangDownCount++
        var isSencodDianming = chuanzhangDownCount % 2 == 0
        if (!isSencodDianming) {//第一次点卡后等3秒再开始识别
            log("第一次点名,3秒后再开始监听")
            delay(3000)
        } else {//第二次点卡后 刷6秒补卡然后停止（这个时间慢慢校验)要撞船了
            if (chuanZhangObeserver) {
//                            delay(10000)
                log("第二次点名,刷卡7.5秒后，然后暂停刷卡")
                beimu = true
                dropThisDeal = true//重新刷新，不用之前的卡（可能被手动动过，比如爱神那里上下圣骑）
                waiting = false
                delay(7500)
                waiting = true
                log("第二次点名,已经刷卡7.5秒，暂停刷卡，10.5秒开启监听")
                GlobalScope.launch {
                    delay(13500)
                    beimu = false //waiting前，把beimu改为false
//                    waiting = false
                    log("第二次点名,暂停刷卡13.5秒后恢复刷卡")
                }
                delay(10500)//5秒后 效果消失，继续补卡，并监听点名
                log("第二次点名,延迟10.5秒,恢复监听")
//                waiting = false
            }
        }
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
        chuanZhangObeserver = false
        App.stopAutoSave()
    }
}