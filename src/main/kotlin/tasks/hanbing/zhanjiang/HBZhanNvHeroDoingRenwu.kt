package tasks.hanbing.zhanjiang

import data.*
import kotlinx.coroutines.*
import log
import tasks.XueLiang
import tasks.Zhuangbei
import tasks.hanbing.BaseHBHeroDoing
import ui.zhandou.hanbing.HanBingModel
import java.awt.event.KeyEvent.*

class HBZhanNvHeroDoingRenwu : BaseHBHeroDoing() {//默认赋值0，左边，借用左边第一个position得点击，去识别车位置后再更改
//es8  es4 ye5   6e0 y80  y50 y56 e04

    val zhanjiang = HeroBean("zhanjiang", 100)
    val nvwang = HeroBean("nvwang", 90)
    val saman = HeroBean("saman2", 80)
    val jiaonv = HeroBean("jiaonv", 70)
    val shahuang = HeroBean("shahuang", 60, compareRate = 0.9)
    val sishen = HeroBean("wangjiang2", 50)
    val muqiu = HeroBean("muqiu", 40, needCar = false, compareRate = 0.95)
    val baoku = HeroBean(getRenWuKa(), 30, needCar = true, isGongCheng = true, compareRate = 0.9)
    val huanqiu = HeroBean("huanqiu", 20, needCar = false, compareRate = 0.95)
    val guangqiu = HeroBean("guangqiu", 0, needCar = false)


    private fun getRenWuKa(): String {
        if (HanBingModel.renwuKa.value.isNullOrEmpty()) {
            return "kuiqian"
        } else {
            return HanBingModel.renwuKa.value
        }
    }

    fun fulls(vararg heros: HeroBean): Boolean {
        return heros.all {
            it.isFull()
        }
    }

    fun fullsBase(vararg heros: HeroBean): Boolean {
        return fulls(zhanjiang, jiaonv, sishen, *heros)
    }

    fun inCars(vararg heros: HeroBean): Boolean {
        return heros.all {
            it.isInCar()
        }
    }

    val longxin: Boolean
        get() = Zhuangbei.isLongxin() && Zhuangbei.hasZhuangbei()
    val qiangxi: Boolean
        get() = Zhuangbei.isQiangxi() && Zhuangbei.hasZhuangbei()
    val yandou: Boolean
        get() = Zhuangbei.isYandou() && Zhuangbei.hasZhuangbei()


    fun Int.notOk(block: () -> Int): Int {
        if (this < 0) return block()
        return this
    }

    fun List<HeroBean?>.upSingleHero(hero: HeroBean, useGuang: Boolean = true): Int {
        if (hero.isFull()) return -1
        return indexOf(hero).notOk {
            if (hero.isInCar() && useGuang) {
                indexOf(guangqiu)
            } else -1
        }
    }

    fun List<HeroBean?>.upAny(
        vararg heros: HeroBean,
        zhuangbei: (() -> Boolean)? = null,
        useGuang: Boolean = true
    ): Int {
        heros.forEach {
            var index = indexOf(it)
            if (index > -1) {
                return index
            }
        }

        if (zhuangbei != null) {
            var index = zhuangbei { zhuangbei() }
            if (index > -1) {
                return index
            }
        }

        if (useGuang) {
            if (heros.filter { it.isInCar() && !it.isFull() }.isNotEmpty()) {
                return indexOf(guangqiu)
            }
        }
        return -1
    }

    fun List<HeroBean?>.zhuangbei(block: () -> Boolean): Int {
        if (!block()) {
            return indexOf(huanqiu)
        }
        return -1
    }

    var g1 = GuanDeal({ fullsBase(saman) && longxin }, {
        return@GuanDeal if (!zhanjiang.isGold()) {
            upSingleHero(zhanjiang)
        } else {
            upAny(zhanjiang,jiaonv, saman, sishen, zhuangbei = { longxin })
        }
    })


