package tasks.hanbing.deprecated.zhanjiang

import MainData
import data.*
import kotlinx.coroutines.*
import log
import tasks.XueLiang
import tasks.Zhuangbei
import tasks.hanbing.BaseHBHeroDoing
import ui.zhandou.UIKeyListenerManager
import java.awt.event.KeyEvent.*

class HBZhanNvHeroDoingBo3 : BaseHBHeroDoing(),
    UIKeyListenerManager.UIKeyListener {//默认赋值0，左边，借用左边第一个position得点击，去识别车位置后再更改
//es8  es4 ye5   6e0 y80  y50 y56 e04

    val zhanjiang = HeroBean("zhanjiang", 100)
    val nvwang = HeroBean("nvwang", 90)
    val saman = HeroBean("saman2", 80)
    val jiaonv = HeroBean("jiaonv", 70)
    val shengqi = HeroBean("shengqi", 60, compareRate = 0.9)
    val wangjiang = HeroBean("wangjiang2", 50)
    val muqiu = HeroBean("muqiu", 40, needCar = false, compareRate = 0.95)
    val bingqi = HeroBean("bingqi", 30)
    val huanqiu = HeroBean("huanqiu", 20, needCar = false, compareRate = 0.95)
    val guangqiu = HeroBean("guangqiu", 0, needCar = false)


    enum class Guan(val des: String? = null) {
        /**
         * 1-25g zhanjiang
         */
        g1("满战将"),

        /**
         * 上女王 吹风
         */
        g2("背卡 女王，按3上，或者8关后等待6秒自动上"),

        /**
         * 萨满，娇女
         */
        g3("上萨满娇女，会尽量压低女王等级，一定不会到4"),

        /**
         * 26-40 nvwang wangjiang
         */
        g26("上女王满，萨满娇女如果没满会继续上，亡将尽量压低"),

        /**
         * 41 shahuang
         */
        g38("沙皇亡将都满"),

        /**
         * 圣骑下，上冰骑
         */
        g91("圣骑下，上冰骑"),

        /**
         * 烟斗 ，下冰骑，上上圣骑
         */
        g101("烟斗 ，下冰骑，上上圣骑"),

        /**
         * 无脑补卡
         */
        g108("无脑补卡"),

        /**
         * checkStars 补满 强袭 爱神是 可以检测完星级，幻好装备后，手动下圣骑刷卡备圣骑。不操作的话就18女妖顶过去。脚本按不下来写，避免没看着电脑没上圣骑
         */
        g110("船长时可以按小键盘2154870进行点名下卡，按战车位置进行对应，0代表工程位"),

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
         * 幻龙心，下圣骑，上冰骑
         */
        g151("幻龙心，下圣骑，上冰骑"),

        //        g159_1("上冰骑，再按0上娇女"),
//        g159_2("上娇女满"),
//        g159_3("强袭"),
//        g161("啥也不干，看看娇女有没有上回位置")
        /**
         * 下冰骑，上圣骑
         */
        g161("下冰骑，上圣骑"),

        /**
         * 强袭
         */
        g171("强袭")
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


    override fun onLeiShenSixBallOver() {
        super.onLeiShenSixBallOver()
        GlobalScope.launch {
            //因为识别到第6个球就回调了。所以这里再延迟5秒再刷木
            log("六个球已经识别完毕，延迟8秒后开始刷木")
            delay(8000)
            guanka = Guan.g139
            waiting = false
//            shuaMuBeiMu(0)
        }
    }

    override fun onXiongMaoQiuGot(qiu: String) {
        super.onXiongMaoQiuGot(qiu)
//        if (qiu == "fs") {
//            needZhuangbei = Zhuangbei.LONGXIN
//        } else if (qiu == "gj") {
//            needZhuangbei = Zhuangbei.QIANGXI
//        } else if (qiu == "zs" || qiu == "ss") {//展示术士都用龙心
//            needZhuangbei = Zhuangbei.LONGXIN
//        }
//        waiting = false

//        if (qiu == "zs") {
//            guanka = Guan.g159_1
//            waiting = false
//        }
    }

    override fun doOnGuanChanged(guan: Int) {

//        if (guan == 159) {
//            guanka = Guan.g159
//            startXiongMaoOberser()
//            App.startAutoSave()
//            waiting = false
//            return
//        }
        if (guan == 171) {
            guanka = Guan.g171
            waiting = false
            return
        }

        if (guan == 169 || guan == 179) {
            guanka = Guan.g139
            waiting = false
            return
        }
        if (guan == 161) {
            guanka = Guan.g161
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

        if (guan == 148) {
//            App.startAutoSave()
            startLeishenOberserver()
            GlobalScope.launch {
                delay(50000)
                guanka = Guan.g139
                waiting = false
//                shuaMuBeiMu(0)
            }
            return
        }

        if (guan == 141) {
            guanka = Guan.g140
            waiting = false
            return
        }

        if (guan == 139) {
//            App.stopAutoSave()
            guanka = Guan.g139
            waiting = false
            return
        }
//        if (guan == 139) {
//            App.startAutoSave()
//        }

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
        if (guan == 111) {
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
            longwangObserver = false
            guanka = Guan.g101
            waiting = false
            return
        }
//        if (guan == 98) {
//            startLongWangOberserver()
//            return
//        }
        if(guan == 91){
            guanka = Guan.g91
            waiting = false
            return
        }

        if (guan == 38) {
            guanka = Guan.g38
            waiting = false
            return
        }

        if (guan == 22) {
            guanka = Guan.g26
            waiting = false
            return
        }

        if (guan == 11) {
            guanka = Guan.g3
            waiting = false
            return
        }

        if (guan == 8) {
            GlobalScope.launch {
                delay(6100)
                beiNvWang = false//6秒后 上备选的女王
            }
        }

        if (guan == 7) {
            guanka = Guan.g2
            waiting = false
            return
        }
    }

    override fun onHeroPointByChuanzhang(hero: HeroBean): Boolean {
        //船长不会点战将，这里除非是识别错了，如果识别错了，肯定也不下战将（目前只有另一个车中了，主车会偶尔识别到战将，这种就不下战将，其他都先不处理）
        return hero != zhanjiang
    }

    override suspend fun onLongwangPoint(point: MPoint, downed: (Boolean) -> Unit) {
        downed.invoke(false)
//        when (point) {
//            Config.hbFSCloud -> {
//                carDoing.downHero(shengqi)
//                downed.invoke(true)
//            }
//
//            else -> {
//                downed.invoke(false)
//            }
//        }
    }


    fun isGkOver(g: Guan): Boolean {

        if (g == Guan.g171) return Zhuangbei.isQiangxi()
        if (g == Guan.g161) return shengqi.isFull()
        if (g == Guan.g151) return Zhuangbei.isLongxin() && bingqi.isFull()
        if (g == Guan.g139) return false
        if (g == Guan.g140) return Zhuangbei.isYandou()
//        if (g == Guan.g140) return Zhuangbei.isYandou() && saman.isFull()
        if (g == Guan.g1) return zhanjiang.isFull()
        if (g == Guan.g2) return nvwang.isInCar()

        var heroOk = zhanjiang.isFull() && jiaonv.isFull() && saman.isFull()
        if (!heroOk) return false
        return when (g) {
            Guan.g3 -> return nvwang.isInCar()

            Guan.g26 -> {
                Zhuangbei.isLongxin() && nvwang.isFull() && wangjiang.isInCar()
            }

            Guan.g38 -> {
                Zhuangbei.isLongxin() && nvwang.isFull() && shengqi.isFull() && wangjiang.isFull()
            }

            Guan.g91 ->{
                bingqi.isInCar()
            }

            Guan.g101 -> {
                Zhuangbei.isYandou() && nvwang.isFull() && shengqi.isFull() && wangjiang.isFull()
            }

            Guan.g108 -> {//无脑补卡，这时降星 是不知道星级的
                false
            }

            Guan.g110 -> {
                Zhuangbei.isQiangxi() && nvwang.isFull() && shengqi.isFull() && wangjiang.isFull()
            }

            Guan.g131 -> {
                Zhuangbei.isQiangxi() && nvwang.isFull() && shengqi.isFull() && wangjiang.isFull()
            }

            else -> false
        }
    }


    override fun initHeroes() {
        heros = arrayListOf()
        heros.add(zhanjiang)
        heros.add(nvwang)
        heros.add(saman)
        heros.add(jiaonv)
        heros.add(shengqi)
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

//        if (guankaTask?.currentGuanIndex == 158 || guankaTask?.currentGuanIndex == 159) {
//            if (code == VK_NUMPAD3) {
//                guanka = Guan.g159_1
//                waiting = false
//            }else if(code == VK_NUMPAD2){
//                needZhuangbei = Zhuangbei.QIANGXI
//                guanka = Guan.g159_3
//                waiting = false
//            } else if (code == VK_NUMPAD0) {
//                if(guanka == Guan.g159_1) {
//                    guanka = Guan.g159_2
//                }
//                needZhuangbei = Zhuangbei.LONGXIN
//                waiting = false
//            }
//        }

        if (code == VK_NUMPAD3) {

            if (guankaTask?.currentGuanIndex == 8 || guankaTask?.currentGuanIndex == 9) {//女王吹风
                beiNvWang = false
            }

//            if (guankaTask?.currentGuanIndex == 98 || guankaTask?.currentGuanIndex == 99) {//龙王快捷键只需要处理fs
//                onLongWangFsClick()
//            }

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
        if(dropThisDeal){//特殊情况下，有时会保留上次的预选卡组，但有手动改变了卡组。导致再waiting变false后继续执行时，依然认为是之前卡组
            //所以这里有这个特殊情形时（比如爱神时要幻强袭，幻完后waiting变true，同时卡组里有木，这个木会计到船长那里，但爱神会手动上下圣骑
            //所以这个木已经没有了，但程序认为还在。）可以在改变waitting等false前，把drop设置true，这样deal就直接返回-1，会重新刷新想要的卡
            dropThisDeal = false
            return -1
        }

        if (guanka == Guan.g1) {//第一阶段
            if (!zhanjiang.isFull()) {//直上战将
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
            }
            return -1
        } else if (guanka == Guan.g2) {
            var index = heros.indexOf(nvwang)
            if (index > -1) {
                while (beiNvWang) {
                    delay(20)
                }
                return index
            }
            return -1
        } else if (guanka == Guan.g3) {
            var fullList = arrayListOf(jiaonv, saman)
            var index = defaultDealHero(
                heros,
                fullList
            )
            if (index > -1) return index

            if ((jiaonv.isInCar() && !jiaonv.isFull()) || (saman.isInCar() && !saman.isFull())) {
                //g3时，如果萨满或娇女在车上且不满，才用光球。比秒女王升级太快
                index = heros.indexOf(guangqiu)
                if (index > -1 && nvwang.currentLevel < 3) {
                    return index
                }
            }
//            index = heros.indexOf(nvwang)
//            if (index > -1 && nvwang.currentLevel < 3) {
//                return index
//            }


            index = heros.indexOf(huanqiu)
            if (index > -1 && !Zhuangbei.isLongxin() && Zhuangbei.hasZhuangbei()) {
                return index
            }
            return -1
        } else if (guanka == Guan.g26) {
            var fullList = arrayListOf(nvwang, jiaonv, saman, zhanjiang)//防止没满
//            if (isBaoku()) {
//                fullList.add(bingqi)
//            }
            var index = defaultDealHero(
                heros,
                fullList
            )
            if (index > -1) {
                return index
            }



            index = heros.indexOf(huanqiu)
            if (index > -1 && !Zhuangbei.isLongxin() && Zhuangbei.hasZhuangbei()) {//小翼 烟斗
                return index
            }
            index = heros.indexOf(guangqiu)
            if (index > -1) {
                return index
            }
            index = heros.indexOf(wangjiang)
            if (index > -1 && !wangjiang.isInCar()) {
                return index
            }

        } else if (guanka == Guan.g38) {
            var fullList = arrayListOf( nvwang, jiaonv, saman, wangjiang,shengqi, zhanjiang)//防止没满
//            if (isBaoku()) {
//                fullList.add(bingqi)
//            }
            var index = defaultDealHero(
                heros,
                fullList
            )
            if (index > -1) {
                return index
            }
            index = heros.indexOf(huanqiu)
            if (index > -1 && !Zhuangbei.isLongxin() && Zhuangbei.hasZhuangbei()) {//小翼 烟斗
                return index
            }
            index = heros.indexOf(guangqiu)
            if (index > -1) {
                return index
            }
        }else if (guanka == Guan.g91){
            if(shengqi.isInCar()){
                carDoing.downHero(shengqi)
            }
            return heros.indexOf(bingqi)
        }
        else if (guanka == Guan.g101) {//101 yandou

            if (bingqi.isInCar()) {
                carDoing.downHero(bingqi)
            }

            var fullList = arrayListOf(nvwang, shengqi, saman, zhanjiang, wangjiang, jiaonv)
//            if (isBaoku()) {
//                fullList.add(bingqi)
//            }
            var index = defaultDealHero(
                heros,
                fullList
            )
            if (index > -1) {
                return index
            }
            //在之前基础上 先刷龙心
            index = heros.indexOf(huanqiu)
            if (index > -1 && !Zhuangbei.isYandou() && Zhuangbei.hasZhuangbei()) {//小翼 烟斗
                return index
            }
            index = heros.indexOf(guangqiu)
            if (index > -1) {
                return index
            }

        } else if (guanka == Guan.g108) {//乱补
            var fullList = arrayListOf(nvwang, shengqi, saman, zhanjiang, wangjiang, jiaonv, guangqiu, muqiu)
//            if (isBaoku()) {
//                fullList.add(bingqi)
//            }
            var index = defaultDealHero(
                heros,
                fullList
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

            var fullList = arrayListOf(zhanjiang, shengqi, jiaonv, wangjiang, nvwang, saman)
//            if (isBaoku()) {
//                fullList.add(bingqi)
//            }
            var index = defaultDealHero(
                heros,
                fullList
            )
            if (index > -1) return index

            if (isGkOver(Guan.g110)) {//船长补满卡后，背木
                if (beimu) {
                    index = heros.indexOf(muqiu)
                    if (index > -1) {
                        log("备好了木，等待使用")
                        while (!XueLiang.isMLess(0.4f) && beimu) {//这里如果不加&& beimu，如果副卡打了木，血量一直不低于40，那么这里就会一直卡着，船长监听那里，也会在适当时机把beimu改成false，这样这里就会扔出去了
                            delay(50)
                        }
                        log("血量低于40%，使用木")
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
            var fullList = arrayListOf(zhanjiang, nvwang, shengqi, jiaonv, wangjiang, saman)
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
            if (index > -1) {
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
            if (shengqi.isInCar()) {
                carDoing.downHero(shengqi)
            }
            var index = heros.indexOf(bingqi)
            if (index > -1) {
                return index
            }
            if (bingqi.isInCar() && !bingqi.isFull()) {
                index = heros.indexOf(guangqiu)
                if (index > -1) {
                    return index
                }
            }


            index = heros.indexOf(huanqiu)
            if (index > -1 && !Zhuangbei.isLongxin()) {
                return index
            }
        } else if (guanka == Guan.g161) {

            if (bingqi.isInCar()) {
                carDoing.downHero(bingqi)
            }

            var fullList = arrayListOf(zhanjiang, nvwang, shengqi, jiaonv, wangjiang, saman, guangqiu)
            var index = defaultDealHero(
                heros,
                fullList
            )
            if (index > -1) return index

        } else if (guanka == Guan.g171) {
            var index = heros.indexOf(huanqiu)
            if (index > -1 && !Zhuangbei.isQiangxi()) {
                return index
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
        //如果反应慢，点的慢，内部也是计时10s上回，如果慢了的话，可以主动改变waiting，通过UI面板或者快捷键9
//        longWangDownLoadPositionFromKey = carDoing.carps.indexOfFirst {
//            it.mHeroBean == shengqi
//        }
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