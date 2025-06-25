package tasks.hanbing.huoling

import MainData
import data.*
import kotlinx.coroutines.*
import log
import tasks.XueLiang
import tasks.Zhuangbei
import tasks.hanbing.BaseHBHeroDoing
import ui.zhandou.UIKeyListenerManager
import java.awt.event.KeyEvent
import java.awt.event.KeyEvent.*

class HBHeroDoingBoHuo : BaseHBHeroDoing(),
    UIKeyListenerManager.UIKeyListener {//默认赋值0，左边，借用左边第一个position得点击，去识别车位置后再更改
//es8  es4 ye5   6e0 y80  y50 y56 e04

    val huoling = HeroBean("huoling", 100)
    val shuiling = HeroBean("shuiling", 90)
    val niutou = HeroBean("niutou2", 20)
    val tieqi = HeroBean("tieqi", 80)
    val xiongmao = HeroBean("xiongmao", 60, compareRate = 0.9)
    val wangjiang = HeroBean("wangjiang2", 50)
    val daoke = HeroBean("daoke", 70)
    val huanqiu = HeroBean("huanqiu", 0, needCar = false)
    val muqiu = HeroBean("muqiu", 40, needCar = false, compareRate = 0.95)
    val haiyao = HeroBean("haiyao", 30)


    enum class Guan(val des: String? = null) {
        /**
         *  满火灵，水灵，牛头，铁骑，熊猫，海妖在车上
         */
        g1("满火灵，水灵，亡将，铁骑，熊猫，海妖在车上"),

        /**
         * 龙心,下海妖，上刀客
         */
        g11("龙心, 下海妖，上刀客"),

        /**
         * 强袭
         */
        g71("强袭"),

        /**
         * 无脑补卡
         */
        g108("无脑补卡"),

        /**
         * 补满卡
         */
        g110("下亡将，上电法，船长时可以按小键盘2154870进行点名下卡，按战车位置进行对应，0代表工程位"),

        /**
         * 备木,掉血，补木
         */
        g139("备木,掉血，补木"),

        /**
         * 下亡将，上海妖，烟斗
         */
        g141("下亡将，上海妖，烟斗"),

        /**
         * 下火灵，红球上，蓝球下
         */
        g149("下火灵，红球上，蓝球下,快捷键 红球按1，蓝球按2，6球结束按3"),

        /**
         * 下海妖，上亡将，强袭
         */
        g151("下海妖，上亡将,强袭"),



        /**
         * 下刀客，上牛头
         */
        g171("下刀客，上牛头"),

        /**
         * 下亡将，上刀客,龙心
         */
        g181("下亡将，上刀客,龙心"),

        /**
         * 补星,补木
         */
        g189("补星,补木,按0切换补星为补木"),

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

    var lanqiuCount = 0
    override fun onLeiShenBlueBallShow() {
        lanqiuCount ++
        curQiu149 = 2
        super.onLeiShenBlueBallShow()
    }

    override fun onLeiShenRedBallShow() {
        if(lanqiuCount==3){
            curQiu149 = 3
        }else {
            curQiu149 = 1
        }
        super.onLeiShenRedBallShow()
    }

    override fun onLeiShenSixBallOver() {
        super.onLeiShenSixBallOver()
        if(curQiu149!=3) {
            GlobalScope.launch {
                //因为识别到第6个球就回调了。所以这里再延迟5秒再刷木
                log("六个球已经识别完毕，延迟8秒后开始满火灵")
                delay(10000)
                curQiu149 = 3
            }
        }
    }

    var time179Fanshang = 0L

    override fun doOnGuanChanged(guan: Int) {

        if (guan == 189) {
            time179Fanshang = System.currentTimeMillis()
            guanka = Guan.g189
            waiting = false
            return
        }

        if (guan == 181) {
            guanka = Guan.g181
            waiting = false
            return
        }

        if (guan == 171) {
            guanka = Guan.g171
            waiting = false
            return
        }

        if (guan == 151) {
//            App.stopAutoSave()
            guanka = Guan.g151
            waiting = false
            return
        }
        if (guan == 149) {
//            App.stopAutoSave()
            GlobalScope.launch {
                carDoing.downHero(huoling)
                guanka = Guan.g149
                waiting = false
            }

            return
        }
        if (guan == 141) {
//            App.stopAutoSave()
            guanka = Guan.g141
            waiting = false
            return
        }

        if (guan == 131) {
            stopChuanZhangOberserver()
            beimu = false
            waiting = false
            return
        }

        if (guan == 128) {
            startChuanZhangOberserver()
            return
        }
        if (guan == 110) {
            GlobalScope.launch {
                carDoing.downHero(wangjiang)
                guanka = Guan.g110
                waiting = false
            }
            return
        }

        if (guan == 109) {
            guanka = Guan.g108
            waiting = false
            return
        }
        if (guan == 71) {
            guanka = Guan.g71
            waiting = false
            return
        }
        if (guan == 11) {
            guanka = Guan.g11
            waiting = false
            return
        }

    }

    override fun onHeroPointByChuanzhang(hero: HeroBean): Boolean {
        //船长不会点战将，这里除非是识别错了，如果识别错了，肯定也不下战将（目前只有另一个车中了，主车会偶尔识别到战将，这种就不下战将，其他都先不处理）
        return true
    }

    override suspend fun onLongwangPoint(point: MPoint, downed: (Boolean) -> Unit) {
    }


    fun isGkOver(g: Guan): Boolean {

        if (g == Guan.g1) return huoling.isFull() && xiongmao.isFull() && wangjiang.isFull() && shuiling.isFull() && tieqi.isFull() && haiyao.isInCar()

        if(g == Guan.g149) return huoling.isFull()

        var heroOk = huoling.isFull() && shuiling.isFull() && tieqi.isFull() && xiongmao.isFull()
        if (!heroOk) return false

        return when (g) {

            Guan.g11 -> daoke.isFull() && wangjiang.isFull() && Zhuangbei.isLongxin()

            Guan.g71 -> Zhuangbei.isQiangxi()

            Guan.g108 -> {//无脑补卡，这时降星 是不知道星级的
                false
            }

            Guan.g110 -> {
                daoke.isFull() && wangjiang.isFull()
            }

            Guan.g139 -> false

            Guan.g141 -> {
                haiyao.isFull() && Zhuangbei.isYandou()
            }

            Guan.g151 -> {
                wangjiang.isFull() && Zhuangbei.isQiangxi()
            }

            Guan.g171 -> niutou.isFull()
            Guan.g181 -> daoke.isFull() && Zhuangbei.isLongxin()
            Guan.g189 -> false
            else -> false
        }
    }


    override fun initHeroes() {
        heros = arrayListOf()
        heros.add(huoling)
        heros.add(shuiling)
        heros.add(tieqi)
        heros.add(daoke)
        heros.add(xiongmao)
        heros.add(wangjiang)
        heros.add(muqiu)
        heros.add(haiyao)
        heros.add(niutou)
        heros.add(huanqiu)
    }

    suspend fun doOnKeyDown(code: Int): Boolean {
        if (guankaTask?.currentGuanIndex == 189 ||guankaTask?.currentGuanIndex == 188 ) {//船长
            if(code==KeyEvent.VK_NUMPAD0) {
                var time = System.currentTimeMillis() - time179Fanshang
                log("time189降星时长:$time")
                guanka = Guan.g139
                waiting = false
            }
        }

        if (guankaTask?.currentGuanIndex == 149 ||guankaTask?.currentGuanIndex == 148 ) {//船长
            if(code==KeyEvent.VK_NUMPAD1) {
                curQiu149 = 1
            }else if(code == VK_NUMPAD2){
                curQiu149 = 2
            }else if(code == VK_NUMPAD3){
                curQiu149 = 3
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
            if(haiyao.isInCar()){
                return defaultDealHero(heros, arrayListOf(huoling,shuiling,xiongmao,tieqi,wangjiang,haiyao))
            }else{
                return defaultDealHero(heros, arrayListOf(haiyao,huoling,shuiling,xiongmao,tieqi,wangjiang))
            }
            return -1
        } else if (guanka == Guan.g11) {
            carDoing.downHero(haiyao)
            var index = defaultDealHero(heros, arrayListOf(huoling,shuiling,xiongmao,tieqi,wangjiang,daoke))
            if(index>-1){
                return index
            }
            index = heros.indexOf(huanqiu)
            if (index > -1 && !Zhuangbei.isLongxin() && Zhuangbei.hasZhuangbei()) {
                return index
            }
            return -1
        } else if (guanka == Guan.g71) {
            var index = heros.indexOf(huanqiu)
            if (index > -1 && !Zhuangbei.isQiangxi() && Zhuangbei.hasZhuangbei()) {
                return index
            }
            return -1
        } else if (guanka == Guan.g108) {//乱补
            var fullList = arrayListOf(shuiling, xiongmao, tieqi, huoling, wangjiang, daoke, muqiu)
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

            var fullList = arrayListOf(huoling, xiongmao, wangjiang, daoke, shuiling, tieqi)
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
                        while (!XueLiang.isMLess(0.5f) && beimu && guanka == Guan.g110) {//这里如果不加&& beimu，如果副卡打了木，血量一直不低于40，那么这里就会一直卡着，船长监听那里，也会在适当时机把beimu改成false，这样这里就会扔出去了
                            delay(50)
                        }
                        log("血量低于50%，使用木")
                        beimu = false //变成false，用木后，判断needwaiting就是true了，就不会刷完木又来刷卡
                        return index
                    }
                }
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

        } else if (guanka == Guan.g141) {
            carDoing.downHero(wangjiang)
            var index = heros.indexOf(haiyao)
            if(index>-1)return index
            index = heros.indexOf(huanqiu)
            if (index > -1 && !Zhuangbei.isYandou() && Zhuangbei.hasZhuangbei()) {
                return index
            }
            return -1
        }else if(guanka == Guan.g149){

            while(guanka == Guan.g149){
                if(curQiu149 == 2){
                    carDoing.downHero(huoling)
                    if(heros.indexOf(huoling)>-1){//下了火灵后，备选个火灵,是火灵就卡在这里，不是的话反-1，继续识别卡
                        delay(100)
                    }else return -1
                }else if(curQiu149 == 1){
                    if(!huoling.isInCar()){
                        return heros.indexOf(huoling)
                    }else{
                        delay(100)
                    }
                }else {
                    if(!huoling.isFull()){
                        return heros.indexOf(huoling)
                    }
                }

            }
        }
        else if (guanka == Guan.g151) {
            carDoing.downHero(haiyao)
            var index = defaultDealHero(heros, arrayListOf(wangjiang,huoling))
            if(index>-1)return index
            index = heros.indexOf(huanqiu)
            if (index > -1 && !Zhuangbei.isQiangxi() && Zhuangbei.hasZhuangbei()) {
                return index
            }
            return -1
        } else if (guanka == Guan.g171) {
            carDoing.downHero(daoke)
            return heros.indexOf(niutou)
        }else if(guanka == Guan.g181){
            carDoing.downHero(wangjiang)
            var index = heros.indexOf(daoke)
            if(index>-1)return index
            index = heros.indexOf(huanqiu)
            if (index > -1 && !Zhuangbei.isLongxin() && Zhuangbei.hasZhuangbei()) {
                return index
            }
            return -1
        }else if(guanka == Guan.g189){
            while(guanka == Guan.g189){
                if(carDoing.hasOpenSpace() || carDoing.hasNotFull()){
                    return defaultDealHero(heros, arrayListOf(huoling,tieqi,niutou,shuiling,wangjiang,xiongmao,huanqiu))
                }
                carDoing.reCheckStars()
                if(carDoing.hasOpenSpace() || carDoing.hasNotFull()){
                    return defaultDealHero(heros, arrayListOf(huoling,tieqi,niutou,shuiling,wangjiang,xiongmao,huanqiu))
                }
            }
        }
        return -1
    }

    var curQiu149 = 2//1红，2蓝

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