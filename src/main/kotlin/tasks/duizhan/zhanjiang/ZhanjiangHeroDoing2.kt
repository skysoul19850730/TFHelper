package tasks.duizhan.zhanjiang

import data.*
import kotlinx.coroutines.delay
import log
import tasks.HeroDoing
import tasks.XueLiang
import ui.zhandou.hanbing.HanBingModel
import utils.MRobot
import java.awt.event.KeyEvent
import kotlin.math.abs
import kotlin.math.max

class ZhanjiangHeroDoing2(val renji: Boolean = false) : HeroDoing(-1, FLAG_KEYEVENT) {

    val zhangjiang = HeroCreator.zhanjiang.create()
    val nvwang = HeroCreator.nvwang.create()
    val bingnv = HeroCreator.bingnv.create()
    val shengqi = HeroCreator.shengqi.create()
    val wangjiang = HeroCreator.wangjiang.create()
    val xiaolu = HeroCreator.xiaolu.create()
    val guangqiu = HeroCreator.guangqiu.create()

    val niutou = HeroCreator.niutou.create()
    val xiaochou = HeroCreator.xiaochou.create()

    val tuqiu = HeroCreator.tuqiu.create()


    fun isGaojiMengyan(): Boolean {
        var color = MRobot.robot.getPixelColor(74, 488)
        var red = color.red
        var blue = color.blue
        if (abs(red - 225) < 30 && abs(blue - 255) < 20) {
            log("高级梦魇啊")
            return true
        }
        return false
    }

    override suspend fun onKeyDown(code: Int): Boolean {
        if (code == KeyEvent.VK_NUMPAD0) {
            touxiang()
            return true
        }
        return super.onKeyDown(code)
    }

    private suspend fun touxiang() {
        delay(200)
        Config.pointDuiZhanRenshu.click()
        delay(500)
        Config.pointDuiZhanRenshuOk.click()
    }

    private fun getRenWuKa(): String {
        if (HanBingModel.renwuKa.value.isNullOrEmpty()) {
            return "wangjiang2"
        } else {
            return HanBingModel.renwuKa.value
        }
    }

    override fun initHeroes() {
        heros = arrayListOf(zhangjiang, nvwang, bingnv, shengqi, wangjiang, xiaolu, guangqiu, niutou, xiaochou, tuqiu)
        carDoing.downCardSpeed = true
//        needCheckQian = false
    }

    var mChePositionCount = 0

    override suspend fun doAfterHeroBeforeWaiting(heroBean: HeroBean) {
        super.doAfterHeroBeforeWaiting(heroBean)
        if (heroBean.needCar) {
            mChePositionCount = max(mChePositionCount, heroCountInCar())
        }
        if (heroBean == xiaolu) {
            carDoing.downHero(heroBean)
        } else if (heroBean == bingnv) {
            carDoing.downHero(bingnv)
        }
        if (renji) {
        } else {
            checkHero(zhangjiang)
            checkHero(nvwang)
        }
    }

    private suspend fun checkHero(heroBean: HeroBean) {
        if (heroBean.trueFull4Duizhan) return
        if (heroBean.isFull()) {
            delay(300)
            heroBean.checkStarLevelUseCard(carDoing)
        }
        if (heroBean.isFull()) {
            heroBean.trueFull4Duizhan = true
        }

    }


    override suspend fun dealHero(heros: List<HeroBean?>): Int {

        val zj = heros.upAny(zhangjiang)
        if (zj >= -1) return zj
        if (zhangjiang.isFull()) {
            val nvw = heros.upAny(nvwang,shengqi)
            if (nvw > -1) {
                return nvw
            }
        }
        val cf = heros.upAny(xiaolu, bingnv)
        if (cf >= -1) return cf

        return -1
    }

    override fun changeHeroWhenNoSpace(heroBean: HeroBean): HeroBean? {
        return null
    }

}