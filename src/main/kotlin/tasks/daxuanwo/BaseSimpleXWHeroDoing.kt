package tasks.daxuanwo

import data.Config
import data.HeroBean
import data.MPoint
import data.MRect
import getImage
import getImageFromRes
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import tasks.SimpleHeZuoHeroDoing
import tasks.XueLiang
import tasks.daxuanwo.utils.WX59
import ui.zhandou.UIKeyListenerManager
import utils.ImgUtil
import utils.MRobot
import java.awt.event.KeyEvent

// 9  29 都自动执行
abstract class BaseSimpleXWHeroDoing() : SimpleHeZuoHeroDoing(), UIKeyListenerManager.UIKeyListener {

    var heroDown49: HeroBean? = null
    var midHeros69: List<HeroBean>? = null

    var auto29 = true
    var auto59 = false

    override suspend fun onKeyDown(code: Int): Boolean {
        //如果龙王识别出错可以按快捷下对应卡牌，但不知道快捷键按下得时间，所以不能延时进行上卡，只能快捷键9来恢复上卡

        if (code == KeyEvent.VK_NUMPAD9) {//9强制改变waitting，防止waiting有逻辑错误不上卡
            waiting = !waiting
            return true
        }

        if (code == KeyEvent.VK_NUMPAD0) {
            if (curGuan == 9) {
                GlobalScope.launch {
                    MRobot.moveFullScreen()
                }
                return true
            }
            if (curGuan == 49) {
                g49 = 1
                return true
            }
            if (curGuan == 69) {
                g69State += 1
                if (g69State >= 2) {
                    g69State = 0
                }
                return true
            }

        }
        if (code == KeyEvent.VK_NUMPAD3) {
            if (curGuan == 49) {
                g49 = 2
                return true
            }
        }
        return false


    }


    fun add50(fullsHeros: List<HeroBean>, midHeros: List<HeroBean>) {
        midHeros69 = midHeros
        //这里要晚一点
        addGuanDeal(52) {
            over {
                fulls(*fullsHeros.toTypedArray())
            }
            chooseHero {
                val otherHeros = fullsHeros.filter { !midHeros.contains(it) }
                if (!carDoing.carps.get(0).hasHero() || !carDoing.carps.get(1).hasHero()) {
                    //如果01位置空着，就上其他的
                    upAny(*otherHeros.toTypedArray())
                } else if (!carDoing.carps.get(2).hasHero() || !carDoing.carps.get(3).hasHero()) {
                    //如果01都上好后，2，3有空的，则上mids
                    var index = upAny(*midHeros.toTypedArray())
                    if (index > -1) {
                        index
                    } else {
                        //如果没有mids预选，可以上 0，1位置的英雄
                        val list = carDoing.carps.take(2).map {
                            it.mHeroBean!!
                        }
                        upAny(*list.toTypedArray())
                    }
                } else {
                    //如果0123都不空了，就开始全部上满
                    upAny(*fullsHeros.toTypedArray())
                }
            }

            onStart {

                //如果不在fulls里，就下掉
                carDoing.carps.forEach {
                    if (it.mHeroBean != null && !fullsHeros.contains(it.mHeroBean)) {
                        carDoing.downHero(it.mHeroBean!!)
                    }
                }

                //如果要摆中间的两个 不在中间，就下掉要重上
                midHeros.forEach {
                    if (it.position != 2 && it.position != 3) {
                        carDoing.downHero(it)
                    }
                }

                //看中间的两个是不是要摆放的，不是的话，要下掉
                var hero2 = carDoing.carps.get(2).mHeroBean
                if (hero2 != null && !midHeros.contains(hero2)) {
                    carDoing.downPosition(2)
                }
                hero2 = carDoing.carps.get(3).mHeroBean
                if (hero2 != null && !midHeros.contains(hero2)) {
                    carDoing.downPosition(3)
                }


            }

        }
    }

    var g49 = 0
    open fun add49(heroBean: HeroBean) {
        heroDown49 = heroBean

        addGuanDeal(48) {
            over {
                heroDown49!!.isInCar()
            }
            chooseHero {
                upAny(heroDown49!!)
            }
            onStart {
                carDoing.downHero(heroDown49!!)
            }
        }

        addGuanDeal(49) {
            over {
                curGuan > 49 || (g49 == 2 && heroDown49!!.isFull() && g49StartBoss == null)
            }
            chooseHero {
                if (heroDown49!!.isFull() && g49StartBoss != null) {
                    g49 = 3
                }

                if (g49 == 3) {
                    g49StartBoss?.invoke(this) ?: -1
                } else {

                    val index = indexOf(heroDown49)
                    if (index > -1) {
                        while (g49 == 0) {
                            delay(100)
                        }
                        if (g49 != 2) {
                            carDoing.downHero(heroDown49!!)
                            g49 = 0
                            delay(100)
                        }
                        index
                    } else {
                        -1
                    }
                }
            }
            des = "需要切的时候按0，会自动下卡再上卡，收集完成后按3，切换的卡会上满"
        }
    }

