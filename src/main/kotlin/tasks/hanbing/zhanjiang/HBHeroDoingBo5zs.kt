package tasks.hanbing.zhanjiang

import MainData
import data.*
import kotlinx.coroutines.*
import log
import tasks.XueLiang
import tasks.hanbing.BaseHBHeroDoing
import ui.zhandou.UIKeyListenerManager
import java.awt.event.KeyEvent
import java.awt.event.KeyEvent.*

class HBHeroDoingBo5zs : BaseHBHeroDoing(),
    UIKeyListenerManager.UIKeyListener {//默认赋值0，左边，借用左边第一个position得点击，去识别车位置后再更改
//es8  es4 ye5   6e0 y80  y50 y56 e04

    val zhanjiang = HeroBean("zhanjiang2", 100)
    val saman = HeroBean("saman2", 90)
    val niutou = HeroBean("niutou2", 20)
    val tieqi = HeroBean("tieqi", 80)
    val dianfa = HeroBean("dianfa", 60, compareRate = 0.9)
    val wangjiang = HeroBean("wangjiang2", 50)
    val kuangjiang = HeroBean("kuangjiang", 70)
    val guangqiu = HeroBean("guangqiu", 0, needCar = false)
    val muqiu = HeroBean("muqiu", 40, needCar = false, compareRate = 0.95)
    val haiyao = HeroBean("haiyao", 30)


    enum class Guan(val des: String? = null) {
        /**
         *  满战将
         */
        g1("满战将"),

        /**
         * 备海妖，上电法
         */
        g2("备海妖，上电法"),

        /**
         * 上牛头，铁骑，萨满，亡将满，上狂将不满
         */
        g3("上牛头，铁骑，萨满，亡将满，上狂将不满"),

        /**
         * 满狂将
         */
        g4("满狂将"),

        /**
         * 无脑补卡
         */
        g108("无脑补卡"),

        /**
         * 下亡将，上电法，船长时可以按小键盘2154870进行点名下卡，按战车位置进行对应，0代表工程位
         */
        g110("下亡将，上电法，船长时可以按小键盘2154870进行点名下卡，按战车位置进行对应，0代表工程位"),

        /**
         * 下电法，上亡将,
         */
        g131("下电法，上亡将"),

        /**
         *  一直刷木
         */
        g139("一直刷木，监控血量低于90%，会自动刷，也可以按3进行释放备好的木刷木"),

        /**
         * 上海妖，下亡将
         */
        g151("上海妖，下亡将"),

        /**
         * 下海妖，上亡将
         */
        g161("下海妖，上亡将"),

        /**
         * 下亡将，上电法
         */
        g171("下亡将，上电法"),

        /**
         * 下狂将，上亡将,补木
         */
        g179("下狂将，上亡将,补木"),

        /**
         * 下亡将，上狂将
         */
        g181("下亡将，上狂将"),

        /**
         * 下狂将，上亡将,补星
         */
        g189("狂将，上亡将,补星"),

        /**
         * 下亡将，上狂将
         */
        g191("下亡将，上狂将"),

        /**
         * 下狂将，上亡将,检测下卡，上卡补木，电法优先满，其他先补木
         */
        g199("下狂将，上亡将,检测下卡，上卡补木，电法优先满，其他先补木"),
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

    override fun onLeiShenBlueBallShow() {
        super.onLeiShenBlueBallShow()
    }

    override fun onLeiShenSixBallOver() {
        super.onLeiShenSixBallOver()
//        GlobalScope.launch {
//            //因为识别到第6个球就回调了。所以这里再延迟5秒再刷木
//            log("六个球已经识别完毕，延迟8秒后开始刷木")
//            delay(11000)
//            guanka = Guan.g139
//            waiting = false
//        }
    }

    var time179Fanshang = 0L

    override fun doOnGuanChanged(guan: Int) {

//        if (guan == 159) {
//            guanka = Guan.g159
//            startXiongMaoOberser()
//            App.startAutoSave()
//            waiting = false
//            return
//        }
        if (guan == 199) {
//            App.startAutoSave()
            guanka = Guan.g199
            needCheckQian = false//199钱肯定多，节省识别钱的时间
            carDoing.downCardSpeed = true
            waiting = false
            return
        }
        if (guan == 191) {
            guanka = Guan.g191
            waiting = false
            return
        }
        if (guan == 189) {
            guanka = Guan.g189
            waiting = false
            return
        }
        if (guan == 181) {
            guanka = Guan.g181
            waiting = false
            return
        }

        if (guan == 179) {
            time179Fanshang = System.currentTimeMillis()
            guanka = Guan.g179
            waiting = false
            return
        }

        if (guan == 171) {
            guanka = Guan.g171
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
            guanka = Guan.g151
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

//        if (guan == 101) {
//            waiting = false
//            return
//        }

        if (guan == 47) {
            guanka = Guan.g4
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
//        downed.invoke(false)
//        when (point) {
//            Config.hbFSCloud -> {
//                carDoing.downHero(dianfa)
//                downed.invoke(true)
//            }
//
//            else -> {
//                downed.invoke(false)
//            }
//        }
    }


    fun isGkOver(g: Guan): Boolean {

        if (g == Guan.g1) return zhanjiang.isFull()
        if (g == Guan.g2) return haiyao.isInCar() && dianfa.isInCar()

        var heroOk = zhanjiang.isFull() && saman.isFull() && tieqi.isFull() && niutou.isFull()
        if (!heroOk) return false

        return when (g) {

            Guan.g3 -> kuangjiang.isInCar() && wangjiang.isFull()
            Guan.g4 -> kuangjiang.isFull() && wangjiang.isFull()


            Guan.g108 -> {//无脑补卡，这时降星 是不知道星级的
                false
            }

            Guan.g110 -> {
                kuangjiang.isFull() && dianfa.isFull()
            }

            Guan.g131 -> {
                kuangjiang.isFull() && wangjiang.isFull()
            }

            Guan.g139 -> false
            Guan.g151 -> {
                haiyao.isFull()
            }

            Guan.g161 -> wangjiang.isFull()
            Guan.g171 -> dianfa.isFull()
            Guan.g179 -> false
            Guan.g181 -> kuangjiang.isFull()
            Guan.g189 -> false
            Guan.g191 -> kuangjiang.isFull()
            Guan.g199 -> false
            else -> false
        }
    }


    override fun initHeroes() {
        heros = arrayListOf()
        heros.add(zhanjiang)
        heros.add(saman)
        heros.add(tieqi)
        heros.add(kuangjiang)
        heros.add(dianfa)
        heros.add(wangjiang)
        heros.add(muqiu)
        heros.add(haiyao)
        heros.add(niutou)
        heros.add(guangqiu)
    }

    var position199 = -1
    suspend fun doOnKeyDown(code: Int): Boolean {
        if (guankaTask?.currentGuanIndex == 179 ||guankaTask?.currentGuanIndex == 178 ) {//船长
            if(code==KeyEvent.VK_NUMPAD0) {
                var time = System.currentTimeMillis() - time179Fanshang
                log("time179Fanshang:$time")
            }
        }

        if (guankaTask?.currentGuanIndex == 199 ||guankaTask?.currentGuanIndex == 198 ) {//船长
            position199 = when (code) {
                KeyEvent.VK_NUMPAD2 -> 0
                KeyEvent.VK_NUMPAD1 -> 1
                KeyEvent.VK_NUMPAD5 -> 2
                KeyEvent.VK_NUMPAD4 -> 3
                KeyEvent.VK_NUMPAD8 -> 4
                KeyEvent.VK_NUMPAD7 -> 5
                KeyEvent.VK_NUMPAD0 -> 6
               else -> -1
            }

            if(position199>-1){
//                carDoing.downPosition(position199)
                loop199 = false
                return true
            }
        }




        if (code == VK_NUMPAD1 || code == VK_1) {//按1刷木
            guanka = Guan.g139
            waiting = false
        }

        if (code == VK_NUMPAD3) {

            if (guanka == Guan.g139 || muBeiLe) {
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
            if(!haiyao.isInCar()) {
                var index = heros.indexOf(haiyao)
                if (index > -1) {
                    while (beiNvWang) {
                        delay(20)
                    }
                    return index
                }
            }else{
                return defaultDealHero(heros, arrayListOf(dianfa,haiyao))
            }
            return -1
        } else if (guanka == Guan.g3) {
            carDoing.downHero(haiyao)
            carDoing.downHero(dianfa)
            var index = defaultDealHero(heros, arrayListOf(niutou, tieqi, saman, wangjiang))
            if (index > -1) {
                return index
            }
            index = heros.indexOf(guangqiu)
            if (index > -1) {
                if (carDoing.hasNotFullExcepte(kuangjiang)) {
                    carDoing.downHero(kuangjiang)
                    return index
                }
            }
            if (kuangjiang.currentLevel < 3) {
                return heros.indexOf(kuangjiang)
            }
        } else if (guanka == Guan.g4) {
            return defaultDealHero(heros, arrayListOf(kuangjiang, guangqiu))
        } else if (guanka == Guan.g108) {//乱补
            var fullList = arrayListOf(saman, niutou, tieqi, zhanjiang, wangjiang, kuangjiang, guangqiu, muqiu)
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

        } else if (guanka == Guan.g110) {//111

            if (!recheckStarFor110) {
                carDoing.reCheckStars()
                recheckStarFor110 = true
            }

            carDoing.downHero(wangjiang)

            var fullList = arrayListOf(zhanjiang, dianfa, niutou, kuangjiang, saman, tieqi)
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


        } else if (guanka == Guan.g131) {
            //防止船长点完卡没补满
            carDoing.downHero(dianfa)
            var fullList = arrayListOf(zhanjiang, saman, wangjiang, kuangjiang, niutou, tieqi, guangqiu)
            var index = defaultDealHero(
                heros,
                fullList
            )
            if (index > -1) return index

        } else if (guanka == Guan.g139) {//刷木
            var index = heros.indexOf(muqiu)
            if (index > -1) {
                muBeiLe = true
                while (guanka == Guan.g139 && (!XueLiang.isMLess(0.9f) || !muBeiLe)) {
                    delay(50)
                }
                return index
            }

        } else if (guanka == Guan.g151) {
            carDoing.downHero(wangjiang)
            var index = defaultDealHero(heros, arrayListOf(haiyao, guangqiu))
            if (index > -1) {
                return index
            }
            //159会根据管卡改变将当前设置为139行为
        } else if (guanka == Guan.g161) {

            carDoing.downHero(haiyao)
            return defaultDealHero(heros, arrayListOf(wangjiang,guangqiu))

        } else if (guanka == Guan.g171) {
            carDoing.downHero(wangjiang)
            var index = defaultDealHero(heros, arrayListOf(dianfa, guangqiu))
            if (index > -1) {
                return index
            }
        }else if(guanka == Guan.g179){
            carDoing.downHero(kuangjiang)
            var index = heros.indexOf(muqiu)
            if(wangjiang.isFull()){//亡将满时，只刷木
                if (index > -1) {
                    muBeiLe = true
                    while (guanka == Guan.g179 && !XueLiang.isMLess(0.9f) && muBeiLe) {
                        delay(50)
                    }
                    return index
                }
            }else{
                if(index>-1 && XueLiang.isMLess(0.9f)){//血量不够先补血
                    return index
                }else{
                    return defaultDealHero(heros, arrayListOf(wangjiang,guangqiu))
                }
            }
        }else if(guanka == Guan.g181){
            carDoing.downHero(wangjiang)
            return defaultDealHero(heros, arrayListOf(kuangjiang,guangqiu))
        }else if(guanka == Guan.g189){
            carDoing.downHero(kuangjiang)
            var mu = heros.indexOf(muqiu)
            while(guanka == Guan.g189){
                //每次循环都检测血量，血量低优先刷木
                if(XueLiang.isMLess(0.9f) ){
                    if(mu>-1) {
                        return mu
                    }
                    //如果没木就补星，没有就刷牌了
                    return defaultDealHero(heros, arrayListOf(zhanjiang,tieqi,niutou,saman,wangjiang,dianfa,guangqiu))
                }
                if(carDoing.hasOpenSpace() || carDoing.hasNotFull()){
                    return defaultDealHero(heros, arrayListOf(zhanjiang,tieqi,niutou,saman,wangjiang,dianfa,guangqiu))
                }
                carDoing.reCheckStars()
                if(carDoing.hasOpenSpace() || carDoing.hasNotFull()){
                    return defaultDealHero(heros, arrayListOf(zhanjiang,tieqi,niutou,saman,wangjiang,dianfa,guangqiu))
                }
            }
        }else if(guanka == Guan.g191){
            carDoing.downHero(wangjiang)
            return defaultDealHero(heros, arrayListOf(kuangjiang,guangqiu))
        }else if(guanka == Guan.g199){

            carDoing.downHero(kuangjiang)
            //优先看一次点名
            var dianmingIndex = carDoing.getHB199Selected()
            if(position199>-1 || dianmingIndex>-1) {
                carDoing.downPosition(position199)
                carDoing.downPosition(dianmingIndex)
                position199 = -1
            }
//            var mu = heros.indexOf(muqiu)
//            if(XueLiang.isMLess(0.5f) && mu>-1){
//                return mu
//            }
            //电法没满，优先满，否则小野不触发就扛不住,//牛不满也特么扛不住
            if(!dianfa.isFull()||!niutou.isFull()){
                var index = defaultDealHero(heros, arrayListOf(dianfa,niutou,guangqiu))
                if(index>-1){
                    return index
                }
            }
           var mu = heros.indexOf(muqiu)
            if(XueLiang.isMLess(0.8f) && mu>-1){
                return mu
            }

            if(carDoing.hasOpenSpace() || carDoing.hasNotFull()){
                return defaultDealHero(heros, arrayListOf(zhanjiang,tieqi,niutou,saman,wangjiang,dianfa,guangqiu))
            }else{
                loop199 = true
                //如果都满着呢，就只等点名和刷木
                while(loop199){
                    if(XueLiang.isMLess(0.8f)){
                        return mu
                    }
                    var dianmingIndex = carDoing.getHB199Selected()
                    if(position199>-1 || dianmingIndex>-1){
                        carDoing.downPosition(position199)
                        carDoing.downPosition(dianmingIndex)
                        position199 = -1
                        return defaultDealHero(heros, arrayListOf(zhanjiang,tieqi,niutou,saman,wangjiang,dianfa,guangqiu))
                    }
                    delay(50)
                }
            }

        }
        return -1
    }

    var loop199 = false

    var muBeiLe = false

    override fun changeHeroWhenNoSpace(heroBean: HeroBean): HeroBean? {
        return null
    }

    override suspend fun onKeyDown(code: Int): Boolean {

        if (guankaTask?.currentGuanIndex == 129 ||guankaTask?.currentGuanIndex == 128 ) {//船长
            var position199 = when (code) {
                KeyEvent.VK_NUMPAD2 -> 0
                KeyEvent.VK_NUMPAD1 -> 1
                KeyEvent.VK_NUMPAD5 -> 2
                KeyEvent.VK_NUMPAD4 -> 3
                KeyEvent.VK_NUMPAD8 -> 4
                KeyEvent.VK_NUMPAD7 -> 5
                KeyEvent.VK_NUMPAD0 -> 6
                else -> -1
            }

            if(position199>-1){
                carDoing.downPosition(position199)
                waiting = false
                return true
            }
        }

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