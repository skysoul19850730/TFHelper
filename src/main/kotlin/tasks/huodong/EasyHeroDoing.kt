package tasks.huodong

import App
import data.Config
import data.HeroBean
import tasks.HeroDoing
import utils.MRobot
import java.awt.event.KeyEvent

class EasyHeroDoing : HeroDoing(0, FLAG_KEYEVENT), App.KeyListener {


    override fun initHeroes() {
        heros = arrayListOf<HeroBean>().apply {
            add(HeroBean("leishen", weightModel = 0))
            add(HeroBean("nvwang"))
            add(HeroBean("shahuang"))
            add(HeroBean("saman2"))
            add(HeroBean("jiaonv", weightModel = 0))
            add(HeroBean("daoke"))
            add(HeroBean("guangqiu", isGongCheng = true))
            add(HeroBean("dijing"))

            add(HeroBean("bingqi", needCar = false))
            add(HeroBean("xiaochou"))


        }
        carPosOffset = -50
    }


    override suspend fun afterHeroClick(heroBean: HeroBean) {
        super.afterHeroClick(heroBean)
    }

    override suspend fun dealHero(heros: List<HeroBean?>): Int {

        var nvwang = this.heros.find { it.heroName == "nvwang" }!!
        if (!nvwang.isFull()) {
            //女王没满，最多就上冰女和萨满，其他都不上，女王满了再上其他
            var index = heros.indexOf(nvwang)
            if (index > -1) {
                return index
            }
            index = heros.indexOfFirst { it?.heroName == "bingnv" }
            if (index > -1) {
                return index
            }
            index = heros.indexOfFirst { it?.heroName == "bawang" }
            if (index > -1) {
                return index
            }
        } else {
            var result: Int = -1
            this.heros.take(8).forEach {
                var index = heros.indexOf(it)
                if (index > -1) {
                    if (it.weightModel == 1) {
                        return index
                    } else if (it.weightModel == 0) {
                        if (!it.isInCar()) {
                            return index
                        }
                        if (result < 0) {
                            result = index
                        }
                    } else if (it.weightModel == 2) {
                        if (!it.isInCar()) {
                            return index
                        }
                        if (result < 0 && it.currentLevel < 3) {
                            result = index
                        }
                    }
                }


            }
            return result
        }

        return -1
    }

    override fun changeHeroWhenNoSpace(heroBean: HeroBean): HeroBean? {
        if (heroBean.heroName == "nvwang" || heroBean.heroName == "bawang") {//只女王和霸王有替换权
            var lastIndex = -1
            var lastHero: HeroBean? = null
            var ttIndex = this.heros.indexOf(heroBean)
            carDoing.carps.forEachIndexed { index, carPosition ->
                if (carPosition.hasHero()) {
                    var hero = carPosition.mHeroBean!!
                    var aaindex = this.heros.indexOf(hero)
                    if (aaindex > lastIndex && aaindex > ttIndex) {
                        lastIndex = aaindex
                        lastHero = hero
                    }
                }
            }
            return lastHero
        }
        return null
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onStop() {
        super.onStop()
    }

    suspend fun doOnKeyDown(code: Int): Boolean {
        var downPos = when (code) {
            KeyEvent.VK_NUMPAD2 -> 0
            KeyEvent.VK_NUMPAD1 -> 1
            KeyEvent.VK_NUMPAD5 -> 2
            KeyEvent.VK_NUMPAD4 -> 3
            KeyEvent.VK_NUMPAD8 -> 4
            KeyEvent.VK_NUMPAD7 -> 5
            KeyEvent.VK_NUMPAD0 -> 6
            KeyEvent.VK_NUMPAD9 -> {
                MRobot.singleClickPc(Config.zhandou_hero1CheckRect.clickPoint)
                100
            }

            KeyEvent.VK_NUMPAD6 -> {
                MRobot.singleClickPc(Config.zhandou_hero2CheckRect.clickPoint)
                101
            }

            KeyEvent.VK_NUMPAD3 -> {
                MRobot.singleClickPc(Config.zhandou_hero3CheckRect.clickPoint)
                102
            }
            else -> {
                -1
            }
        }

        if (downPos > -1 && downPos < 100) {
            carDoing.downPosition(downPos)
            return true
        }

        if (downPos >= 100) {
            return true
        }
        return false
    }

    override suspend fun onKeyDown(code: Int): Boolean {
        return doOnKeyDown(code)
    }

}