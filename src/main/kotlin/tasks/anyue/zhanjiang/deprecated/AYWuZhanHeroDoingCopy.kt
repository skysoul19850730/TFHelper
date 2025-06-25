package tasks.anyue.zhanjiang.deprecated

import MainData
import MainData.job79TimeDt
import data.*
import getImage
import kotlinx.coroutines.*
import log
import tasks.XueLiang
import tasks.Zhuangbei
import tasks.anyue.zhanjiang.BaseAnYueHeroDoing
import ui.zhandou.UIKeyListenerManager
import utils.AYUtil
import utils.GuDingShuaKaUtil
import java.awt.event.KeyEvent
import java.awt.event.KeyEvent.*
import java.awt.image.BufferedImage
import kotlin.math.abs

class AYWuZhanHeroDoingCopy : BaseAnYueHeroDoing(),
    UIKeyListenerManager.UIKeyListener {//默认赋值0，左边，借用左边第一个position得点击，去识别车位置后再更改
//es8  es4 ye5   6e0 y80  y50 y56 e04

    val zhanjiang = HeroBean("zhanjiang", 100)
    val tieqi = HeroBean("tieqi", 90)
    val saman = HeroBean("saman2", 80)
    val sishen = HeroBean("sishen", 70)
    val yuren = HeroBean("yuren", 60, compareRate = 0.9)
    val baoku = HeroBean("shexian", 30, needCar = true, isGongCheng = true, compareRate = 0.9)
    val xiaoye = HeroBean("xiaoye", 30)
    val dijing = HeroBean("dijing", 30)
    val huanqiu = HeroBean("huanqiu", 20, needCar = false, compareRate = 0.95)
    val guangqiu = HeroBean("guangqiu", 0, needCar = false)

    //副卡 底层 石头，萨满 其他：女王 小野 死神 亡将 射线（宝库）地精 光球 魔球

    enum class Guan(val des: String? = null) {
        /**
         * 优先满战将，地精能上就上，没满时最多上战将和地精，战将满后，继续满宝库，地精，过程中其他随意,不上女王
         */
        g1("优先满战将，地精能上就上，没满时最多上战将和地精，战将满后，继续满宝库，地精，过程中其他随意,不上女王"),

        /**
         * 下地精，其他全满，备地精
         */
        g17("下地精，其他全满，备地精"),

        /**
         * 19关开识别,开识别，上下地精操作
         */
//        g19("开识别，上下地精操作"),

        /**
         * 下沙，上地精，烟斗，其他全满
         */
        g21("下沙，上地精，烟斗，其他全满"),

        /**
         * 4个都下，留不满女王，若满则下了上一星
         */
        g37("4个都下，留不满女王，若满则下了上一星"),

        /**
         * 烟斗，不上地精，其他全满
         */
        g41("烟斗，不上地精，其他全满"),


        g51("用圣剑"),
        g71("用龙芯"),
        g79("小野定时"),
        gxiaoye(""),
    }

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

    var guDingShuaKaUtil: GuDingShuaKaUtil? = null


    private var job79: Job? = null
    private var job79StartTime = 0L

    private suspend fun beiye() {
        carDoing.downHero(xiaoye)
        preHero(xiaoye)
        waiting = false
    }

    private suspend fun shangye() {
        preHero(null)
        job79StartTime = System.currentTimeMillis()
    }

    private fun tiaoye79() {
        job79StartTime =
            System.currentTimeMillis()
        job79 = GlobalScope.launch {
            //为了避免操作耗时引起时间问题，记录每次上野时间，第一次是10s（监听到79开始),
            // 之后是8s攻击一次（看截图应该不管是否打死牌，都是8s一次)
            // 如果时间不准，只需要改开始的10s，比如改成9800，或9500等。后面是8s一次不能改的，否则牌多了，时间误差就变大了
            // 每次攻击后 约1-1.2秒就开始出牌了，这时候可以进行识别扑克牌
//            beiye()
//            delay(9700 - (System.currentTimeMillis() - job79StartTime))
//            job79StartTime = System.currentTimeMillis()
//            shangye()
            //改进成 先delay 2s（视为废话时间），然后直接每8s一次技能。
            delay(1700)
            job79StartTime = System.currentTimeMillis()
            while (guanka == Guan.g79) {
                delay(2000)
                App.save()//如果现在计算是准的，那这里应该可以只截图一张扑克牌，不用autosave，那样保存太多图片，如果不准，则需要继续使用autosave来观察规则
                beiye()
                delay(8000 - (System.currentTimeMillis() - job79StartTime) + job79TimeDt.value)
                shangye()
            }
        }
    }

    private fun stopTiaoye79() {
        job79?.cancel()
    }


    override fun doOnGuanChanged(guan: Int) {
        if (guan == 100) {
            App.stopAutoSave()
            return
        }
        if (guan == 99) {
            GlobalScope.launch {
                carDoing.downHero(xiaoye)
                guDingShuaKaUtil = GuDingShuaKaUtil(xiaoye, 1300)
                guanka = Guan.gxiaoye
                waiting = false
            }

            App.startAutoSave()
            return
        }
        guDingShuaKaUtil = null

        if (guan == 80 || guan == 90) {
            stopTiaoye79()
            return
        }

        if (guan == 79 || guan == 89) {
            try {
                changeGuanKa(guan, 79, Guan.g79)
            } catch (e: Exception) {
            }
            tiaoye79()
            return
        }

        try {
            changeGuanKa(guan, 101, Guan.g71)
            changeGuanKa(guan, 91, Guan.g71)
            changeGuanKa(guan, 81, Guan.g71)
            changeGuanKa(guan, 71, Guan.g71)
            changeGuanKa(guan, 50, Guan.g51)



            changeGuanKa(guan, 40, Guan.g41)
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
        return hero != zhanjiang
    }

    var gk1Over = false
    fun isGkOver(g: Guan): Boolean {

        var heroOk = zhanjiang.isFull()
        if (!heroOk) return false

        return when (g) {
            Guan.g1 -> {
//                if (gk1Over) true
//                else (dijing.isFull() && jiaonv.isFull() && niutou.isFull() && nvwang.isInCar() && saman.isFull() && baoku.isFull()).apply {
//                    gk1Over = this
//                }
                yuren.isFull() && baoku.isFull() && sishen.isFull() && xiaoye.isFull() && saman.isFull() && tieqi.isFull()
            }

            Guan.g17 -> {
                var r =
                    yuren.isFull() && baoku.isFull() && sishen.isFull() && xiaoye.isFull() && saman.isFull() && tieqi.isFull()
                if (r) {//只设置一次true
                    gk1Over = true
                }

                //17要处理地精，这里不能返回true，一直false
                false
            }

            Guan.g21 -> {
                yuren.isFull() && baoku.isFull() && sishen.isFull() && xiaoye.isFull() && saman.isFull() && tieqi.isFull() && Zhuangbei.isYandou()
            }

//            Guan.g31 -> Zhuangbei.isShengjian() && shahuang.isFull()  && baoku.isFull()
            Guan.g37 -> false
            Guan.g41 -> yuren.isFull() && sishen.isFull() && xiaoye.isFull() && saman.isFull() && tieqi.isFull() && baoku.isFull() && Zhuangbei.isYandou()
            Guan.g51 -> {
                Zhuangbei.isQiangxi() && yuren.isFull() && sishen.isFull() && xiaoye.isFull() && saman.isFull() && tieqi.isFull() && baoku.isFull()
            }

            Guan.g71 -> {
                Zhuangbei.isLongxin() && yuren.isFull() && sishen.isFull() && xiaoye.isFull() && saman.isFull() && tieqi.isFull() && baoku.isFull()
            }

            Guan.g79 -> {
                return xiaoye.isInCar()
            }


            else -> false
        }
    }


    override fun initHeroes() {
        heros = arrayListOf()
        heros.add(zhanjiang)
        heros.add(tieqi)
        heros.add(saman)
        heros.add(sishen)
        heros.add(yuren)
        heros.add(xiaoye)
        heros.add(dijing)
        heros.add(baoku)
        heros.add(huanqiu)
        heros.add(guangqiu)
    }

    suspend fun doOnKeyDown(code: Int): Boolean {

        if (guankaTask?.currentGuanIndex == 39 || guankaTask?.currentGuanIndex == 38
//            || guankaTask?.currentGuanIndex == 49 || guankaTask?.currentGuanIndex == 48
        ) {//19
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

            if(DownLoadPositionFromKeyFor39>-1){
                down39(null,arrayListOf(DownLoadPositionFromKeyFor39))
            }

            return true
        }
        if (guDingShuaKaUtil != null) {
            if (code == VK_NUMPAD3) {
                guDingShuaKaUtil!!.go()
            }
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

    override suspend fun dealHero(heros: List<HeroBean?>): Int {
        while (waiting) {
            delay(100)
        }

        if (guDingShuaKaUtil != null) {
            carDoing.downHero(xiaoye)
            return guDingShuaKaUtil!!.dealHero(heros)
        }

//        if (!waiting && isGkOver(guanka)) {
//            waiting = true
//        }

        if (guanka == Guan.g1) {//第一阶段
            if (!zhanjiang.isFull()) {//直上战将
                log("战将没满")
//                log("战将没满")
                return defaultDealHero(heros, arrayListOf(zhanjiang, guangqiu))
            } else {
                return defaultDealHero(heros, arrayListOf(tieqi, saman, yuren, baoku, sishen, xiaoye, guangqiu))
            }
        } else if (guanka == Guan.g17) {

            if (!gk1Over) {//g11没结束，代表还没满车位，对于19来说很危险，所以继续完成11，但实际，这里情况基本没有，只是防止万一，比如确实卡牌很严重等。
                var r =
                    yuren.isFull() && baoku.isFull() && sishen.isFull() && xiaoye.isFull() && saman.isFull() && tieqi.isFull()
                if (r) {//只设置一次true
                    gk1Over = true
                }
                //因为最可能缺钱的地方就是19关，所以这里不调整沙皇位置，等39再换
//                var index = heros.indexOf(shahuang)
//                if (index > -1 && !shahuang.isInCar()) {//如果拿到沙皇，并且沙皇没在1的位置，则下1上沙
//                    carDoing.downPosition(1)
//                    return index
//                }

                //这里怕耗时，因为要速满，17马上就19了，10秒左右就要都满上
//                index = heros.indexOf(huanqiu)//捡漏刷烟斗
//                if (index > -1 && !Zhuangbei.isYandou() && Zhuangbei.hasZhuangbei()) {//小翼 烟斗
//                    return index
//                }

                log("guan11还未结束")
                return defaultDealHero(heros, arrayListOf(tieqi, saman, yuren, baoku, sishen, xiaoye, guangqiu))
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


                //其实这里基本不可能走到。预选3个，比如光，幻，还有一张上车的卡，要么不在车上：等通知下地精就上去了，要么在车上 不满就上了
                if (carDoing.hasNotFull()) {
                    return heros.indexOf(guangqiu)
                }
            } else {
                //未出现地精时上这几个
                log("地精不在车上")
//                if (carDoing.hasNotFull()) {//先升满，然后备地精
//                    return defaultDealHero(heros, arrayListOf(shahuang, baoku, jiaonv, saman, niutou, guangqiu, nvwang))
//                } else {
//                    preHero(dijing)
//                }

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
                    return defaultDealHero(heros, arrayListOf(yuren, sishen, saman, xiaoye, tieqi, baoku, guangqiu))
                }


            }
            return -1
        } else if (guanka == Guan.g21) {
//            //沙皇容易抢兵
            carDoing.downHero(dijing)
            var index =
                defaultDealHero(heros, arrayListOf(yuren, sishen, saman, xiaoye, tieqi, baoku, guangqiu))
            if (index > -1) {
                return index
            }

            index = heros.indexOf(huanqiu)
            if (index > -1 && !Zhuangbei.isYandou() && Zhuangbei.hasZhuangbei()) {//小翼 烟斗
                return index
            }

        } else if (guanka == Guan.g37) {

            return deal39(heros)

        } else if (guanka == Guan.g41) {

            var index =
                defaultDealHero(heros, arrayListOf(tieqi, yuren, saman, baoku, sishen, xiaoye, zhanjiang, guangqiu))
            if (index > -1) {
                return index
            }

            index = heros.indexOf(huanqiu)
            if (index > -1 && !Zhuangbei.isYandou() && Zhuangbei.hasZhuangbei()) {//小翼 烟斗
                return index
            }

        } else if (guanka == Guan.g51) {

            var index =
                defaultDealHero(heros, arrayListOf(tieqi, yuren, saman, baoku, sishen, xiaoye, zhanjiang, guangqiu))
            if (index > -1) {
                return index
            }

            index = heros.indexOf(huanqiu)
            if (index > -1 && !Zhuangbei.isQiangxi() && Zhuangbei.hasZhuangbei()) {//小翼 烟斗
                return index
            }
        } else if (guanka == Guan.g71) {
            var index =
                defaultDealHero(heros, arrayListOf(tieqi, yuren, saman, baoku, sishen, xiaoye, zhanjiang, guangqiu))
            if (index > -1) {
                return index
            }

            index = heros.indexOf(huanqiu)
            if (index > -1 && !Zhuangbei.isLongxin() && Zhuangbei.hasZhuangbei()) {//小翼 烟斗
                return index
            }
        }
        return -1
    }

    var lastXue = 1f

    /**
     * 1代表监听到刚下卡，2代表血量刚发生变化
     */
    private var status39 = 2


    private suspend fun deal39(heros: List<HeroBean?>): Int {
        //车满的时候就等血量变低就上卡，都可以上。
        //上卡过程中，点了卡，血量没变时，只能上车上的卡，血量变了就可以上所有的卡
        if(guanka != Guan.g37){
            return -1
        }
        freshStatus39()
        if(status39 == 2){//刚掉完血，啥都可以上，下次会再次刷39status
            if(carDoing.hasNotFull()|| carDoing.hasOpenSpace()){
                return defaultDealHero(heros, arrayListOf(yuren, sishen, saman, xiaoye, tieqi, baoku, guangqiu))
            }else{//都满了就等点名（第一次也走这里，因为status39默认赋值为2）
                while(status39!=1 && guanka == Guan.g37){
                    freshStatus39()
                    delay(100)
                }
                //这是status39 = 1 了,递归回去，再进入后走 =1 逻辑，上车上的卡，并继续监听点名修复。车上满了或血量变了再变 2 回来
                return deal39(heros)
            }
        }else if(status39 == 1){
            if(carDoing.hasNotFull()){//只能上车上的
                var listtt = arrayListOf<HeroBean>()
                listtt.addAll(carDoing.carps.filter {
                    it.mHeroBean != null && !it.mHeroBean!!.isFull()
                }.map { it.mHeroBean!! })
                listtt.add(guangqiu)
                return defaultDealHero(heros, listtt)
            }else{//如果车上都满了，监听掉血,过程中也在继续监听下卡，监听到就下，反正多下了，也比少下了强
                while(status39!=2 && guanka == Guan.g37){
                    freshStatus39()
                    delay(100)
                }
                //这时status = 2 了，递归回去啥都上
                return deal39(heros)
            }
        }

        return -1
    }

    private suspend fun freshStatus39(){
        var img = getImage(App.rectWindow);
        var xue = XueLiang.getXueLiang(img)


        val pos = AYUtil.getAy39SelectedPositions(carDoing.chePosition, img)
        if (pos.isNotEmpty()) {//点名，
            down39(img,pos)
        }
        if (abs(xue - lastXue) > 0.03) {//上卡过程中撞过了，没撞过就不用继续识别
            log("血量发生变化，所有卡都上")
           status39 = 2
        }
    }
    private suspend fun down39(img2:BufferedImage?,pos:List<Int>){
        var img = img2?: getImage(App.rectWindow);
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