    var g26 = GuanDeal({ fullsBase(saman, nvwang) && longxin }, {
        var fullList = arrayListOf<HeroBean>(nvwang, jiaonv, saman, sishen, zhanjiang)//防止没满
        if (isBaoku()) {
            fullList.add(baoku)
        }
        upAny(*(fullList.toTypedArray()), zhuangbei = { longxin })
    })
    var g41 = GuanDeal({ fullsBase(saman, nvwang, shahuang) && longxin }, {
        var fullList = arrayListOf<HeroBean>(shahuang, nvwang, jiaonv, saman, sishen, zhanjiang)//防止没满
        if (isBaoku()) {
            fullList.add(baoku)
        }
        upAny(*(fullList.toTypedArray()), zhuangbei = { longxin })
    })
    var g101 = GuanDeal({ fullsBase(shahuang) && yandou }, {
        upAny(shahuang, zhuangbei = { yandou })
    })
    var g108 = GuanDeal({ false }, {
        var fullList = arrayListOf<HeroBean>(shahuang, nvwang, jiaonv, saman, sishen, zhanjiang, guangqiu, muqiu)//防止没满
        if (isBaoku()) {
            fullList.add(baoku)
        }
        upAny(*(fullList.toTypedArray()), zhuangbei = { yandou })
    })
    var g110 = GuanDeal({ fullsBase(saman, nvwang, shahuang) && qiangxi }, {
        recheckStars()
        var fullList = arrayListOf<HeroBean>(shahuang, nvwang, jiaonv, saman, sishen, zhanjiang)//防止没满
        if (isBaoku()) {
            fullList.add(baoku)
        }
        var index = upAny(*(fullList.toTypedArray()))
        if (index > -1) {
            return@GuanDeal index
        }
        if (fullsBase(saman, nvwang, shahuang) && qiangxi) {//船长补满卡后，背木
            if (beimu) {
                index = indexOf(muqiu)
                if (index > -1) {
                    log("备好了木，等待使用")
                    while (!XueLiang.isMLess(0.5f) && beimu) {//这里如果不加&& beimu，如果副卡打了木，血量一直不低于40，那么这里就会一直卡着，船长监听那里，也会在适当时机把beimu改成false，这样这里就会扔出去了
                        delay(100)
                    }
                    log("血量低于50%，使用木")
                    beimu = false //变成false，用木后，判断needwaiting就是true了，就不会刷完木又来刷卡
                    return@GuanDeal index
                }
            }
        } else {
            index = indexOf(guangqiu)
            if (index > -1 && carDoing.hasNotFull()) {
                return@GuanDeal index
            }
        }

        index = indexOf(huanqiu)
        if (index > -1 && !Zhuangbei.isQiangxi() && Zhuangbei.hasZhuangbei()) {//小翼 烟斗
            if (guankaTask?.currentGuanIndex != 129) {//129 有次识别错了，然后幻了装备,所以129就不用幻
                return@GuanDeal index
            }
        }

        return@GuanDeal zhuangbei { qiangxi }
    })
    var g131 = GuanDeal({ fullsBase(saman, nvwang, shahuang) && longxin }, {
        var fullList = arrayListOf<HeroBean>(shahuang, nvwang, jiaonv, saman, sishen, zhanjiang)//防止没满
        if (isBaoku()) {
            fullList.add(baoku)
        }
        upAny(*(fullList.toTypedArray()), zhuangbei = { longxin })
    })
    var g139 = GuanDeal({ false }, {
        indexOf(muqiu)
    })
    var g140 = GuanDeal({ yandou }, {
        zhuangbei { yandou }
    })

    suspend fun recheckStars() {
        if (!recheckStarFor110) {
            carDoing.reCheckStars()
            recheckStarFor110 = true
        }
    }

    class GuanDeal(val isOver: () -> Boolean, val chooseHero: suspend List<HeroBean?>.() -> Int) {
    }

    var currentGaunDeal = g1


    var needZhuangbei = Zhuangbei.YANDOU//目前熊猫用，打完雷神时是烟斗，默认值用烟斗

    override fun onLeiShenSixBallOver() {
        super.onLeiShenSixBallOver()
        GlobalScope.launch {
            //因为识别到第6个球就回调了。所以这里再延迟5秒再刷木
            delay(5000)
            currentGaunDeal = g139
            waiting = false
        }
    }

    override fun onXiongMaoQiuGot(qiu: String) {
        super.onXiongMaoQiuGot(qiu)
        if (qiu == "fs") {
            needZhuangbei = Zhuangbei.YANDOU
        } else if (qiu == "gj") {
            needZhuangbei = Zhuangbei.QIANGXI
        } else if (qiu == "zs" || qiu == "ss") {//展示术士都用龙心
            needZhuangbei = Zhuangbei.LONGXIN
        }
        waiting = false
    }

