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

class HBZhanNvHeroDoing3Beifen : BaseHBHeroDoing(), UIKeyListenerManager.UIKeyListener {//默认赋值0，左边，借用左边第一个position得点击，去识别车位置后再更改
//es8  es4 ye5   6e0 y80  y50 y56 e04

    val zhanjiang = HeroBean("zhanjiang", 100)
    val nvwang = HeroBean("nvwang", 90)
    val saman = HeroBean("saman2", 80)
    val jiaonv = HeroBean("jiaonv", 70)
    val shahuang = HeroBean("shahuang", 60, compareRate = 0.9)
    val wangjiang = HeroBean("wangjiang2", 50)
    val muqiu = HeroBean("muqiu", 40, needCar = false, compareRate = 0.95)
    val bingqi = HeroBean("bingqi", 30)
    val huanqiu = HeroBean("huanqiu", 20, needCar = false, compareRate = 0.95)
    val guangqiu = HeroBean("guangqiu", 0, needCar = false)


    enum class Guan(val des:String?=null) {
        /**
         * 1-25g zhanjiang
         */
        g1,

        /**
         * 上女王 吹风
         */
        g2,

        /**
         * 萨满，娇女
         */
        g3,

        /**
         * 26-40 nvwang wangjiang
         */
        g26,

        /**
         * 41 shahuang
         */
        g41,

        /**
         * 防止战士云，直接下亡将，上冰骑
         */
        g91,
        /**
         * 烟斗
         */
        g101,

        /**
         * 无脑补卡
         */
        g108,

        /**
         * checkStars 补满 强袭
         */
        g110("船长时可以按小键盘2154870进行点名下卡，按战车位置进行对应，0代表工程位"),

        /**
         * 强袭 船长有上下卡,下沙皇，上冰骑
         */
        g131,

        /**
         *  一直刷木
         */
        g139("按3进行刷木备木"),

        /**
         * 烟斗
         */
        g140,

        /**
         * 熊猫关，改龙心，不用监听
         */
        g159("按3进行备冰骑，再按3下娇女上冰骑，再按3下冰骑满回娇女"),
        gShuaMuBeiMu("备木阶段，按3进行3秒刷木（或刷一次木）后备木"),

    }

    var guanka = Guan.g1
        set(value) {
            field = value
            MainData.curGuanKaName.value = value.name
            MainData.curGuanKaDes.value = value.des?:"无"
            if(value != Guan.gShuaMuBeiMu){
                lastGuan = value
            }
        }


