package tasks.anyue.zhanjiang

import MainData
import MainData.job79TimeDt
import androidx.compose.runtime.mutableStateOf
import data.*
import getImage
import kotlinx.coroutines.*
import log
import tasks.XueLiang
import tasks.Zhuangbei
import ui.zhandou.UIKeyListenerManager
import utils.AYUtil
import utils.GuDingShuaKaUtil
import java.awt.event.KeyEvent
import java.awt.event.KeyEvent.*
import java.awt.image.BufferedImage
import kotlin.math.abs

class AYHeroDoingBo : BaseAnYueHeroDoing(),
    UIKeyListenerManager.UIKeyListener {//默认赋值0，左边，借用左边第一个position得点击，去识别车位置后再更改
//es8  es4 ye5   6e0 y80  y50 y56 e04

    val houyi = HeroBean("houyi", 100)
    val tieqi = HeroBean("tieqi", 90)
    val yuren = HeroBean("yuren", 80)
    val dianfa = HeroBean("dianfa", 70)
    val tuling = HeroBean("tuling", 60)
    val dapao = HeroBean("dapao", 30, needCar = true, isGongCheng = true, compareRate = 0.9)
    val shitou = HeroBean("shitou", 30)
    val dijing = HeroBean("dijing", 30)
    val niutou = HeroBean("niutou2", 20)
    val muqiu = HeroBean("muqiu", 0, needCar = false)

    //副卡 底层 石头，萨满 其他：女王 小野 死神 亡将 射线（宝库）地精 光球 魔球

    enum class Guan(val des: String? = null) {
        /**
         * 先铁骑石头放下面，然后除了地精土灵，其他全满
         */
        g1("先铁骑石头放下面，然后除了地精土灵，其他全满"),

        /**
         * 下备地精
         */
        g17("备地精"),

        /**
         * 19关开识别,开识别，上下地精操作
         */
//        g19("开识别，上下地精操作"),

        /**
         * 除了地精土灵，其他全满
         */
        g21("除了地精土灵，其他全满"),

        g37("39识别，按键可以下卡"),

        /**
         * 除了地精土灵，其他全满
         */
        g40("除了地精土灵，其他全满"),
        /**
         * 39，小键盘点啥下啥，按3上回
         */

        /**
         * 101，下鱼人上土灵
         */
        g101("下鱼人上土灵"),

        /**
         * 109
         */
        g109("下后裔上鱼人，刷血"),

        /**
         * 101，下鱼人上土灵
         */
        g111("下鱼人上后裔"),

        /**
         * 119_1，从这里开始，就是该下下，该上上，否则就观察血量刷木，都是waitting false，isOver 也false
         */
        g119("下后裔大炮，上鱼人，"),

        /**
         *
         */
        g121("满上满上")

    }

    var g1191State = 1

    private fun changeGuanKa(guan: Int, aim: Int, changeTo: Guan, block: (() -> Unit)? = null) {
        if (guanka == changeTo) {
            throw Exception()
        }
        if (guan >= aim && guanka != changeTo) {
            guanka = changeTo
            waiting = false
            block?.invoke()
            throw Exception()
        }
    }

    override fun doOnGuanChanged(guan: Int) {

        try {
            changeGuanKa(guan, 120, Guan.g121)
            changeGuanKa(guan, 119, Guan.g119)
            changeGuanKa(guan, 110, Guan.g111)
            changeGuanKa(guan, 109, Guan.g109)
            changeGuanKa(guan, 101, Guan.g101)

            changeGuanKa(guan, 40, Guan.g40)
            changeGuanKa(guan, 39, Guan.g37)

            changeGuanKa(guan, 20, Guan.g21) {
                //            App.stopAutoSave()
                stop19Oberserver()
            }

            if (guan == 19) {
                start19Oberserver(true)
                return
            }
            changeGuanKa(guan, 18, Guan.g17)
        } catch (e: Exception) {

        }

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


    override fun onHeroPointByG19(hero: HeroBean): Boolean {//点战将无敌抗，点两次认输。。。
        return true
    }

    var gk1Over = false
    var gk39ModelVersion = 2//使用版本2，稳定，等1测试ok后再切1
    fun isGkOver(g: Guan): Boolean {

        return when (g) {
            Guan.g1 -> {
//                if (gk1Over) true
//                else (dijing.isFull() && jiaonv.isFull() && niutou.isFull() && nvwang.isInCar() && saman.isFull() && baoku.isFull()).apply {
//                    gk1Over = this
//                }
                tieqi.isFull() && shitou.isFull() && houyi.isFull() && dianfa.isFull() && yuren.isFull() && dapao.isFull() && niutou.isFull()
            }

            Guan.g17 -> {
                //17要处理地精，这里不能返回true，一直false
                false
            }

            Guan.g21, Guan.g40 -> {
                tieqi.isFull() && shitou.isFull() && houyi.isFull() && dianfa.isFull() && yuren.isFull() && dapao.isFull() && niutou.isFull()
            }

            Guan.g37 -> if (gk39ModelVersion == 1) false else (dianfa.isInCar() && !houyi.isInCar() && !yuren.isInCar() && !niutou.isInCar())

            Guan.g101 -> {
                tieqi.isFull() && shitou.isFull() && houyi.isFull() && dianfa.isFull() && tuling.isFull() && dapao.isFull() && niutou.isFull()
            }

            Guan.g109 -> {//这里后面也要一直观察木
//                tieqi.isFull() && shitou.isFull() && tuling.isFull() && dianfa.isFull() && yuren.isFull() && dapao.isFull() && niutou.isFull()
                false
            }

            Guan.g111 -> {
                tieqi.isFull() && shitou.isFull() && houyi.isFull() && dianfa.isFull() && tuling.isFull() && dapao.isFull() && niutou.isFull()
            }

            Guan.g121 -> {
                tieqi.isFull() && shitou.isFull() && houyi.isFull() && dianfa.isFull() && tuling.isFull() && dapao.isFull() && niutou.isFull()
            }
//            Guan.g119_1 -> {
//                tieqi.isFull()&&shitou.isFull()&&tuling.isFull()&&dianfa.isFull()&&yuren.isFull()&&niutou.isFull()
//            }
//            Guan.g119_5 ->{
//                !niutou.isInCar()
//            }
//            Guan.g119_6 ->{
//                niutou.isFull()
//            }
//            Guan.g119_7 ->{
//                dapao.isFull()
//            }
//            Guan.g119_9 ->{
//                !niutou.isInCar() && !dapao.isInCar()
//            }

            else -> false
        }
    }


    override fun initHeroes() {
        heros = arrayListOf()
        heros.add(shitou)
        heros.add(tieqi)
        heros.add(niutou)
        heros.add(houyi)
        heros.add(yuren)
        heros.add(tuling)
        heros.add(dijing)
        heros.add(dapao)
        heros.add(dianfa)
        heros.add(muqiu)
    }

    private suspend fun click39V1(code: Int): Boolean {
        DownLoadPositionFromKeyFor39 = -1
        DownLoadPositionFromKeyFor39 = when (code) {
            KeyEvent.VK_NUMPAD2 -> 0
            KeyEvent.VK_NUMPAD1 -> 1
            KeyEvent.VK_NUMPAD5 -> 2
            KeyEvent.VK_NUMPAD4 -> 3
            KeyEvent.VK_NUMPAD8 -> 4
            KeyEvent.VK_NUMPAD7 -> 5
            KeyEvent.VK_NUMPAD0 -> 6
            else -> {
                return false
            }
        }

        if (DownLoadPositionFromKeyFor39 > -1) {
            down39(null, arrayListOf(DownLoadPositionFromKeyFor39))
        }
        return true
    }

    private suspend fun click39V2(code: Int): Boolean {
        if (code == VK_NUMPAD3) {
            GlobalScope.launch {
                if (dianfa.isInCar()) {//女王在车上，下女王，备女王
                    waiting = true
                    carDoing.downHero(dianfa)
                    preHero(dianfa)
                    waiting = false
                } else {//不在车上，上女王
                    preHero(null)
                }
            }
        }
        return true
    }

    suspend fun doOnKeyDown(code: Int): Boolean {


        if (code == VK_NUMPAD3 && (guanka == Guan.g109 || guanka.name.startsWith("g119"))) {
            muBeiLe = false
            return true
        }

        if (guankaTask?.currentGuanIndex == 39 || guankaTask?.currentGuanIndex == 38
//            || guankaTask?.currentGuanIndex == 49 || guankaTask?.currentGuanIndex == 48
        ) {//19


//            return click39V1(code)
            return if (gk39ModelVersion == 1) click39V1(code) else click39V2(code)
        }
        if (guankaTask?.currentGuanIndex == 119 || guankaTask?.currentGuanIndex == 118
//            || guankaTask?.currentGuanIndex == 49 || guankaTask?.currentGuanIndex == 48
        ) {//19
            muBeiLe = false
            when (code) {
                VK_NUMPAD5 -> {
                    g1191State = 5
                }

                VK_NUMPAD6 -> {
                    g1191State = 6
                }

                VK_NUMPAD7 -> {
                    g1191State = 7
                }

                VK_NUMPAD8 -> {
                    g1191State = 8
                }

                VK_NUMPAD9 -> {
                    g1191State = 9
                }
            }

            return true
        }

        return true
    }


    override suspend fun doAfterHeroBeforeWaiting(heroBean: HeroBean) {
        if (heroBean.heroName == "muqiu") {
            return
        }
        if (!waiting && isGkOver(guanka)) {
            waiting = true
        }
    }

    var muBeiLe = false
    override suspend fun dealHero(heros: List<HeroBean?>): Int {
        while (waiting) {
            delay(100)
        }
//        if (!waiting && isGkOver(guanka)) {
//            waiting = true
//        }

        if (guanka == Guan.g1) {//第一阶段

            if (!tieqi.isInCar() || !shitou.isInCar()) {//石头铁骑放下面一行
                if (!tieqi.isInCar()) {
                    var index = heros.indexOf(tieqi)
                    if (index > -1) return index
                }
                if (!shitou.isInCar()) {
                    var index = heros.indexOf(shitou)
                    if (index > -1) return index
                }
                return defaultDealHero(heros, arrayListOf(tieqi, shitou))
            } else {
                return defaultDealHero(heros, arrayListOf(houyi, dianfa, tieqi, niutou, shitou, yuren, dapao))
            }

        } else if (guanka == Guan.g17) {
            if (!gk1Over) {
                var r =
                    tieqi.isFull() && shitou.isFull() && houyi.isFull() && dianfa.isFull() && yuren.isFull() && dapao.isFull() && niutou.isFull()
                if (r) {
                    gk1Over = true
                }
                return defaultDealHero(heros, arrayListOf(houyi, dianfa, tieqi, niutou, shitou, yuren, dapao))
            }

            var curG = guankaTask?.currentGuanIndex ?: 0

            if (dijing.isInCar()) {
                log("g17:地精在车上")
                heros.filter {
                    it != null && it.needCar
                }.ifEmpty {
                    return -1
                }.apply {

                    firstOrNull { !it!!.isInCar() }?.let {
                        log("找到${it.heroName} 不在车上，下地精 上${it.heroName} ,备地精")
                        while (dijingIng && curG < 20) {//加管卡，避免卡死。超过20关就不会再卡了
                            delay(50)
                            curG = guankaTask?.currentGuanIndex ?: 0
                        }
                        if (curG >= 20) {
                            return -1//如果超过20关，就直接返回-1，否则这会base里不会下19点名牌（boss结束了），就没空位，这里返回地精就上不去。。。。
                        }
                        carDoing.downHero(dijing)
                        return heros.indexOf(it)
                    }//找不在车上的


                    //比如第一次时地精等其他都满，女王不满（防止抢兵）.这时如果上面找到了沙皇就下地精上沙皇了，后面会满上
                    //比如 下沙皇上的地精，本次预选里如果没有沙皇，那么我们就先满女王。但这里先排除地精，先找不是地精且需要满的
                    firstOrNull { it!!.isInCar() && !it.isFull() && it != dijing }?.let {
                        return heros.indexOf(it)
                    }

                    //因为地精1星，比如前面下了女王，如果这一组没女王，那么就补地精，补满，女王就出来了，否则万一就一直不出女王就尴尬了
                    if (!dijing.isFull()) {
                        return heros.indexOf(dijing)
                    }
                }

            } else {
                //未出现地精时上这几个
                log("地精不在车上")

                var index = heros.indexOf(dijing)
                if (index > -1) {
                    while (!dijingIng && curG < 20) {//加管卡，避免卡死。超过20关就不会再卡了
                        delay(50)
                        curG = guankaTask?.currentGuanIndex ?: 0
                    }
                    if (curG >= 20) {
                        return -1//如果超过20关，就直接返回-1，否则这会base里不会下19点名牌（boss结束了），就没空位，这里返回地精就上不去。。。。
                    }
                    return index
                } else {//地精不在车上，且预选里没地精，就一定有以下几个英雄,随便上哪个继续找地精
                    var ttheros = carDoing.carps.filter { it.mHeroBean != null && it.mHeroBean != dijing }.map {
                        it.mHeroBean!!
                    }
                    return defaultDealHero(heros, ttheros)
                }


            }
            return -1
        } else if (guanka == Guan.g21 || guanka == Guan.g40) {
//            //沙皇容易抢兵
            carDoing.downHero(dijing)
            carDoing.downHero(tuling)
            var hero0 = carDoing.carps.get(0).mHeroBean
            var hero1 = carDoing.carps.get(1).mHeroBean

            if (hero0 != null && hero0 != shitou && hero0 != tieqi) {
                carDoing.downHero(hero0)
            }
            if (hero1 != null && hero1 != shitou && hero1 != tieqi) {
                carDoing.downHero(hero1)
            }
            if (shitou.isInCar() && shitou.position != 1 && shitou.position != 0) {
                carDoing.downHero(shitou)
            }
            if (tieqi.isInCar() && tieqi.position != 1 && tieqi.position != 0) {
                carDoing.downHero(tieqi)
            }


            if (isGkOver(Guan.g21)) {
                waiting = true
            }
            if (!tieqi.isInCar() || !shitou.isInCar()) {//石头铁骑放下面一行
                if (!tieqi.isInCar()) {
                    var index = heros.indexOf(tieqi)
                    if (index > -1) return index
                }
                if (!shitou.isInCar()) {
                    var index = heros.indexOf(shitou)
                    if (index > -1) return index
                }
                return defaultDealHero(heros, arrayListOf(tieqi, shitou))
            } else {
                return defaultDealHero(heros, arrayListOf(houyi, dianfa, tieqi, niutou, shitou, yuren, dapao))
            }

        } else if (guanka == Guan.g37) {
//            return deal39(heros)
            return if (gk39ModelVersion == 1) deal39(heros) else deal39V2(heros)
        } else if (guanka == Guan.g101) {

            carDoing.downHero(yuren)

            return heros.indexOf(tuling)
        } else if (guanka == Guan.g109) {

            carDoing.downHero(houyi)
            var mu = heros.indexOf(muqiu)

            if (!yuren.isFull()) {//没满时如果掉血了就打木，没木才上鱼人
                if (XueLiang.isMLess(0.9f)) {//1秒内打过木，不用再打
                    var mun = useMuIndex(heros)
                    if (mun > -1) return mun
                }
                return heros.indexOf(yuren)
            } else {
                //如果鱼人满了，就刷到木时，等血少打木
                if (mu > -1) {
                    muBeiLe = true
                    while (guanka == Guan.g109 && (System.currentTimeMillis() - lastMuTime < 1500 || !XueLiang.isMLess(
                            0.9f
                        )) && muBeiLe
                    ) {
                        delay(50)
                    }
                    delay(500)//补木太快，怕不同步
                    lastMuTime = System.currentTimeMillis()
                    log("use muqiu at: ${lastMuTime}")
                    return mu
                } else return -1
            }

        } else if (guanka == Guan.g111) {
            carDoing.downHero(yuren)
            return heros.indexOf(houyi)
        } else if (guanka == Guan.g119) {

            when (g1191State) {
                1 -> {
                    carDoing.downHero(dapao)
                    carDoing.downHero(niutou)

                    if (!yuren.isFull()) {//没满时如果掉血了就打木，没木才上鱼人
                        if (XueLiang.isMLess(0.9f)) {
                            var mun = useMuIndex(heros)
                            if (mun > -1) return mun
                        }
                        return heros.indexOf(yuren)
                    }
                }

                5 -> {
                    carDoing.downHero(houyi)
                }

                6 -> {

                    if (!houyi.isFull()) {//没满时如果掉血了就打木，没木才上鱼人
                        if (XueLiang.isMLess(0.9f)) {
                            var mun = useMuIndex(heros)
                            if (mun > -1) return mun
                        }
                        return heros.indexOf(houyi)
                    }
                }

                7 -> {

                    if (!dapao.isFull()) {//没满时如果掉血了就打木，没木才上鱼人
                        if (XueLiang.isMLess(0.9f)) {
                            var mun = useMuIndex(heros)
                            if (mun > -1) return mun
                        }
                        return heros.indexOf(dapao)
                    }
                }

                8 -> {
                    carDoing.downHero(dapao)
                    carDoing.downHero(yuren)
                    carDoing.downHero(houyi)
                }

                9 -> {
                    if (yuren.isFull()) {//没触发8时，下大炮后裔
                        carDoing.downHero(dapao)
                        carDoing.downHero(houyi)
                    } else {
                        if (!yuren.isFull()) {//没满时如果掉血了就打木，没木才上鱼人
                            if (XueLiang.isMLess(0.9f)) {
                                var mun = useMuIndex(heros)
                                if (mun > -1) return mun
                            }
                            return heros.indexOf(yuren)
                        }
                    }
                }
            }
            //鱼人满了 就走后面的刷木逻辑
        } else if (guanka == Guan.g121) {
            carDoing.downHero(yuren)
            return defaultDealHero(heros, arrayListOf(niutou, houyi, dapao, dianfa, tieqi, shitou, tuling))
        }

        if (guanka == Guan.g109 || guanka == Guan.g119) {
            var mu = heros.indexOf(muqiu)
            if (mu > -1) {
                muBeiLe = true
                while ((guanka == Guan.g109 || guanka == Guan.g119) && (System.currentTimeMillis() - lastMuTime < 1500 || !XueLiang.isMLess(
                        0.9f
                    )) && muBeiLe
                ) {
                    delay(50)
                }
                delay(500)
                lastMuTime = System.currentTimeMillis()
                log("use muqiu at: ${lastMuTime}")
                return mu
            }
        }

        return -1
    }

    var lastMuTime = 0L

    private fun useMuIndex(heros: List<HeroBean?>): Int {
        if (System.currentTimeMillis() - lastMuTime > 1500) {
            lastMuTime = System.currentTimeMillis()
            return heros.indexOf(muqiu)
        }
        return -1
    }

    private suspend fun deal39V2(heros: List<HeroBean?>): Int {
        carDoing.downHero(niutou)
        carDoing.downHero(yuren)
        carDoing.downHero(houyi)
        if (!dianfa.isInCar()) {
            return heros.indexOf(dianfa)
        }
        if (isGkOver(Guan.g37)) {
            waiting = true
        }
        return -1
    }

    private suspend fun deal39(heros: List<HeroBean?>): Int {
        //车满的时候就等血量变低就上卡，都可以上。
        //上卡过程中，点了卡，血量没变时，只能上车上的卡，血量变了就可以上所有的卡

        if(guanka != Guan.g37){
            return -1
        }

        freshStatus39()
        if (status39 == 2) {//刚掉完血，啥都可以上，下次会再次刷39status
            if (carDoing.hasNotFull() || carDoing.hasOpenSpace()) {
                return defaultDealHero(heros, arrayListOf(dianfa, houyi, yuren, niutou))
            } else {//都满了就等点名（第一次也走这里，因为status39默认赋值为2）
                while (status39 != 1 && guanka == Guan.g37) {
                    delay(100)
                    freshStatus39()
                }
                //这是status39 = 1 了,递归回去，再进入后走 =1 逻辑，上车上的卡，并继续监听点名修复。车上满了或血量变了再变 2 回来
                return deal39(heros)
            }
        } else if (status39 == 1) {
            if (carDoing.hasNotFull()) {//只能上车上的
                var listtt = arrayListOf<HeroBean>()
                listtt.addAll(carDoing.carps.filter {
                    it.mHeroBean != null && !it.mHeroBean!!.isFull()
                }.map { it.mHeroBean!! })
                return defaultDealHero(heros, listtt)
            } else {//如果车上都满了，监听掉血,过程中也在继续监听下卡，监听到就下，反正多下了，也比少下了强
                while (status39 != 2 && guanka == Guan.g37) {
                    delay(100)
                    freshStatus39()
                }
                //这时status = 2 了，递归回去啥都上
                return deal39(heros)
            }
        }

        return -1
    }

    var lastXue = 1f

    /**
     * 1代表监听到刚下卡，2代表血量刚发生变化
     */
    private var status39 = 2
    private suspend fun freshStatus39() {
        var img = getImage(App.rectWindow);
        var xue = XueLiang.getXueLiang(img)


        val pos = AYUtil.getAy39SelectedPositions(carDoing.chePosition, img)
        if (pos.isNotEmpty()) {//点名，
            down39(img, pos)
        }
        if (abs(xue - lastXue) > 0.03) {//上卡过程中撞过了，没撞过就不用继续识别
            log("血量发生变化，所有卡都上")
            status39 = 2
        }
    }

    private suspend fun down39(img2: BufferedImage?, pos: List<Int>) {
        var img = img2 ?: getImage(App.rectWindow);
        lastXue = XueLiang.getXueLiang(img)//记录点名时的血量
        pos.forEach {
            carDoing.downPosition(it)
        }
        status39 = 1
        log("监听到点名了，只有车上的可以上")
    }

    override fun changeHeroWhenNoSpace(heroBean: HeroBean): HeroBean? {
        return null
    }

    override suspend fun onKeyDown(code: Int): Boolean {
        if (guankaTask?.currentGuanIndex == 119 || guankaTask?.currentGuanIndex == 118
//            || guankaTask?.currentGuanIndex == 49 || guankaTask?.currentGuanIndex == 48
        ) {//19
            muBeiLe = false
            when (code) {
                VK_NUMPAD5 -> {
                    g1191State = 5
                }

                VK_NUMPAD6 -> {
                    g1191State = 6
                }

                VK_NUMPAD7 -> {
                    g1191State = 7
                }

                VK_NUMPAD8 -> {
                    g1191State = 8
                }

                VK_NUMPAD9, VK_NUMPAD0 -> {
                    g1191State = 9
                }
            }

            return true
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