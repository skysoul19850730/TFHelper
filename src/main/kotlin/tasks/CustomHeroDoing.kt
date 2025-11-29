package tasks

import data.Config
import data.HeroBean
import data.MPoint
import utils.MRobot
import java.awt.event.KeyEvent

class CustomHeroDoing(chePos: Int) : HeroDoing(chePos, FLAG_GUANKA or FLAG_KEYEVENT) {
    override fun initHeroes() {
    }

    override suspend fun dealHero(heros: List<HeroBean?>): Int {
        return -1
    }

    override fun changeHeroWhenNoSpace(heroBean: HeroBean): HeroBean? {
        return null
    }


    override fun start() {
        if (running) return
        running = true
        onStart()
    }


    private suspend fun upHero(position: Int) {

        var rect = when (position) {
            0 -> Config.zhandou_hero1CheckRect
            1 -> Config.zhandou_hero2CheckRect
            else -> Config.zhandou_hero3CheckRect
        }
        MRobot.singleClick(MPoint(rect.clickPoint.x, rect.clickPoint.y + 25))
    }

    private suspend fun shuaxin() {
        MRobot.singleClick(Config.zhandou_shuaxinPoint)
    }

    private suspend fun kuojian() {
        MRobot.singleClick(Config.zhandou_kuojianPoint)
    }

    override suspend fun isKeyDownNeed(code: Int): Boolean {
        return true
    }

    override suspend fun onKeyDown(code: Int): Boolean {

        when (code) {
            KeyEvent.VK_NUMPAD4 ->
                upHero(0)

            KeyEvent.VK_NUMPAD5 ->
                upHero(1)

            KeyEvent.VK_NUMPAD6 ->
                upHero(2)

            KeyEvent.VK_NUMPAD0 ->
                shuaxin()

            KeyEvent.VK_NUMPAD8 ->
                kuojian()

            KeyEvent.VK_NUMPAD3 -> {
                carDoing.chePosition = 1
                carDoing.initPositions()
            }

            KeyEvent.VK_NUMPAD1 -> {
                carDoing.chePosition = 0
                carDoing.initPositions()
            }

        }


        return super.onKeyDown(code)
    }

    override suspend fun onKeyDoubleDown(code: Int) {
        super.onKeyDoubleDown(code)
        val downPs = when (code) {
            KeyEvent.VK_NUMPAD2 -> 0
            KeyEvent.VK_NUMPAD1 -> 1
            KeyEvent.VK_NUMPAD5 -> 2
            KeyEvent.VK_NUMPAD4 -> 3
            KeyEvent.VK_NUMPAD8 -> 4
            KeyEvent.VK_NUMPAD7 -> 5
            KeyEvent.VK_NUMPAD0 -> 6
            else -> {
                -1
            }
        }
        if (downPs > -1) {
            carDoing.downPosition(downPs, true)
        }
    }

}