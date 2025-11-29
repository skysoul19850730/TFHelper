package tasks.huodong.sanguo

import data.HeroBean
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import tasks.HeroDoing
import java.awt.event.KeyEvent

abstract class BaseSanguoHeroDoing : HeroDoing(0, FLAG_GUANKA or FLAG_KEYEVENT), App.KeyListener {

    var guanka = 0

    var upHeros = arrayListOf<HeroBean>()

    var mQiu:HeroBean?=null



    override suspend fun doAfterHeroBeforeWaiting(heroBean: HeroBean) {
        if (guanka == 0 && isGuanKaOver()) {
            waiting = true
        }else if(guanka == 1){
            waiting = false
        }
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

    var bingIng = true
    var lastBingTime = 0L

    override suspend fun dealHero(heros: List<HeroBean?>): Int {
        if (guanka == 0) {
            //未开到第5个格子（战车已达到最高，工程位不会再变)先不上工程
            var index = defaultDealHero(heros, upHeros)
            return index
        }else if(guanka == 1){
            var index = heros.indexOf(mQiu)
            if(index>-1){
                while(System.currentTimeMillis() - lastBingTime<2500L || !bingIng){
                    delay(100)
                }
                lastBingTime = System.currentTimeMillis()
                return index

            }
        }
        return -1
    }

    override suspend fun onKeyDown(code: Int): Boolean {
        if(code == KeyEvent.VK_NUMPAD0){
            bingIng = !bingIng
        }
        return super.onKeyDown(code)
    }

    override fun changeHeroWhenNoSpace(heroBean: HeroBean): HeroBean? {
        return null
    }

    override fun onStart() {
        super.onStart()
        if(mQiu!=null) {
            GlobalScope.launch {
                delay(55 * 1000)
                guanka = 1
                waiting = false
            }
        }
    }
}