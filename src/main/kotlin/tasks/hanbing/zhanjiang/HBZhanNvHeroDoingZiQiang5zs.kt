package tasks.hanbing.zhanjiang

import MainData
import data.*
import kotlinx.coroutines.*
import log
import tasks.XueLiang
import tasks.Zhuangbei
import tasks.hanbing.BaseHBHeroDoing
import ui.zhandou.UIKeyListenerManager
import ui.zhandou.hanbing.HanBingModel
import java.awt.event.KeyEvent.*

class HBZhanNvHeroDoingZiQiang5zs : BaseHBHeroDoing(),
    UIKeyListenerManager.UIKeyListener {//默认赋值0，左边，借用左边第一个position得点击，去识别车位置后再更改
//es8  es4 ye5   6e0 y80  y50 y56 e04

        var isRenwu = false

    val zhanjiang = HeroBean("zhanjiang2", 100, isMohua = true)
    val saman = HeroBean("saman2", 90)
//    val saman = HeroBean("xiaoye", 90)
    val tieqi = HeroBean("tieqi", 80)
    val wangjiang = HeroBean("wangjiang2", 70)
    val sishen = HeroCreator.sishen2.create()
    val yuren = HeroBean("yuren", 50)
    val muqiu = HeroBean(if(isRenwu) HanBingModel.renwuKa.value else "muqiu", 40, needCar = false, compareRate = 0.95)
    val bingqi = HeroCreator.bingqi.create()
    val huanqiu = HeroBean("huanqiu", 20, needCar = false, compareRate = 0.95)
    val guangqiu = HeroBean("guangqiu", 0, needCar = false)


    enum class Guan(val des: String? = null) {
        /**
         *  满战将，死神，铁骑，鱼人，亡将，萨满
         */
        g1("满战将，死神，铁骑，鱼人，亡将，萨满"),

        /**
         * 下鱼人，上冰骑
         */
        g91("下鱼人，上冰骑"),

        /**
         * 烟斗 ，下冰骑，上鱼人
         */
        g101("烟斗 ，下冰骑，上鱼人"),

        /**
         * 无脑补卡
         */
        g108("无脑补卡"),

        /**
         * 强袭，船长时可以按小键盘2154870进行点名下卡，按战车位置进行对应，0代表工程位
         */
        g110("强袭，船长时可以按小键盘2154870进行点名下卡，按战车位置进行对应，0代表工程位"),

        /**
         * 强袭 船长有上下卡,
         */
        g131("继续强袭，保持卡满"),

        /**
         *  一直刷木
         */
        g139("一直刷木，监控血量低于90%，会自动刷，也可以按3进行释放备好的木刷木"),

        /**
         * 烟斗
         */
        g140("幻烟斗,149时会自动延迟6个球的时间，或检测到6球结束后，切到guan139进行补木"),


        /**
         * 幻龙心,下亡将，上冰骑,159会根据管卡改变将当前设置为139行为
         */
        g151("幻龙心,下亡将，上冰骑，159会根据管卡改变将当前设置为139行为"),

        /**
         * 下冰骑，上亡将
         */
        g161("下冰骑，上亡将"),

        /**
         * 强袭
         */
        g171("强袭"),
        /**
         * 补星
         */
        g189("补星"),
    }

    var guanka = Guan.g1
        set(value) {
            field = value
            MainData.curGuanKaName.value = value.name
            MainData.curGuanKaDes.value = value.des ?: "无"
        }

    override fun onStart() {
        super.onStart()
        UIKeyListenerManager.addKeyListener(this)
    }

    override fun onStop() {
        super.onStop()
        UIKeyListenerManager.removeKeyListener(this)
    }

    override fun doOnGuanChanged(guan: Int) {

        if (guan == 189) {
            guanka = Guan.g189
            waiting = false
            return
        }


        if (guan == 171) {
            guanka = Guan.g171
            waiting = false
            return
        }

        if (guan == 179) {
            guanka = Guan.g139
            waiting = false
            return
        }
        if (guan == 161) {
            guanka = Guan.g161
            waiting = false
            return
        }

        if (guan == 159) {//没亡将了，刷木
            guanka = Guan.g139
            waiting = false
            return
        }


        if (guan == 151) {
//            App.stopAutoSave()
            leishenOberser = false
            guanka = Guan.g151
            waiting = false
            return
        }

        if (guan == 141) {
            guanka = Guan.g140
            waiting = false
            return
        }

        if (guan == 139) {
            guanka = Guan.g139
            waiting = false
            return
        }

        if (guan == 131) {
            stopChuanZhangOberserver()
            beimu = false
            guanka = Guan.g131
            waiting = false
            return
        }

        if (guan == 128) {
            startChuanZhangOberserver()
            return
        }
        if (guan == 110) {
            guanka = Guan.g110
            waiting = false
            return
        }

        if (guan == 109) {
            guanka = Guan.g108
            waiting = false
            return
        }

        if (guan == 101) {
            guanka = Guan.g101
            waiting = false
            return
        }

        if (guan == 91) {
            guanka = Guan.g91
            waiting = false
            return
        }
    }

    override fun onHeroPointByChuanzhang(hero: HeroBean): Boolean {
        //船长不会点战将，这里除非是识别错了，如果识别错了，肯定也不下战将（目前只有另一个车中了，主车会偶尔识别到战将，这种就不下战将，其他都先不处理）
        return hero != zhanjiang
    }

    override suspend fun onLongwangPoint(point: MPoint, downed: (Boolean) -> Unit) {
    }


    fun isGkOver(g: Guan): Boolean {

        if (g == Guan.g91) {
            return zhanjiang.isFull() && tieqi.isFull() && wangjiang.isFull() && saman.isFull() && sishen.isFull() && bingqi.isFull()
        }
        if (g == Guan.g151) {
            return Zhuangbei.isLongxin() && zhanjiang.isFull() && tieqi.isFull() && yuren.isFull() && saman.isFull() && sishen.isFull() && bingqi.isFull()
        }

        var heroOk =
            zhanjiang.isFull() && tieqi.isFull() && yuren.isFull() && saman.isFull() && sishen.isFull() && wangjiang.isFull()
        if (!heroOk) return false

        return when (g) {

            Guan.g1 -> Zhuangbei.isShengjian()

            Guan.g101 -> {
                Zhuangbei.isYandou()
            }

            Guan.g108 -> {//无脑补卡，这时降星 是不知道星级的
                false
            }

            Guan.g110 -> {
                Zhuangbei.isQiangxi()
            }

            Guan.g131 -> {
                Zhuangbei.isQiangxi()
            }

            Guan.g140 -> Zhuangbei.isYandou()

            Guan.g161 -> true
            Guan.g171 -> Zhuangbei.isQiangxi()
            else -> false
        }
    }


    override fun initHeroes() {
        heros = arrayListOf()
        heros.add(zhanjiang)
        heros.add(saman)
        heros.add(tieqi)
        heros.add(yuren)
        heros.add(sishen)
        heros.add(wangjiang)
        heros.add(muqiu)
        heros.add(bingqi)
        heros.add(huanqiu)
        heros.add(guangqiu)
    }

    suspend fun doOnKeyDown(code: Int): Boolean {


        if (code == VK_NUMPAD1 || code == VK_1) {//按1刷木
            guanka = Guan.g139
            waiting = false
        }

        if (code == VK_NUMPAD3) {

            if (guanka == Guan.g139) {
                muBeiLe = false//防止血量监控不准，可以按3进行释放木
            }
        }
        return true
    }


    override suspend fun doAfterHeroBeforeWaiting(heroBean: HeroBean) {
        if (heroBean.heroName == "muqiu") {
            return
        }
        if (!waiting && isGkOver(guanka)) {
            if (guanka == Guan.g110 && beimu) {//如果beimu时，不waiting，去dealhero里去卡住
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

    var beiNvWang: Boolean = true


    override suspend fun dealHero(heros: List<HeroBean?>): Int {
        while (waiting) {
            delay(100)
        }
//        if (!waiting && isGkOver(guanka)) {
//            waiting = true
//        }
        if (dropThisDeal) {//特殊情况下，有时会保留上次的预选卡组，但有手动改变了卡组。导致再waiting变false后继续执行时，依然认为是之前卡组
            //所以这里有这个特殊情形时（比如爱神时要幻强袭，幻完后waiting变true，同时卡组里有木，这个木会计到船长那里，但爱神会手动上下圣骑
            //所以这个木已经没有了，但程序认为还在。）可以在改变waitting等false前，把drop设置true，这样deal就直接返回-1，会重新刷新想要的卡
            dropThisDeal = false
            return -1
        }

        if (guanka == Guan.g1) {//第一阶段
            if (!zhanjiang.isGold()) {//直上战将
                log("战将没满")
//                log("战将没满")
                var index = heros.indexOf(zhanjiang)
                if (index > -1) {
                    return index
                }
                index = heros.indexOf(guangqiu)
                if (index > -1 && zhanjiang.isInCar()) {
                    return index
                }

            } else {
                var index = defaultDealHero(
                    heros, arrayListOf(
                        zhanjiang, sishen, saman, tieqi,
                        yuren,
                        wangjiang, guangqiu
                    )
                )
                if (index > -1) {
                    return index
                }
            }
            var index = heros.indexOf(huanqiu)
            if (index > -1 && !Zhuangbei.isShengjian() && Zhuangbei.hasZhuangbei()) {
                return index
            }
            return -1
        } else if (guanka == Guan.g91) {
            carDoing.downHero(yuren)
            return heros.indexOf(bingqi)
        } else if (guanka == Guan.g101) {//101 yandou

            carDoing.downHero(bingqi)

            var index = defaultDealHero(heros, arrayListOf(yuren, guangqiu))
            if (index > -1) {
                return index
            }

            //在之前基础上 先刷龙心
            index = heros.indexOf(huanqiu)
            if (index > -1 && !Zhuangbei.isYandou() && Zhuangbei.hasZhuangbei()) {//小翼 烟斗
                return index
            }

        } else if (guanka == Guan.g108) {//乱补
            var fullList = arrayListOf(
                saman, sishen, tieqi, zhanjiang,
                yuren,
                wangjiang, guangqiu
            )
//            if (isBaoku()) {
//                fullList.add(bingqi)
//            }
            var index = defaultDealHero(
                heros,
                fullList, true
            )
            if (index > -1) {//补太快费钱，延迟300试试
                delay(300)
                return index
            }
            index = heros.indexOf(huanqiu)
            if (index > -1 && !Zhuangbei.isYandou() && Zhuangbei.hasZhuangbei()) {//小翼 烟斗
                return index
            }

        } else if (guanka == Guan.g110) {//111

            if (!recheckStarFor110) {
                carDoing.reCheckStars()
                recheckStarFor110 = true
            }

            var fullList = arrayListOf(
                zhanjiang, sishen,
                yuren,
                wangjiang, saman, tieqi
            )
//            if (isBaoku()) {
//                fullList.add(bingqi)
//            }
            var index = defaultDealHero(
                heros,
                fullList
            )
            if (index > -1) return index

            if (isGkOver(Guan.g110)) {//船长补满卡后，背木
                if (beimu && !isRenwu) {
                    index = heros.indexOf(muqiu)
                    if (index > -1) {
                        log("备好了木，等待使用")
                        while (!XueLiang.isMLess(0.5f) && beimu) {//这里如果不加&& beimu，如果副卡打了木，血量一直不低于40，那么这里就会一直卡着，船长监听那里，也会在适当时机把beimu改成false，这样这里就会扔出去了
                            delay(50)
                        }
                        log("血量低于50%，使用木")
                        beimu = false //变成false，用木后，判断needwaiting就是true了，就不会刷完木又来刷卡
                        return index
                    }
                }


            } else {
                index = heros.indexOf(guangqiu)
                if (index > -1 && carDoing.hasNotFull()) {
                    return index
                }
            }


            index = heros.indexOf(huanqiu)
            if (index > -1 && !Zhuangbei.isQiangxi() && Zhuangbei.hasZhuangbei()) {//小翼 烟斗
                if (guankaTask?.currentGuanIndex != 129 && guankaTask?.currentGuanIndex != 128) {//129 有次识别错了，然后幻了装备,所以129就不用幻
                    return index
                }
            }


        } else if (guanka == Guan.g131) {
            //防止船长点完卡没补满
            var fullList = arrayListOf(
                zhanjiang, saman, sishen,
                yuren,
                wangjiang, tieqi, guangqiu
            )
            var index = defaultDealHero(
                heros,
                fullList
            )
            if (index > -1) return index

            if (isGkOver(guanka)) {
                waiting = true
                return -1
            }
        } else if (guanka == Guan.g139) {//刷木
            var index = heros.indexOf(muqiu)
            if (index > -1 && !isRenwu) {
                muBeiLe = true
                while (guanka == Guan.g139 && (!XueLiang.isMLess(0.9f) || !muBeiLe)) {
                    delay(50)
                }
                return index
            }

        } else if (guanka == Guan.g140) {

            var index = heros.indexOf(huanqiu)
            if (index > -1 && !Zhuangbei.isYandou()) {//小翼 烟斗
                return index
            }

        } else if (guanka == Guan.g151) {
            carDoing.downHero(wangjiang)
            var index = defaultDealHero(heros, arrayListOf(bingqi, guangqiu))
            if (index > -1) {
                return index
            }
            index = heros.indexOf(huanqiu)
            if (index > -1 && !Zhuangbei.isLongxin()) {
                return index
            }
        } else if (guanka == Guan.g161) {

            carDoing.downHero(bingqi)

            var index = defaultDealHero(heros, arrayListOf(wangjiang, guangqiu))
            if (index > -1) {
                return index
            }

        } else if (guanka == Guan.g171) {
            var index = heros.indexOf(huanqiu)
            if (index > -1 && !Zhuangbei.isQiangxi()) {
                return index
            }
        }else if(guanka == Guan.g189){
            if(isRenwu) {
                while(guanka == Guan.g189){
                    delay(1000)
                }
                return -1
            }
            var mu = heros.indexOf(muqiu)
            while(guanka == Guan.g189){
                //每次循环都检测血量，血量低优先刷木
                if(XueLiang.isMLess(0.9f) ){
                    if(mu>-1) {
                        return mu
                    }
                    //如果没木就补星，没有就刷牌了
                    return defaultDealHero(heros, arrayListOf(zhanjiang,tieqi,yuren,saman,wangjiang,sishen,guangqiu))
                }
                if(carDoing.hasOpenSpace() || carDoing.hasNotFull()){
                    return defaultDealHero(heros, arrayListOf(zhanjiang,tieqi,yuren,saman,wangjiang,sishen,guangqiu))
                }
                carDoing.reCheckStars()
                if(carDoing.hasOpenSpace() || carDoing.hasNotFull()){
                    return defaultDealHero(heros, arrayListOf(zhanjiang,tieqi,yuren,saman,wangjiang,sishen,guangqiu))
                }
            }
        }
        return -1
    }

    var muBeiLe = false

    override fun changeHeroWhenNoSpace(heroBean: HeroBean): HeroBean? {
        return null
    }

    override suspend fun onKeyDown(code: Int): Boolean {
        if (super.onKeyDown(code)) {
            return true
        }
        return doOnKeyDown(code)
    }

    override fun onWaitingClick() {
        waiting = !waiting
        log("cur waiting is $waiting")
    }

    override fun onLongWangZsClick() {
    }

    override fun onLongWangFsClick() {
    }

    override fun onChuanzhangClick(position: Int) {
        chuanzhangDownLoadPositionFromKey = position
    }

    override fun onKey3Down() {
        GlobalScope.launch {
            onKeyDown(VK_NUMPAD3)
        }
    }

    override fun onGuanFix(guan: Int) {
        guankaTask?.setCurGuanIndex(guan)
    }
}