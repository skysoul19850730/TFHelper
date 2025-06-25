package tasks.anyue.zhanjiang

import data.HeroBean
import getImage
import kotlinx.coroutines.*
import log
import logOnly
import saveTo
import tasks.HeroDoing
import java.awt.event.KeyEvent
import java.awt.image.BufferedImage
import java.io.File

abstract class BaseAnYueHeroDoing() : HeroDoing(0, FLAG_GUANKA or FLAG_KEYEVENT) {

    var beimu = false
    var beiDiJing = false

    override fun onGuanChange(guan: Int) {
        doOnGuanChanged(guan)
    }

    abstract fun doOnGuanChanged(guan: Int)


    var DownLoadPositionFromKeyFor19 = -1
    var DownLoadPositionFromKeyFor39 = -1


    override suspend fun onKeyDown(code: Int): Boolean {
        //如果龙王识别出错可以按快捷下对应卡牌，但不知道快捷键按下得时间，所以不能延时进行上卡，只能快捷键9来恢复上卡

        if (code == KeyEvent.VK_NUMPAD9) {//9强制改变waitting，防止waiting有逻辑错误不上卡
            waiting = !waiting
            return false
        }

        if (guankaTask?.currentGuanIndex == 19 || guankaTask?.currentGuanIndex == 18) {//19
            DownLoadPositionFromKeyFor19 = -1
            DownLoadPositionFromKeyFor19 = when (code) {
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
//        if (guankaTask?.currentGuanIndex == 39 || guankaTask?.currentGuanIndex == 38) {//19
//            DownLoadPositionFromKeyFor39 = -1
//            DownLoadPositionFromKeyFor39 = when (code) {
//                KeyEvent.VK_NUMPAD2 -> 0
//                KeyEvent.VK_NUMPAD1 -> 1
//                KeyEvent.VK_NUMPAD5 -> 2
//                KeyEvent.VK_NUMPAD4 -> 3
//                KeyEvent.VK_NUMPAD8 -> 4
//                KeyEvent.VK_NUMPAD7 -> 5
//                KeyEvent.VK_NUMPAD0 -> 6
//                KeyEvent.VK_NUMPAD3 -> 100
//                else -> {
//                    return false
//                }
//            }
//            GlobalScope.launch {
//                waiting = true//暂停上卡，开始下卡
//                carDoing.downPosition(DownLoadPositionFromKeyFor39)
//            }
//            return true
//        }
        return false
    }

    var g19Obeserver = false

    var time1 = 0L
    var time2 = 0L
    var time3 = 0L
    var mG19Job: Job? = null
    fun stop19Oberserver() {
        g19Obeserver = false
        mG19Job?.cancel()
        mG19Job = null
    }

    open fun onHeroPointByG19(hero: HeroBean): Boolean {
        return true
    }

    fun start19Oberserver(isNew: Boolean = false) {
        if (g19Obeserver) return
        g19Obeserver = true

        mG19Job = GlobalScope.launch {
            while (g19Obeserver) {
                //4 100 300 370
                var img = getImage(App.rectWindow, null)

                if (DownLoadPositionFromKeyFor19 > -1) {
                    log("g19 ：接收到快捷键事件")
                    if (DownLoadPositionFromKeyFor19 < 100) {//100代表对面车
                        var hero = carDoing.carps[DownLoadPositionFromKeyFor19].mHeroBean
                        if (hero != null && onHeroPointByG19(hero)) {
                            if (!isNew) {
                                waiting = true
                            }
                            carDoing.downHero(hero)
                            if (!isNew) {
                                preHero(null)//上地精
                            }
                            //检测到没有被选中为止
                            dijingIng = true

                            var canDownDijing = false
                            var overred = false
                            var canDownDijingDelay = false
//                                delay(500)//这里给个下卡时间，再检测有没有点名结束，否则如果正好下卡点出了卡片，这时检测红圈和蓝色方块来确定有没有结束点名就不正确了
                            while (!canDownDijing && !canDownDijingDelay) {
                                delay(50)
                                var img2 = getImage(App.rectWindow, null)
                                canDownDijing = carDoing.carps.get(DownLoadPositionFromKeyFor19).over19Selected(img2)
                                if (!canDownDijing && !overred) {
                                    var reding3 =
                                        carDoing.carps.get(DownLoadPositionFromKeyFor19).isAy19SelectedV2(img2)
                                    if (!reding3) {
                                        log("没有识别到19点名一次结束,检测到没有红圈了，1秒后下卡，1秒内检测到蓝色over就立马下")
                                        overred = true
                                        GlobalScope.launch {
                                            delay(1000)
                                            canDownDijingDelay = true
                                        }

                                    }
                                }
                            }
//                                delay(2500)
                            delay(500)
                            dijingIng = false
                            if (!isNew) {
                                waiting = false
                            }

                        }
                    }
                    onChuanZhangPoint(img)
                    DownLoadPositionFromKeyFor19 = -1
                } else {

                    var index = carDoing.getAY19Selected(img)
                    if (index > -1) {
                        var hero = carDoing.carps.get(index).mHeroBean
                        if (hero != null) {
                            if (onHeroPointByG19(hero)) {
                                log("检测到被标记  位置：$index  英雄：${hero?.heroName}")
                                if (!isNew) {
                                    waiting = true
                                }
                                carDoing.downHero(hero)
                                if (!isNew) {
                                    preHero(null)//上地精
                                }
                                dijingIng = true

                                var canDownDijing = false
                                var overred = false
                                var canDownDijingDelay = false
//                                delay(500)//这里给个下卡时间，再检测有没有点名结束，否则如果正好下卡点出了卡片，这时检测红圈和蓝色方块来确定有没有结束点名就不正确了
                                try {
                                    withTimeout(4000) {
                                        while (!canDownDijing && !canDownDijingDelay) {
                                            delay(50)
                                            var img2 = getImage(App.rectWindow, null)
                                            canDownDijing = carDoing.carps.get(index).over19Selected(img2)
                                            if (!canDownDijing && !overred) {
                                                var reding3 = carDoing.carps.get(index).isAy19SelectedV2(img2)
                                                if (!reding3) {
                                                    log("没有识别到19点名一次结束,检测到没有红圈了，1秒后下卡，1秒内检测到蓝色over就立马下")
                                                    overred = true
                                                    GlobalScope.launch {
                                                        delay(1000)
                                                        canDownDijingDelay = true
                                                    }

                                                }
                                            }
                                        }
                                    }
                                } catch (e: Exception) {
                                    log("没有识别到19点名一次结束,也未检测到红圈消失，超时后下卡继续")
                                }
//                                delay(2500)
                                dijingIng = false
                                if (!isNew) {
                                    waiting = false
                                }
                            } else {//比如点的战将，那么这里就延迟下，否则会一直循环的
                                delay(2200)
                            }

                        }
                        onChuanZhangPoint(img)
                        DownLoadPositionFromKeyFor19 = -1
                    }
                }
            }
        }
    }

    protected var dijingIng = false //true 上地精  false 下地精

    private suspend fun onChuanZhangPoint(img2: BufferedImage? = null) {
        var img = img2 ?: getImage(App.rectWindow)
        logOnly("船长点名啦")
        img.saveTo(File(App.caijiPath, "${System.currentTimeMillis()}.png"))//目前稳定，不用再采集了
    }


    override fun onStart() {
        super.onStart()
    }

    override fun onStop() {
        super.onStop()
        g19Obeserver = false
        App.stopAutoSave()
    }
}