    var lastQiu49 = 0L
    lateinit var qiu49: HeroBean
    var qiu49Time: Long = 0L

    /**
     * 其实这里也就小号用的到，这个球基本也就是魔球了
     */
    fun add49WithQiu(heroBean: HeroBean, qiu: HeroBean, qiuTime: Long) {
        heroDown49 = heroBean
        qiu49 = qiu
        qiu49Time = qiuTime

        addGuanDeal(48) {
            over {
                heroDown49!!.isInCar()
            }
            chooseHero {
                upAny(heroDown49!!)
            }
            onStart {
                carDoing.downHero(heroDown49!!)
            }
        }

        addGuanDeal(49) {
            over {
                curGuan > 49 || (g49 == 2 && heroDown49!!.isFull() && g49StartBoss == null)
            }
            chooseHero {
                if (heroDown49!!.isFull() && g49StartBoss != null) {
                    g49 = 3
                }

                if (g49 == 3) {
                    g49StartBoss?.invoke(this) ?: -1
                }else if(g49==2){//打完融合，boss和满herodown的两个阶段都不再需要打魔球了，鱼人战将基本都够攻速了，打魔没效果了。
                    return@chooseHero upAny(heroDown49!!)
                } else {
                    var mo = indexOf(qiu49)
                    val index = indexOf(heroDown49)
                    //融合阶段
                    if (g49 == 1) {
                        carDoing.downHero(heroDown49!!)
                        g49 = 0
                    }

                    if (!heroDown49!!.isInCar()) {
                        //没在车上就赶紧上车，通常就是刚下了卡，那就赶紧上去，先不考虑球，如果预选里没有，再看是否扔魔不扔就反-1刷新了
                        if (index > -1) {
                            return@chooseHero index
                        } else if (mo > -1 && XueLiang.getXueLiang() > 0.9f) {//这里不管时间也没关系，只有血量够就可以扔魔
                            lastQiu49 = System.currentTimeMillis()
                            return@chooseHero mo
                        } else {
                            return@chooseHero -1 //没魔就放弃这次， 去刷新上卡
                        }
                    }
                    if (mo > -1) {
                        //如果在车上，这里就可以等，但等的逻辑里必须判断g49==1让下卡的情况
                        while(System.currentTimeMillis()-lastQiu49<qiu49Time && g49==0){
                            delay(100)
                        }
                        if(g49 == 1){
                            carDoing.downHero(heroDown49!!)
                            g49 = 0
                        }
                        if (!heroDown49!!.isInCar() && index > -1) {
                            return@chooseHero index
                        } else {
                            lastQiu49 = System.currentTimeMillis()
                            return@chooseHero mo
                        }
                    }


                    //如果上面都没走，证明没特殊情况，所以至少也要刷出魔球来，这里就返回-1去刷出魔球了需要
                   return@chooseHero -1
                }
            }
            des = "需要切的时候按0，会自动下卡再上卡，收集完成后按3，切换的卡会上满"
        }
    }

    var g49StartBoss: (suspend (List<HeroBean?>) -> Int)? = null


    var g69State = 0 //0:全上，1 下中间俩
    var g69Type = 1  // 0：正常上，按顺序，有哪个上哪个，
    // 1：没都上车时，优先上没上车的。比如要满鱼人，但也要上个电法撑速度，
    // 当有鱼人电法时如果电法没在车，就先电法，后面就都一样了
    //

    // 不管g69Type是什么，在达到g69基本要求后，是否需要第一个优先快速满，与upany的区别就是如果没第一个有第二个但也有光球时，upany会优先上卡
    //而不是上光（光还验卡呢）。这里优先用光来撞运气去满第一个，然后考虑节省掉验光操作
    var g69TypeFirstQuickFull = true

    var g69guangCount = 0
    var g69allNeedStar = 8
    override suspend fun onGuangqiuPost() {
        if (curGuan == 69) {
            var noFullCount = carDoing.carps.count {
                !(it.mHeroBean?.isFull() ?: true)
            }
            if (noFullCount == 1) {//只有一个英雄就直接长星，15光了
                carDoing.carps.find {
                    !(it.mHeroBean?.isFull() ?: true)
                }?.apply {
                    addHero()
//                if(mHeroBean?.isFull() == true){
//                    mHeroBean?.checkStarLevelUseCard(carDoing)
//                }
                }
            } else if (noFullCount < 1) {//都满着就不用验了
                return
            } else {
                g69guangCount++
            }
            return
        }
        super.onGuangqiuPost()
    }

