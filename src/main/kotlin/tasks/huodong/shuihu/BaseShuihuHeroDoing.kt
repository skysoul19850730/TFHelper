package tasks.huodong.shuihu

import data.HeroBean
import data.MRect
import getImage
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import log
import tasks.HeroDoing
import tasks.XueLiang
import java.awt.Color
import java.awt.event.KeyEvent

abstract class BaseShuihuHeroDoing : HeroDoing(0, FLAG_GUANKA or FLAG_KEYEVENT), App.KeyListener {

    var guanka = 0

    var upHeros = arrayListOf<HeroBean>()

    var mQiu:HeroBean?=null



    override suspend fun doAfterHeroBeforeWaiting(heroBean: HeroBean) {
        if (guanka == 0 && isGuanKaOver()) {
            waiting = true
        }else if(guanka == 1){
//            if(XueLiang.getBossXueliang() == 1f){
//                waiting = true
//            }
            waiting = false
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
        if (guanka == 0) {
            //未开到第5个格子（战车已达到最高，工程位不会再变)先不上工程
            var index = defaultDealHero(heros, upHeros)
            return index
        }else if(guanka == 1){
            return heros.indexOf(mQiu)
        }
        return -1
    }

    override fun changeHeroWhenNoSpace(heroBean: HeroBean): HeroBean? {
        return null
    }

    override fun onStart() {
        super.onStart()
        if(mQiu!=null) {
            GlobalScope.launch {
                delay(60 * 1000)
                guanka = 1
                waiting = false
            }
        }
    }
}