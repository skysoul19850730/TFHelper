package tasks.huodong

import App
import data.HeroBean
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import log
import tasks.HeroDoing
import java.awt.event.KeyEvent

class HuodongHeroDoing : HeroDoing(0, FLAG_GUANKA or FLAG_KEYEVENT), App.KeyListener {

    var guanka = 0

    lateinit var muqiu: HeroBean
    lateinit var hunqiu: HeroBean



    override fun initHeroes() {
        heros = arrayListOf<HeroBean>().apply {
            add(HeroBean("houyi"))
            add(HeroBean("hugong"))

            add(HeroBean("xiaochou"))
            add(HeroBean("niutou"))
            add(HeroBean("gugu"))
            add(HeroBean("dianfa"))
            add(HeroBean("dapao", isGongCheng = true))
            add(HeroBean("guangqiu", needCar = false))
            add(HeroBean("muqiu", needCar = false))
            add(HeroBean("leiqiu", needCar = false))
        }
    }

    override suspend fun doAfterHeroBeforeWaiting(heroBean: HeroBean) {
        if (guanka==0 && isGuanKaOver()) {
            guanka=2
        }
    }

    override suspend fun afterHeroClick(heroBean: HeroBean) {
        super.afterHeroClick(heroBean)
    }

    fun isGuanKaOver(): Boolean {
        return when (guanka) {
            0 -> {
                heros.filter {
                    it.needCar || it.isGongCheng
                }.all {
                    it.isFull()
                }
            }

            else -> false
        }
    }


    override suspend fun dealHero(heros: List<HeroBean?>): Int {
        if (guanka == 0) {
            //未开到第5个格子（战车已达到最高，工程位不会再变)先不上工程
            var sortHeros = if (carDoing.openCount() > 4) this.heros
            else this.heros.filter {
                !it.isGongCheng
            }
            var index = defaultDealHero(heros, sortHeros)
            return index
        } else if (guanka == 1) {
            //第二阶段不用光球了，来了就补，不来就补血
            return defaultDealHero(heros, this.heros.filter {
                it.heroName != "guangqiu"
            })
        }else if(guanka==2){//上满后扔雷节省时间
            return heros.indexOfFirst {
                it?.heroName == "leiqiu"
            }
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
            waiting = false
        }
    }

    override fun onStop() {
        super.onStop()
    }

    suspend fun doOnKeyDown(code: Int): Boolean {
        var handle = true
        if (code == KeyEvent.VK_NUMPAD1) {
            guanka = 1
            waiting = false
        } else {
            handle = false
        }

        if (handle) {
            log("code $code is Down")
        }

        return handle
    }

    override suspend fun onKeyDown(code: Int): Boolean {
        return doOnKeyDown(code)
    }

}