    fun add69() {
        addGuanDeal(69) {
            over {
                curGuan > 69
            }

            chooseHero {
                if (g69State == 0) {
                    if (midHeros69?.all { it.isFull() } == true) {
                        while (g69State == 0) {
                            delay(200)
                        }
                    } else {
                        var index = -1
                        //type = 0
                        if (g69Type == 0) {
                            if (g69TypeFirstQuickFull) {
                                //优先快速满第一个,这里就包含光了
                                index = upAny(midHeros69!!.first())
                            }

                            if (index < 0) {
                                index = upAny(*midHeros69!!.toTypedArray())
                            }

                        } else if (g69Type == 1) {
                            //第一个都没上车。都走正常顺序
                            if (!midHeros69!!.first().isInCar()) {
                                index = upAny(*midHeros69!!.toTypedArray())
                            } else if (!midHeros69!!.get(1).isInCar()) {
                                //第二个没上车，优先上车
                                index = upAny(*midHeros69!!.reversed().toTypedArray())
                            } else {

                                if (g69TypeFirstQuickFull) {
                                    //优先快速满第一个,这里就包含光了
                                    index = upAny(midHeros69!!.first())
                                }

                                if (index < 0) {
                                    index = upAny(*midHeros69!!.toTypedArray())
                                }
                            }
                        }

                        if (index > -1) {
                            if (midHeros69!!.sumOf { it.currentLevel } + g69guangCount >= g69allNeedStar - 1) {
                                midHeros69!!.forEach {
                                    it.setFull()//当前星 + 光 = 满-1 就都设置为满就可以了.(因为index还没返回出去，所以按-1计算
                                }
                            }
                        }

                        return@chooseHero index
                    }
                }
                if (g69State == 1) {
                    midHeros69?.forEach {
                        carDoing.downHero(it)
                    }
                    g69guangCount = 0
                    //备卡第一个
                    val ind = upAny(midHeros69!!.first())

                    if (ind > -1) {
                        while (g69State == 1) {
                            delay(50)
                        }
                        return@chooseHero ind
                    }
                }
                -1
            }

            onStart {
                g69allNeedStar = midHeros69!!.sumOf {
                    it.fullStarNum
                }
            }

            des = "69关，按0依次执行 下中间两个或上满中间两个"
        }
    }

    override fun onGuanChange(guan: Int) {
        super.onGuanChange(guan)

        if (guan == 9) {
            GlobalScope.launch {
                delay(1000)
                MRobot.moveFullScreen()
            }
        }
        if (guan == 29 && auto29) {
            start29()
        } else {
            stop29()
        }

        if (guan == 59) {
            WX59.autoDo(auto59)
        }


//        if (guan in listOf(59)) {
//            App.startAutoSave(200)
//        } else {
//            App.stopAutoSave()
//        }
    }


    override fun onGuanFix(guan: Int) {
        guankaTask?.setCurGuanIndex(guan)
    }

    override fun onLongWangZsClick() {
    }

    override fun onLongWangFsClick() {
    }

    override fun onKey3Down() {
    }

    override fun onWaitingClick() {
        waiting = !waiting
    }


    override fun onStart() {
        super.onStart()
        UIKeyListenerManager.addKeyListener(this)
    }

    override fun onStop() {
        super.onStop()
        UIKeyListenerManager.removeKeyListener(this)
        App.stopAutoSave()
    }


    var job29: Job? = null
    fun start29() {
        var subFoler = "${Config.platName}/xuanwo/"
        val daxia = getImageFromRes("${subFoler}xw_daxia.png")
        val xw_pangxie = getImageFromRes("${subFoler}xw_pangxie.png")
        val xw_shaoji = getImageFromRes("${subFoler}xw_shaoji.png")
        val xw_shaoyu = getImageFromRes("${subFoler}xw_shaoyu.png")
        val xw_zhutou = getImageFromRes("${subFoler}xw_zhutou.png")

        job29?.cancel()
        job29 = GlobalScope.launch {
            while (curGuan == 29) {
                val img = getImage(MRect.createWH(675, 210, 54, 30))
                val sim = 0.95
                val point = if (ImgUtil.isImageSim(img, daxia, sim)) {
                    MPoint(650, 300)
                } else if (ImgUtil.isImageSim(img, xw_pangxie, sim)) {
                    MPoint(410, 310)
                } else if (ImgUtil.isImageSim(img, xw_shaoji, sim)) {
                    MPoint(470, 410)
                } else if (ImgUtil.isImageSim(img, xw_shaoyu, sim)) {
                    MPoint(580, 410)
                } else if (ImgUtil.isImageSim(img, xw_zhutou, sim)) {
                    MPoint(530, 270)
                } else {
                    null
                }
                if (point != null) {
                    point.clickPc()
                    delay(5000)
                } else {
                    delay(100)
                }
            }
        }
    }

    fun stop29() {
        job29?.cancel()
    }

    fun start59() {

    }

    fun stop59() {

    }
}