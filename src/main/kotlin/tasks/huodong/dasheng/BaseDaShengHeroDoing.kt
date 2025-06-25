package tasks.huodong.dasheng

import data.HeroBean
import data.MRect
import getImage
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import log
import tasks.HeroDoing
import java.awt.Color
import java.awt.event.KeyEvent

abstract class BaseDaShengHeroDoing : HeroDoing(0, FLAG_GUANKA or FLAG_KEYEVENT), App.KeyListener {

    var guanka = 0

    var upHeros = arrayListOf<HeroBean>()



    override suspend fun doAfterHeroBeforeWaiting(heroBean: HeroBean) {
        if (guanka == 0 && isGuanKaOver()) {
            waiting = true
        }
    }

    override suspend fun afterHeroClick(heroBean: HeroBean) {
        super.afterHeroClick(heroBean)
    }



    fun isGuanKaOver(): Boolean {
        return when (guanka) {
            0 -> {
                upHeros.all {
                    !it.needCar || it.isFull()
                }
            }

            else -> false
        }
    }


    override suspend fun dealHero(heros: List<HeroBean?>): Int {
        if (guanka == 0 || guanka == 1) {
            //未开到第5个格子（战车已达到最高，工程位不会再变)先不上工程
            var index = defaultDealHero(heros, upHeros)
            return index
        }
        return -1
    }

    override fun changeHeroWhenNoSpace(heroBean: HeroBean): HeroBean? {
        return null
    }

    override fun onStart() {
        super.onStart()
        GlobalScope.launch {
            delay(56 * 1000)
            guanka = 1
//            startDianMingListener()
            waiting = false
        }
    }

    private fun startDianMingListener() {
        GlobalScope.launch {

            while (running) {

                var img = getImage(App.rectWindow)
                var found = false
                carDoing.carps.forEach {

                    if (it.hasHero()) {
                        var rect = MRect.createPointR(it.mRect.clickPoint, 18)
                        var wCount = rect.hasColorCount(Color.WHITE, 15, img)
                        var bCount = rect.hasColorCount(Color.BLACK, 15, img)
                        //参考值，200//超过200算被点名
                        if (wCount > 170 && bCount>170) {
                            log("startDianMingListener carpos:${it.mPos}  wcount:$wCount  bcount:$bCount")
                            log(getImage(rect))
                            carDoing.downHero(it.mHeroBean!!)
                            found = true
                        }

                    }
                }
                delay(300)
//                delay(if (found) 5000 else 300)
            }

        }
    }


    override fun onStop() {
        super.onStop()
    }


    private suspend fun onPosDown(position: Int) {
        carDoing.downPosition(position)
    }

    suspend fun doOnKeyDown(code: Int): Boolean {
        var downpos = when (code) {
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

        onPosDown(downpos)

        return true
    }

    override suspend fun onKeyDown(code: Int): Boolean {
        return doOnKeyDown(code)
    }
}