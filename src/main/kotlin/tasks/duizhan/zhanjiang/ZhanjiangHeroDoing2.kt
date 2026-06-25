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

    val kuanglong = HeroBean(getRenWuKa(), 3)
    val xiaochou = HeroCreator.xiaochou.create()

    val dijing = HeroCreator.dijing.create()


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
            return "kuanglong"
        } else {
            return HanBingModel.renwuKa.value
        }
    }

    override fun initHeroes() {
        userNewShuaxin = false
        heros =
            arrayListOf(zhangjiang, nvwang, bingnv, shengqi, wangjiang, xiaolu, guangqiu, kuanglong, xiaochou, dijing)
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

        val zj = heros.indexOf(zhangjiang)
        if(zj>-1){
            return zj
        }

        val guangQiuIndex = heros.indexOf(guangqiu)

        //有光球一定会用，只是看是不是值得 下其他卡。补：要验证是不是满了。。
        if (guangQiuIndex > -1 && carDoing.hasNotFull()) {//战将在时，优先光，否则upany是最后才用光的。。。。。
            if (zhangjiang.currentLevel in listOf(3, 4)) {
                val herosInCarAndNotFull = this.heros.filter {
                    it.isInCar() && it.currentLevel<4 && it!=zhangjiang
                }
                //战将等级等于3或4，需要冲刺魔化或金时 才下卡，否则就正常上卡
                //防止下卡太多影响布阵，只有下卡数量少于等于2个时才下卡，否则就直接使用光球
                if (herosInCarAndNotFull.count() <= 2) {
                    herosInCarAndNotFull.forEach {
                        carDoing.downHero(it)
                    }

                }
                return guangQiuIndex
            } else if (zhangjiang.isInCar() && !zhangjiang.isFull()) {
                return guangQiuIndex
            }
        }


        val index = if (!isGaojiMengyan() || shengqi.isInCar()) {
            if (carDoing.carps.count { it.hasHero() } >= 6) {
                log("车满了")
                heros.upAnyNotInCarFirst( shengqi, dijing, xiaochou, wangjiang, nvwang)
            } else {
                log("车没满了")
                heros.upAnyNotInCarFirst(

                    xiaolu,
                    dijing,
                    shengqi,
                    bingnv,
                    xiaochou,
                    wangjiang,
                    nvwang
                )
            }
        } else heros.upAny( shengqi, xiaochou, wangjiang, nvwang)



        return index
    }

    override fun changeHeroWhenNoSpace(heroBean: HeroBean): HeroBean? {
//        if (mChePositionCount >= 4) {
//            //遇到上不了的，就尝试土球
//            return dijing
//        }
        if (heroBean == zhangjiang) {
            listOf(nvwang, wangjiang, xiaolu, shengqi, dijing).forEach {
                if (it.isInCar() && !it.isFull()) {
                    return it
                }
            }
        }
        return null
    }

}