package tasks.anyue.base

import data.HeroBean
import getImage
import kotlinx.coroutines.delay
import org.apache.commons.compress.harmony.pack200.PackingUtils.log
import tasks.XueLiang
import utils.AYUtil
import java.awt.event.KeyEvent
import java.awt.image.BufferedImage
import kotlin.math.abs

class An39(val heroDoing: BaseAnYueHeroDoing) : AnSub {

    var DownLoadPositionFromKeyFor39 = -1


    var status = 2
    var lastXue = 1f

    var heros39Up4 = listOf<HeroBean>()

    override fun addToHeroDoing() {
        heroDoing.apply {
            addGuanDeal(39) {
                over {//这样以后可以处理 结束后上回去的写法，
                    curGuan > 39 && fulls(*heros39Up4.toTypedArray())
                }
                chooseHero {
                    if(curGuan==39) {
                        deal39(this)
                    }else{
                        upAny(heros39Up4)
                    }
                }
                onStart {
                    heros39Up4 = carDoing.carps.filter {
                        it.mPos in 2..5
                    }.map { it.mHeroBean!! }
                }
            }
        }
    }

    private suspend fun BaseAnYueHeroDoing.deal39(heros: List<HeroBean?>): Int {
        //车满的时候就等血量变低就上卡，都可以上。
        //上卡过程中，点了卡，血量没变时，只能上车上的卡，血量变了就可以上所有的卡
        if (curGuan > 39) {
            return -1
        }
        freshstatus()
        if (status == 2) {//刚掉完血，啥都可以上，下次会再次刷39status
            if (carDoing.hasNotFull() || carDoing.hasOpenSpace()) {
                return defaultDealHero(heros, heros39Up4)
            } else {//都满了就等点名（第一次也走这里，因为status默认赋值为2）
                while (status != 1 && curGuan < 40) {
                    delay(100)
                    freshstatus()
                }
                //这是status = 1 了,递归回去，再进入后走 =1 逻辑，上车上的卡，并继续监听点名修复。车上满了或血量变了再变 2 回来
                return deal39(heros)
            }
        } else if (status == 1) {
            if (carDoing.hasNotFull()) {//只能上车上的
                var listtt = arrayListOf<HeroBean>()
                listtt.addAll(carDoing.carps.filter {
                    it.mHeroBean != null && !it.mHeroBean!!.isFull()
                }.map { it.mHeroBean!! })
                return defaultDealHero(heros, listtt)
            } else {//如果车上都满了，监听掉血,过程中也在继续监听下卡，监听到就下，反正多下了，也比少下了强
                while (status != 2 && curGuan < 40) {
                    delay(100)
                    freshstatus()
                }
                //这时status = 2 了，递归回去啥都上
                return deal39(heros)
            }
        }
        return -1
    }


    private suspend fun BaseAnYueHeroDoing.freshStatus39() {
        var img = getImage(App.rectWindow);
        var xue = XueLiang.getXueLiang(img)


        val pos = AYUtil.getAy39SelectedPositions(carDoing.chePosition, img)
        if (pos.isNotEmpty()) {//点名，
            down39(img, pos)
        }
        if (abs(xue - lastXue) > 0.03) {//上卡过程中撞过了，没撞过就不用继续识别
            log("血量发生变化，所有卡都上")
            delay(200)
            status = 2
        }
    }

    private suspend fun BaseAnYueHeroDoing.down39(img2: BufferedImage?, pos: List<Int>) {
        var img = img2 ?: getImage(App.rectWindow);
        lastXue = XueLiang.getXueLiang(img)//记录点名时的血量
        pos.forEach {
            carDoing.downPosition(it)
        }
        status = 1
        log("监听到点名了，只有车上的可以上")
    }


    private suspend fun BaseAnYueHeroDoing.freshstatus() {
        var img = getImage(App.rectWindow);
        var xue = XueLiang.getXueLiang(img)


        val pos = AYUtil.getAy39SelectedPositions(carDoing.chePosition, img)
        if (pos.isNotEmpty()) {//点名，
            down39(img, pos)
        }
        if (abs(xue - lastXue) > 0.03) {//上卡过程中撞过了，没撞过就不用继续识别
            log("血量发生变化，所有卡都上")
            delay(200)
            status = 2
        }
    }


    suspend fun onKeyDown(code: Int): Boolean {
        heroDoing.apply {
            if (curGuan == 39) {
                DownLoadPositionFromKeyFor39 = -1
                DownLoadPositionFromKeyFor39 = when (code) {
                    KeyEvent.VK_NUMPAD2 -> 0
                    KeyEvent.VK_NUMPAD1 -> 1
                    KeyEvent.VK_NUMPAD5 -> 2
                    KeyEvent.VK_NUMPAD4 -> 3
                    KeyEvent.VK_NUMPAD8 -> 4
                    KeyEvent.VK_NUMPAD7 -> 5
                    KeyEvent.VK_NUMPAD0 -> 6
                    else -> {
                        return false
                    }
                }
                down39(null, arrayListOf(DownLoadPositionFromKeyFor39))
                return true
            }

        }
        return false
    }
}