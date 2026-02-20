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

    val tuqiu = HeroCreator.dijing.create()


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
        if (heroBean == xiaolu || heroBean == bingnv) {
            carDoing.downHero(heroBean)
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
        val listBeforeZJ = listOf(xiaochou, wangjiang)
        val zj = heros.upAny(zhangjiang)
        if (zj > -1) {
            //战将没满时，如果有之前上了防止刷不到战将的，就下来
            listBeforeZJ.forEach {
                carDoing.downHero(it)
            }
            return zj
        }

        if (zhangjiang.isFull()) {
            val list = listOf(nvwang, shengqi, niutou, xiaochou, wangjiang)
            val inCar = list.filter { it.isInCar() }
            val notInCar = list.filter { !it.isInCar() }
            val upList = arrayListOf<HeroBean>().apply {
                addAll(notInCar)
                addAll(inCar)
            }
            val nvw = heros.upAny(upList)
            if (nvw > -1) {
                return nvw
            }
        }
        val cf = heros.upAny(xiaolu, bingnv)
        if (cf >= -1) return cf
//        val tu = heros.indexOf(tuqiu)
//        if (tu > -1) {
//            return tu
//        }

        if (!zhangjiang.isInCar()) {
            val list = listOf(xiaochou, wangjiang)//这里去掉女王，女王攻速慢，而且一下清兵太多，抢战将兵
            if (list.any { it.isInCar() }) {//没战将时，上面的也都没走，就先上一个输出
                return -1
            }
            return heros.upAny(list)
        }

        return -1
    }

    override fun changeHeroWhenNoSpace(heroBean: HeroBean): HeroBean? {
//        if (mChePositionCount >= 4) {
//            //遇到上不了的，就尝试土球
//            return tuqiu
//        }
        return null
    }

}