    var needZhuangbei = Zhuangbei.LONGXIN


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
//            delay(8000)
//            guanka = Guan.g139
//            waiting = false
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
    }

    override fun doOnGuanChanged(guan: Int) {

        if(guan == 161){
            shuaMuBeiMu()
            return
        }

//        if (guan == 159) {
//            guanka = Guan.g159
//            startXiongMaoOberser()
//            App.startAutoSave()
//            waiting = false
//            return
//        }

        if (guan == 152) {
//            App.stopAutoSave()
            leishenOberser = false
            guanka = Guan.g159
            needZhuangbei = Zhuangbei.LONGXIN
            waiting = false
            return
        }

        if (guan == 148) {
//            App.startAutoSave()
//            startLeishenOberserver()
//            GlobalScope.launch {
//                delay(60000)
////                guanka = Guan.g139
////                waiting = false
//                shuaMuBeiMu(0)
//            }
            shuaMuBeiMu(0)
            return
        }

        if (guan == 141) {
            guanka = Guan.g140
            waiting = false
            return
        }

        if (guan == 135) {
            shuaMuBeiMu(0)
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
        if (guan == 98) {
            startLongWangOberserver()
            return
        }

        if(guan==91){
            guanka = Guan.g91
            waiting = false
            return
        }

        if (guan == 38) {
            guanka = Guan.g41
            waiting = false
            return
        }

        if (guan == 22) {
            guanka = Guan.g26
            waiting = false
            return
        }

        if(guan == 11){
            guanka = Guan.g3
            waiting = false
            return
        }

        if(guan == 8){
            GlobalScope.launch {
                delay(4000)
                guanka = Guan.g2
                waiting = false
            }
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


    var beiShuaMu = true
    var lastGuan = Guan.g1

    var lastBeishuaMuTime = 0L
    var shumuDuration = 3000//<=0时代表只刷一次，比如沙皇，雷神。但牛头就有连着刷，多段掉血，且一个木刷不满。
    var BeiBingqiFor159 :Boolean? = true

    fun shuaMuBeiMu(duration :Int = 3000){
        shumuDuration = duration
        if(guanka!= Guan.gShuaMuBeiMu) {
            lastGuan = guanka
        }
        guanka = Guan.gShuaMuBeiMu
        waiting = false
    }

    /**
     * 修改beiShuaMu状态，开始扔木，前期用在一次按键得监听上，之后可以在具体算法里执行，比如监听血量一黑就触发一次
     */
    fun doShuaMuOne(){
        beiShuaMu = false
    }

    /**
     * 暂时可以不用，因为刷木就沙皇和169牛用，管卡过后就自动改guanka了
     */
    fun stopShuaMu(){
        log("还原lastGuan ${lastGuan.name}")
        beiShuaMu = false
        guanka = lastGuan
        waiting = false
    }

//    private fun isBaoku(): Boolean {
//        return bingqi.heroName == "baoku" || bingqi.heroName == "shexian"
//    }

    fun isGkOver(g: Guan): Boolean {

        if (g == Guan.g159) return Zhuangbei.getZhuangBei() == needZhuangbei && (if(BeiBingqiFor159!=false) jiaonv.isFull() else bingqi.isFull())
        if (g == Guan.g139 || g == Guan.gShuaMuBeiMu) return false
        if (g == Guan.g140) return Zhuangbei.isYandou()
//        if (g == Guan.g140) return Zhuangbei.isYandou() && saman.isFull()
        if (g == Guan.g1) return zhanjiang.isFull()
        if (g == Guan.g2) return nvwang.isInCar()

        var heroOk = zhanjiang.isFull() && jiaonv.isFull() && saman.isFull()
        if (!heroOk) return false
        return when (g) {
            Guan.g3 -> return nvwang.isInCar()

            Guan.g26 -> {
                Zhuangbei.isLongxin() && nvwang.isFull() && wangjiang.isFull()
            }

            Guan.g41 -> {
                Zhuangbei.isLongxin() && nvwang.isFull() && shahuang.isFull() && wangjiang.isFull()
            }

            Guan.g91 ->{
                bingqi.isInCar()
            }

            Guan.g101 -> {
                Zhuangbei.isYandou() && nvwang.isFull() && shahuang.isFull() && wangjiang.isFull()
            }

            Guan.g108 -> {//无脑补卡，这时降星 是不知道星级的
                false
            }

            Guan.g110 -> {
                Zhuangbei.isQiangxi() && nvwang.isFull() && shahuang.isFull() && wangjiang.isFull()
            }

            Guan.g131 -> {
                Zhuangbei.isQiangxi() && nvwang.isFull() && bingqi.isFull() && wangjiang.isFull()
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
        heros.add(shahuang)
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

//        if (guankaTask?.currentGuanIndex == 158 || guankaTask?.currentGuanIndex == 159 || guankaTask?.currentGuanIndex == 157) {
//            if (code == VK_NUMPAD3) {
//                needZhuangbei = Zhuangbei.QIANGXI
//                waiting = false
//            } else if (code == VK_NUMPAD6) {
//                needZhuangbei = Zhuangbei.LONGXIN
//                waiting = false
//            }
//        }

        if (code == VK_NUMPAD3) {
            if (guankaTask?.currentGuanIndex == 118 || guankaTask?.currentGuanIndex == 119 || guankaTask?.currentGuanIndex == 117) {
                beiSQ = !beiSQ
                waiting = false
            }

            if(guankaTask?.currentGuanIndex == 98 || guankaTask?.currentGuanIndex == 99){//龙王快捷键只需要处理fs
                onLongWangFsClick()
            }

            if(guankaTask?.currentGuanIndex == 158 || guankaTask?.currentGuanIndex == 159) {
                if (waiting) {//初始，可能幻完就over了，不会触发备冰骑，这时按3，waitingfalse后就备冰骑
                    waiting = false
                    return true
                }
                if(BeiBingqiFor159 == true){//按完下娇女 上冰骑满
                    BeiBingqiFor159 = false
                    waiting = false
                    return true
                }
                if (BeiBingqiFor159 == false) {//按完，下冰骑上娇女满
                    BeiBingqiFor159 = null
                    waiting = false
                    return true
                }

            }

            if(guankaTask?.currentGuanIndex == 168 || guankaTask?.currentGuanIndex == 169
                || guankaTask?.currentGuanIndex == 138|| guankaTask?.currentGuanIndex == 139) {
                //每按一次，就刷了当前备得木后，再继续刷3秒木，之后会停止并备好一个木，boss放技能，再按键
                doShuaMuOne()
                return true
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
        if (!waiting && isGkOver(guanka)) {
            if (guanka == Guan.g110 && beimu) {//如果beimu时，不waiting，去dealhero里去卡住
                waiting = false
            } else {
                waiting = true
            }
        }
    }


    var beiSQ = false

    /**
     * 110检查一遍星级，以补满卡
     */
    var recheckStarFor110 = false

    override suspend fun dealHero(heros: List<HeroBean?>): Int {
        while (waiting) {
            delay(100)
        }
//        if (!waiting && isGkOver(guanka)) {
//            waiting = true
//        }


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

            index = heros.indexOf(guangqiu)
            if (index > -1) {
                return index
            }
            index = heros.indexOf(nvwang)
            if (index > -1 && nvwang.currentLevel < 3) {
                return index
            }


            index = heros.indexOf(huanqiu)
            if (index > -1 && !Zhuangbei.isLongxin() && Zhuangbei.hasZhuangbei()) {
                return index
            }
            return -1
        } else if (guanka == Guan.g26) {
            var fullList = arrayListOf(nvwang, jiaonv, saman, wangjiang, zhanjiang)//防止没满
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

        } else if (guanka == Guan.g41) {
            var fullList = arrayListOf(shahuang, nvwang, jiaonv, saman, wangjiang, zhanjiang)//防止没满
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
        }else if(guanka == Guan.g91){
            if(wangjiang.isInCar()){
                carDoing.downHero(wangjiang)
            }
            return heros.indexOf(bingqi)
        }
        else if (guanka == Guan.g101) {//101 yandou

            if(bingqi.isInCar()){
                carDoing.downHero(bingqi)
            }

            var fullList = arrayListOf(nvwang, shahuang, saman, zhanjiang, wangjiang, jiaonv)
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
            var fullList = arrayListOf(nvwang, shahuang, saman, zhanjiang, wangjiang, jiaonv,guangqiu, muqiu )
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

            var fullList = arrayListOf(zhanjiang, shahuang, jiaonv, wangjiang, nvwang, saman)
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

                if (beiSQ) {
                    index = heros.indexOf(bingqi)
                    if (index > -1) {

                        log("备了圣骑")
                        while (beiSQ && !XueLiang.isMLess(0.1f)) {
                            delay(50)
                        }

                        log("手动决定上圣骑，或血量低于了10%")
                        beiSQ = false
                        carDoing.downHero(wangjiang)
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


        }
//        else if (guanka == Guan.g131) {
////            if (shahuang.isInCar()) {
////                carDoing.downHero(shahuang)
////            }
//
//
//            //防止船长点完卡没补满
//            var fullList = arrayListOf(zhanjiang, nvwang, shahuang, jiaonv, wangjiang, saman)
////            if (isBaoku()) {
////                fullList.add(bingqi)
////            }
//            var index = defaultDealHero(
//                heros,
//                fullList
//            )
//            if (index > -1) return index
////            index = heros.indexOf(nvwang)
////            if (index > -1 && !nvwang.isInCar()) {//换一星女王，副卡满（副卡18）
////                return index
////            }
//
//            index = heros.indexOf(huanqiu)
//            if (index > -1 && !Zhuangbei.isQiangxi() && Zhuangbei.hasZhuangbei()) {//小翼 烟斗
//                return index
//            }
//        }
        else if (guanka == Guan.g139) {//刷木
//            if (shahuang.isInCar()) {
//                carDoing.downHero(shahuang)
//            }
//            if (nvwang.isInCar()) {
//                carDoing.downHero(nvwang)
//            }
            return heros.indexOf(muqiu)
        } else if (guanka == Guan.g140) {

//            if (saman.isFull()) {
//                carDoing.downHero(saman)//主卡萨满16 ，低星有加成
//            }
//            var index = heros.indexOf(saman)
//            if (index > -1 && !saman.isInCar()) {//换一星女王，副卡满（副卡18）
//                return index
//            }

            var index = heros.indexOf(huanqiu)
            if (index > -1 && !Zhuangbei.isYandou() && Zhuangbei.hasZhuangbei()) {//小翼 烟斗
                return index
            }
        } else if (guanka == Guan.g159) {

            var tempHero = if(BeiBingqiFor159==false) bingqi else jiaonv

            var fullList = arrayListOf(zhanjiang, shahuang, tempHero, wangjiang, nvwang, saman)
//            if (isBaoku()) {
//                fullList.add(bingqi)
//            }
            var index = defaultDealHero(
                heros,
                fullList
            )
            if (index > -1) return index

            index = heros.indexOf(huanqiu)
            log("needZB is $needZhuangbei  cur ZB is ${Zhuangbei.getZhuangBei()}")
            if (index > -1 && Zhuangbei.getZhuangBei() != needZhuangbei) {//小翼 烟斗
                return index
            }
            if (isGkOver(Guan.g159)) {

                if(BeiBingqiFor159 == false){
                    BeiBingqiFor159 = null
                }

                if(BeiBingqiFor159 == true){
                    index = heros.indexOf(bingqi)
                    if(index>-1){
                        while(BeiBingqiFor159 == true){
                            delay(100)
                        }
                        if(jiaonv.isInCar()){
                            carDoing.downHero(jiaonv)
                            delay(100)
                        }
                    }
                    return index
                }
                if(BeiBingqiFor159 == false){
                    index = heros.indexOf(jiaonv)
                    if(index>-1){
                        while(BeiBingqiFor159==false){
                            delay(100)
                        }
                        if(bingqi.isInCar()){
                            carDoing.downHero(bingqi)
                            delay(100)
                        }
                    }
                    return index
                }
                waiting = true
                return -1
            }
        }else if(guanka == Guan.gShuaMuBeiMu){
            if(System.currentTimeMillis()-lastBeishuaMuTime>shumuDuration || shumuDuration<=0){
                beiShuaMu = true
            }
            if(beiShuaMu){
                var index = heros.indexOf(muqiu)
                if(index > -1){
                     while (beiShuaMu && guanka == Guan.gShuaMuBeiMu){//如果guanka已改变就不再卡了，否则这里会永远卡住
                         delay(50)
                     }
                    lastBeishuaMuTime = System.currentTimeMillis()//这里是刷木开始得第一下，用来计算刷多久，比如169，攻击出来就刷木，刷3-4秒后就要备木
                    return index
                }
            }

            return heros.indexOf(muqiu)

        }
        return -1
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

    override fun onWaitingClick() {
        waiting = !waiting
        log("cur waiting is $waiting")
    }

    override fun onLongWangZsClick() {
    }

    override fun onLongWangFsClick() {
        //如果反应慢，点的慢，内部也是计时10s上回，如果慢了的话，可以主动改变waiting，通过UI面板或者快捷键9
        longWangDownLoadPositionFromKey = carDoing.carps.indexOfFirst {
            it.mHeroBean == shahuang
        }
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