    override fun doOnGuanChanged(guan: Int) {
        if (guan == 149) {
//            App.startAutoSave()
            startLeishenOberserver()
            GlobalScope.launch {
                delay(60000)
                currentGaunDeal = g139
                waiting = false
            }
            return
        }

        if (guan == 141) {
            currentGaunDeal = g140
            waiting = false
            return
        }

        if (guan == 139) {
//            App.stopAutoSave()
            currentGaunDeal = g139
            waiting = false
            return
        }
//        if (guan == 139) {
//            App.startAutoSave()
//        }

        if (guan == 131 || guan == 130) {
            stopChuanZhangOberserver()
            beimu = false
            currentGaunDeal = g131
            waiting = false
            return
        }

        if (guan == 128) {
            startChuanZhangOberserver()
            return
        }
        if (guan == 111) {
            currentGaunDeal = g110
            waiting = false
            return
        }

        if (guan == 109) {
            currentGaunDeal = g108
            waiting = false
            return
        }

        if (guan == 101) {
            longwangObserver = false
            currentGaunDeal = g101
            waiting = false
            return
        }
        if (guan == 98) {
            startLongWangOberserver()
            return
        }

        if (guan == 38) {
            currentGaunDeal = g41
            waiting = false
            return
        }

        if (guan == 21) {
            currentGaunDeal = g26
            waiting = false
            return
        }
    }

    override fun onHeroPointByChuanzhang(hero: HeroBean): Boolean {
        //船长不会点战将，这里除非是识别错了，如果识别错了，肯定也不下战将（目前只有另一个车中了，主车会偶尔识别到战将，这种就不下战将，其他都先不处理）
        return hero != zhanjiang
    }

    override suspend fun onLongwangPoint(point: MPoint, downed: (Boolean) -> Unit) {
        when (point) {
            Config.hbFSCloud -> {
                carDoing.downHero(shahuang)
                downed.invoke(true)
            }
//            Config.hbMSCloud ->{
//                carDoing.downHero(xiaoye)
//                carDoing.downHero(saman)
//                carDoing.downHero(jiaonv)
//                downed.invoke(true)
//            }
            else -> {
                downed.invoke(false)
            }
        }
    }

    private fun isBaoku(): Boolean {
        return baoku.heroName == "baoku" || baoku.heroName == "shexian"
    }

    fun isGkOver(g: GuanDeal): Boolean {
        return g.isOver.invoke()
    }


    override fun initHeroes() {
        heros = arrayListOf()
        heros.add(zhanjiang)
        heros.add(nvwang)
        heros.add(saman)
        heros.add(jiaonv)
        heros.add(shahuang)
        heros.add(sishen)
        heros.add(muqiu)
        heros.add(baoku)
        heros.add(huanqiu)
        heros.add(guangqiu)

        HanBingModel.imgs.clear()
        HanBingModel.imgs.addAll(heros.map {
            it.imgList.last()
        })
    }

    suspend fun doOnKeyDown(code: Int): Boolean {

        if (code == VK_NUMPAD1 || code == VK_1) {//按1刷木
            currentGaunDeal = g139
            waiting = false
        }
        if (guankaTask?.currentGuanIndex == 158 || guankaTask?.currentGuanIndex == 159 || guankaTask?.currentGuanIndex == 157) {
            if (code == VK_NUMPAD3) {
                needZhuangbei = Zhuangbei.YANDOU
                waiting = false
            } else if (code == VK_NUMPAD6) {
                needZhuangbei = Zhuangbei.LONGXIN
                waiting = false
            }
        }

//        if (code == VK_1 || code == VK_NUMPAD1) {
//            App.save()
//            carDoing.downHero(shahuang)
//            longyunStart = System.currentTimeMillis()
//        } else if (code == VK_2) {
//            App.save()
//            carDoing.downHero(xiaoye)
//            carDoing.downHero(saman)
//            carDoing.downHero(jiaonv)
//            longyunStart = System.currentTimeMillis()
//        } else if (code == VK_3) {
//            waiting = false
//            var endTime = System.currentTimeMillis()
//            log("识别到云到云炸，大约：${(endTime - longyunStart) / 1000}")
//        }

        return true
    }


    override suspend fun doAfterHeroBeforeWaiting(heroBean: HeroBean) {
        if (heroBean.heroName == "muqiu") {
            return
        }
        if (!waiting && isGkOver(currentGaunDeal)) {
            if (currentGaunDeal == g110 && beimu) {//如果beimu时，不waiting，去dealhero里去卡住
                waiting = false
            } else {
                waiting = true
            }
        }
    }


    /**
     * 110检查一遍星级，以补满卡
     */
    var recheckStarFor110 = false

    override suspend fun dealHero(heros: List<HeroBean?>): Int {
        while (waiting) {
            delay(100)
        }
        return currentGaunDeal.chooseHero.invoke(heros)
    }


    override fun changeHeroWhenNoSpace(heroBean: HeroBean): HeroBean? {
        return null
    }

    override suspend fun onKeyDown(code: Int): Boolean {
        if (super.onKeyDown(code)) {
            return true
        }
        return doOnKeyDown(code)
    }
}