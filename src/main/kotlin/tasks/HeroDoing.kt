package tasks

import MainData
import data.*
import doDebug
import getImage
import getSubImage
import kotlinx.coroutines.*
import listerners.UIListener
import listerners.UIListenerManager
import log
import logOnly
import model.CarDoing
import tasks.guankatask.GuankaTask
import utils.ImgUtil
import utils.LogUtil
import utils.MRobot
import java.awt.Color
import java.awt.event.KeyEvent
import java.awt.image.BufferedImage
import java.util.*
import kotlin.collections.ArrayList
import kotlin.concurrent.timerTask
import kotlin.coroutines.resume
import kotlin.random.Random
import kotlin.system.measureTimeMillis

abstract class HeroDoing(var chePosition: Int = -1, val flags: Int = 0) : IDoing, GuankaTask.ChangeListener,
    App.KeyListener {

    companion object {
        val FLAG_GUANKA = 0x00000001
        val FLAG_KEYEVENT = 0x00000010
    }

    //在InitHero里更改吧。这个就是天空的时候发现车有偏移，与合作和寒冰的车的坐标有出入，但大小没变，所以暂时只改偏移就行
    var carPosOffset = 0

    lateinit var heros: ArrayList<HeroBean>
    lateinit var carDoing: CarDoing
    lateinit var otherCarDoing: CarDoing
    private var carChecked = false

    var running = false
    private var mainJob: Job? = null
    var waiting = false
        set(value) {
            field = value
            MainData.isWaiting.value = value
        }


    var guankaTask: GuankaTask? = null

    var mUiListener = object : UIListener {
        override fun onRecheckStars() {
            GlobalScope.launch {
                carDoing.reCheckStars()
            }
        }

        override fun setWaiting(waiting: Boolean) {
            this@HeroDoing.waiting = waiting
        }
    }

    val curGuan: Int
        get() = (guankaTask?.currentGuanIndex ?: 0)+1


    abstract fun initHeroes()

    //决定用哪个，-1都不用(如何车上6个满了，现在要替换英雄，需要实现者去下卡，这个方法只会告诉外面点击哪个上车）
    abstract suspend fun dealHero(heros: List<HeroBean?>): Int

    //每格子是否更换场上英雄，返回null代表不更换，等着开格子,返回bean 下bean再上bean
    abstract fun changeHeroWhenNoSpace(heroBean: HeroBean): HeroBean?

    //点了英雄后，比如光 魔等，需要延迟
    open suspend fun afterHeroClick(heroBean: HeroBean) {
        //-1 为单车，不用check
        if (chePosition != -1 && !carChecked && heroBean.position == 0) {
            checkCar()
//            if(carDoing.chePosition==1){//变换了
//                heroBean.reset()
//                carDoing.addHero(heroBean)
//            }
        }

        if (needCheckStar && carDoing.openCount() > 1) {
            carDoing.checkStars()
            needCheckStar = false
        }

        if (heroBean.heroName == "guangqiu") {
            onGuangqiuPost()
        } else if (heroBean.heroName == "huanqiu") {
            onHuanQiuPost()
        }

        doAfterHeroBeforeWaiting(heroBean)

        while (waiting) {//卡住 不再刷卡，幻的原因是，之前先预选了卡，比如第一个是木球，但过程中使用幻或者其他操作已经改变了预选卡的组成，比如第一个变成了幻。导致小翼无限刷卡时第一个判断上木，结果就上成了幻！！！
            delay(100)
        }
    }

    open suspend fun doAfterHeroBeforeWaiting(heroBean: HeroBean) {

    }

    var curZhuangBei: Int = 0
    var isHuanIng = false
    open suspend fun onHuanQiuPost() {
        curZhuangBei = Zhuangbei.getZhuangBei()
        isHuanIng = true
        GlobalScope.launch {
            delay(Config.delayNor)
            try {
                withTimeout(1500) {//加个超时保险一些，防止死循环
                    var reZB = Zhuangbei.getZhuangBei(true)
                    while (reZB == curZhuangBei || reZB == 0) {
                        delay(100)
                        reZB = Zhuangbei.getZhuangBei(true)
                    }
                }
            } catch (e: Exception) {

            }
            doAfterHeroBeforeWaiting(heros.first { it.heroName == "huanqiu" })
            isHuanIng = false
        }

    }

    var needCheckStar = false
    var needReCheckStar = false
    open suspend fun onGuangqiuPost() {
        if (App.mLaunchModel and App.model_duizhan != 0) {
            carDoing.checkStars()
            return
        }

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
            var checked = carDoing.checkStarsWithoutCard()
            if (!checked) {//1.5秒没有check到的话，再使用弹窗识别
                if (carDoing.openCount() > 1 || chePosition == 0) {//前车或开格子多余1个
                    carDoing.checkStars()
                } else {
                    needCheckStar = true
                }
            }
        }


    }

    override fun init() {

        carDoing = CarDoing(chePosition).apply {
            initPositions(carPosOffset)
            attchToMain()
        }
        initHeroes()
        if (flags and FLAG_GUANKA != 0) {
            guankaTask = GuankaTask().apply {
                changeListener = this@HeroDoing
            }
        }
        if (flags and FLAG_KEYEVENT != 0) {
            App.keyListeners.add(this)
        }
    }

    fun heroCountInCar() = heros.filter { it.isInCar() && !it.isGongCheng }.size


    open fun onStart() {
        Zhuangbei.init()
        guankaTask?.start()
        UIListenerManager.addUIListener(mUiListener)
    }


    protected suspend fun checkCar() {
        log("开始检测车")
        carDoing.carps.get(0).click()
        delay(1000)
        if (Recognize.saleRect.isFit()) {//是自己，啥也不用干，开始初始化得位置就是对得
            chePosition = 0//
        } else {
            //我在右边
            chePosition = 1
            carDoing.chePosition = 1
            carDoing.reInitPositions(carPosOffset)
        }
        log("识别车位结果：$chePosition")
        CarDoing.cardClosePoint.click()
        otherCarDoing = CarDoing((chePosition + 1) % 2).apply {
            initPositions()
        }
        carChecked = true
    }

    override fun start() {
        if (running) return
        running = true
        onStart()
        mainJob = GlobalScope.launch {
            var hero = shuaka(false)
            while (running) {
                shangka(hero)
                hero = shuaka()
            }
        }
    }


    open fun onStop() {
        guankaTask?.stop()
        App.keyListeners.remove(this)
        MainData.carPositions.clear()
        UIListenerManager.removeUIListener(mUiListener)
        LogUtil.saveAndClear()

    }

    override fun stop() {
        log("herodoing stop")
        running = false
        mainJob?.cancel("tuichu")
        onStop()
    }

    var needCheckQian = true

    open suspend fun shuaka(needShuaxin: Boolean = true): List<HeroBean?> {
        var hs: List<HeroBean?>? = null
        var first = true
        var imgLoged = false
        var time = System.currentTimeMillis()
        while (hs == null && running) {
            logOnly("未识别到英雄")
            if (!first) {
                log("某次刷卡失败了，超时未识别到卡")
                if (!imgLoged) {
//                    log(getImage(App.rectWindow))
                    imgLoged = true
                }
                if (Recognize.saleRect.isFit()) {//识别不到 识别看看是不是英雄弹窗挡住了//如果上卡和下卡一起操作，这里会导致下卡失败//加到这里应该没问题，毕竟是识别出错后才尝试看有没有未关闭弹窗
                    CarDoing.cardClosePoint.click()
                }
            }
            first = false

//            if (needShuaxin) {
////                withTimeoutOrNull(2000) {//超时就点一下，这里没有问题
////                if (needCheckQian) {
////                    measureTimeMillis {
////                        while (Config.rect4ShuakaColor.hasColor(Color.RED)) {//有白色（钱够）再点击刷新->改成如果有红色
////                            delay(50)
////                        }
////                    }.apply {
////                        log("识别刷新银币颜色耗时 $this ms")
////                    }
////                }
////                }
////                if (!needCheckQian) {
////                    val imgBefore = getImage(Config.zhandou_hero1CheckRect)
////                    MRobot.singleClick(Config.zhandou_shuaxinPoint)
////                    var shuaxinClicked = true
////
////                    while (shuaxinClicked) {
////
////                        withTimeoutOrNull(100) {
////                            while (shuaxinClicked) {
////                                val img = getImage(Config.zhandou_hero1CheckRect)
////                                if (!ImgUtil.isImageSim(imgBefore, img, 0.99)) {//如果一样代表刷新没点下去
////                                    shuaxinClicked = false
////                                }
////                                delay(10)
////                            }
////                        }
////                        if (shuaxinClicked) {//如果没变成false，证明钱没变，就再点一下
////                            log("刷新点击没成功，再点一次")
////                            MRobot.singleClick(Config.zhandou_shuaxinPoint)
////                        }
////                    }
////                } else {
//                MRobot.singleClick(Config.zhandou_shuaxinPoint)
//                delay(180)
////                }
//
//                if (lastHeroPres == null) {
//
//                }
//
//            }
////            if(needCheckQian) {
//            delay(100)//部下标注
////            }
//            hs = getPreHeros(if (needShuaxin) 200 else 10000)//点完刷新如果1秒中识别不出来应该就是识别不出来了，这里不需要2秒

            if (!needShuaxin) {
                hs = getPreHeros(10000)
            } else {
                if (needCheckQian) {
                    measureTimeMillis {
                        while (Config.rect4ShuakaColor.hasColor(Color.RED)) {//有白色（钱够）再点击刷新->改成如果有红色
                            delay(50)
                        }
                    }.apply {
                        log("识别刷新银币颜色耗时 $this ms")
                    }
                }
//                if (lastHeroPres == null) {
//                    clickShuaxinAndWaitYuxuanChanged()
//                    hs = getPreHeros(200)
//                } else {
                hs = clickShuaxinAndWaitYuxuanChanged2()
//                }
            }
        }
        log("识别到英雄 ${hs?.getOrNull(0)?.heroName}  ${hs?.getOrNull(1)?.heroName}  ${hs?.getOrNull(2)?.heroName} coast time:${System.currentTimeMillis() - time}")
        doDebug {
            log(
                getImage(
                    MRect.create4P(
                        Config.zhandou_hero1CheckRect.left,
                        Config.zhandou_hero1CheckRect.top,
                        Config.zhandou_hero3CheckRect.right,
                        Config.zhandou_hero1CheckRect.bottom
                    )
                )
            )
        }
        return hs!!
    }

//    private suspend fun clickShuaxinAndWaitYuxuanChanged() {
//        val imgBefore = getImage(Config.zhandou_hero1CheckRect)
//        MRobot.singleClick(Config.zhandou_shuaxinPoint)
//        var shuaxinClicked = true
//
//        while (shuaxinClicked) {
//
//            withTimeoutOrNull(100) {
//                while (shuaxinClicked) {
//                    val img = getImage(Config.zhandou_hero1CheckRect)
//                    if (!ImgUtil.isImageSim(imgBefore, img, 0.9)) {//如果一样代表刷新没点下去
//                        shuaxinClicked = false
//                    }
//                    delay(10)
//                }
//            }
//            if (shuaxinClicked) {//如果没变成false，证明钱没变，就再点一下
//                log("刷新点击没成功，再点一次")
//                MRobot.singleClick(Config.zhandou_shuaxinPoint)
//            }
//        }
//    }

    private suspend fun clickShuaxinAndWaitYuxuanChanged2(): List<HeroBean?>? {

        //先确保变了
//        clickShuaxinAndWaitYuxuanChanged()

        var shuaxinClicked = true

        var hs: List<HeroBean?>? = null

        while (shuaxinClicked) {
            MRobot.singleClick(Config.zhandou_shuaxinPoint)
            delay(100)
            withTimeoutOrNull(400) {
                while (shuaxinClicked) {
                    hs = doGetPreHeros()

                    var noChanged = if (lastHeroPres != null) {
                        (hs?.getOrNull(0) == null || hs?.getOrNull(0) == lastHeroPres?.getOrNull(0))
                                && (hs?.getOrNull(1) == null || hs?.getOrNull(1) == lastHeroPres?.getOrNull(1))
                                && (hs?.getOrNull(2) == null || hs?.getOrNull(2) == lastHeroPres?.getOrNull(2))
                    } else {
                        hs?.getOrNull(0) == null && hs?.getOrNull(1) == null && hs?.getOrNull(2) == null
                    }

                    if (noChanged) {//变了之后立马识别，会从上次的消失动画里继续识别到即将消失的上一组预选。这里就一直识别到和上一次不一样为止
                        //如果500ms都没识别到和上次 不一样，证明刷新后和之前一摸一样，那就返回当前的hs

                    } else {
                        shuaxinClicked = false
                    }
                }
            }

            if (shuaxinClicked) {
                log("刷新点击没成功，再点一次2")

//                log( getImage(
//                    MRect.create4P(
//                        Config.zhandou_hero1CheckRect.left,
//                        Config.zhandou_hero1CheckRect.top,
//                        Config.zhandou_hero3CheckRect.right,
//                        Config.zhandou_hero1CheckRect.bottom
//                    )
//                ))
            } else {
                //刷到一个不一样的，就代表刷新了,但可能 比如  只有第三个识别到女王，前两个还没识别到呢，但可以确认已经ok了，那么就用之前方式获取
                //但如果hs本身就3个都有，且和之前不同，那么就可以直接用了，就不用再识别一次
                if (hs?.contains(null) == true) {
                    hs = getPreHeros(700)
                }
            }
        }
        return hs
    }

    var yubeiHeroBean: HeroBean? = null
        private set

    fun preHero(heroBean: HeroBean?) {
        log("preHero: ${heroBean?.heroName}")
        yubeiHeroBean = heroBean
    }

    /**
     * 记录当前的预选卡组，如果上过车就null，没上过就记下来。点刷新时来判断是否刷新点击成功
     */
    var lastHeroPres: List<HeroBean?>? = null

    private suspend fun shangka(hs: List<HeroBean?>) {

        while (waiting) {
            delay(100)
        }
        var heroChoose = -1

        if (yubeiHeroBean != null) {
            var yuindex = hs.indexOf(yubeiHeroBean)
            if (yuindex > -1) {
                log("pre hero ok")
                var needCar = yubeiHeroBean?.needCar ?: false
                while (yubeiHeroBean != null) {
                    delay(100)
                }
                if (!needCar || carDoing.hasOpenSpace()) {//如果预备卡上时，有空位或本身是球，不需要位置，那么才上补卡，否则就认为放弃了补卡
                    //比如船长，合作时的狂将等。都会提前下卡给补卡让位置，备木这种属于精灵球，可以直接释放
                    heroChoose = yuindex
                    log("pre over ,index is $heroChoose")
                }
            }
        }
        if (heroChoose < 0) {
            heroChoose = dealHero(hs)
        }

        if (heroChoose > -1) {
            if (hs.get(heroChoose)!!.heroName == "huanqiu") {
                if (isHuanIng) {//如果刚用了幻，就等着
                    while (isHuanIng) {
                        delay(30)
                    }
                    //等幻结束，再deal一次，判断还用不用幻,下次deal完，因为isHuanIng是false了，所以就不会再递归了

                    var heroChoose2 = dealHero(hs)//幻后如果结束，这里会waiting卡住
                    if (heroChoose2 > -1) {
                        //重新deal后，如果有能用的就继续用，包括可能再次用幻
                        heroChoose = heroChoose2
                    } else {
                        //如果没有可上的，调用这个方法验证下，是否over，这里参数就假装是使用了幻之后
                        log("幻结束后，决定不再用幻或本卡组无其他可用卡，重新检测waiting")
                        //幻后如果正好waiting，也正好waiting后，要备卡，这里就有问题，因为幻完后就会把waiting设置true
                        //会卡在上面的dealHero处，preHero设置了waitingfalse后，dealHero仍然用原来的关卡策略找卡，因为已经结束，所以是-1
                        //会进这里，这里如果再次执行doAfter，则会再次将wating设置成true。要么就是子类的doAfter 自己判断好是否要备卡
                        //如果处于备卡中，则不要waiting变false
                        //这里暂时去掉这个doAfter，因为幻完已经处理了，这里就不用再处理了
                        //最好的方式是 这3个候选卡能进入prehero的逻辑，但现在框架只能重新刷卡了。。。。。
//                        doAfterHeroBeforeWaiting(hs.get(heroChoose)!!)
                        //这里递归一次shangka，看看是否需要备卡
                        shangka(hs)
                        return
                    }
                }
            }
        }

        logOnly("上卡的index 是 ${heroChoose}")
        lastHeroPres = hs
        if (heroChoose > -1) {
            lastHeroPres = null
            doUpHero(hs.get(heroChoose)!!, heroChoose)
        }
    }

    private suspend fun doUpHeroDeal(rect: MRect, incluedKuojian: Boolean = false) {
        var start = System.currentTimeMillis()

        if (!incluedKuojian && carDoing.downCardSpeed) {//更多的是点击阔建后没响应，导致卡没上去，所以加了后面的判断//如果不需要扩建的话，尝试下直接点击就认为上卡了，毕竟纯粹的点击很少失败
            //这里主要是寒冰199这种补卡要求太高，需要节省速度，先看效果，有问题的话再说，如果点击没响应，按原逻辑，上卡时间是要超过500ms的至少，然后才会重新点击，目前的日志基本没有日志体现上卡超过500ms
            MRobot.singleClick(MPoint(rect.clickPoint.x, rect.clickPoint.y + 25))
            delay(100)
            log("上卡花费时间：${System.currentTimeMillis() - start}")
            return
        }

        var uped = false
        while (!uped) {
            if (incluedKuojian) {
                MRobot.singleClick(Config.zhandou_kuojianPoint)//点扩建
                delay(50)
                //如果是前车，阔建后修改pos 0的位置
                if (carDoing.openCount() == 1) {//只差一次。open>1时就不用执行了
                    carDoing.reInitPos0ForQianche(carPosOffset)
                }
            }
            MRobot.singleClick(
                MPoint(
                    rect.clickPoint.x + Random.nextInt(5),
                    rect.clickPoint.y + 25 + Random.nextInt(5)
                )
            )
            //点击上卡后，如果都空了，代表确实上去了
            uped = withTimeoutOrNull(300) {
                while (doGetPreHeros() != null) {
                    delay(10)
                }
                true
            } ?: false
        }

        log("上卡花费时间：${System.currentTimeMillis() - start}")
    }

    open suspend fun doUpHero(heroBean: HeroBean, position: Int, hasKuoJianClicked: Boolean = false) {

        var rect = when (position) {
            0 -> Config.zhandou_hero1CheckRect
            1 -> Config.zhandou_hero2CheckRect
            else -> Config.zhandou_hero3CheckRect
        }
        if (heroBean.heroName == "muqiu") {//木球不走检测逻辑，提升速度
            MRobot.singleClick(MPoint(rect.clickPoint.x, rect.clickPoint.y + 25))
            afterHeroClick(heroBean)
            return
        }

        if (heroBean.isInCar() || !heroBean.needCar || heroBean.isGongCheng || carDoing.hasOpenSpace()) {
            //英雄在车上，或者此卡不需要车位,或者该卡是工程卡（暂时一套阵容不会有带两个工程），或者车上有空位
//            MRobot.singleClick(MPoint(rect.clickPoint.x, rect.clickPoint.y + 25))
            doUpHeroDeal(rect)
        } else {
            //需要开格子
            if (!Config.rect4KuojianColor.hasColor(Color.RED)) {//可以开格子
                log("点击扩建")
                doUpHeroDeal(rect, true)
//                MRobot.singleClick(Config.zhandou_kuojianPoint)//点扩建
//                delay(50)
//                MRobot.singleClick(MPoint(rect.clickPoint.x, rect.clickPoint.y + 25))
            } else {
                var changeOne = changeHeroWhenNoSpace(heroBean)
                if (changeOne != null) {//钱不够扩建时，是否需要替换已在车上的卡
                    log("没钱扩建，替换英雄")
                    carDoing.downHero(changeOne)
                    delay(50)
                    doUpHeroDeal(rect)
//                    MRobot.singleClick(MPoint(rect.clickPoint.x, rect.clickPoint.y + 25))
                } else {//不替换就等钱够
                    while (Config.rect4KuojianColor.hasColor(Color.RED)) {
                        delay(50)
                    }
                    log("点击扩建")
                    doUpHeroDeal(rect, true)
//                    MRobot.singleClick(Config.zhandou_kuojianPoint)//点扩建
//                    delay(50)
//                    MRobot.singleClick(MPoint(rect.clickPoint.x, rect.clickPoint.y + 25))
                }
            }
        }
        carDoing.addHero(heroBean)

        //满星后加一步验证操作，时常出现认为满了，实际未满的情况
//        if(heroBean.isFull()){
//            heroBean.checkStarLevelUseCard(carDoing)
//        }

        log("英雄上阵:${heroBean.heroName} 位置:${heroBean.position} 等级:${heroBean.currentLevel}")
//        delay(100)//这里略微等待一下，否则点完上卡，立马点刷新可能还会识别到
//        delay(100)//这里略微等待一下，否则点完上卡，立马点刷新可能还会识别到,而且这里如果不延迟，可能点刷新太快了，几乎是点了上卡马上点刷新，可能没点上刷新（因为日志体现了好几组点完刷新后1秒超时无法识别的，大概率就是 点刷新没有效果）
//        //这里还有一组日志是 上了猫咪点刷新然后识别到了猫咪，其他两个都是null。推测：点了猫咪 去点刷新（没点出效果）所以没有立即触发刷新卡牌，点击猫咪 猫咪自己有个动画，其他两个就消失了，所以这个时候会再次识别到猫咪，导致310猫咪不满
//        //如果这里还有问题，就延迟加大一点，但50后点刷新不出问题应该就不会有问题了，因为以前刷不出需要的卡，然后去点刷新，也没有被残影破坏过识别，点了刷新还会延迟100毫秒才开始识别呢
        afterHeroClick(heroBean)


////        MRobot.singleClick(rect.clickPoint)//点击卡片 //这里点击卡片可能刚好点中end得确定按钮，导致检测不到结束
//        MRobot.singleClick(MPoint(rect.clickPoint.x, rect.clickPoint.y + 25))
//        delay(Config.delayNor)
//        //如果正在下卡，弹窗会挡住，识别不到英雄，就等于认为已经上卡了
//        while (carDoing.downing) {
//            delay(50)
//        }
//        var hs = doGetPreHeros()
//
//        if (hs == null) {//上车了(点击后再检验，目标区域不含英雄了）
//            carDoing.addHero(heroBean)
//            log("英雄上阵:${heroBean.heroName} 位置:${heroBean.position} 等级:${heroBean.currentLevel}")
//            afterHeroClick(heroBean)
//        } else {//上不去，没格子了(如何是换卡，在这之前已经下了卡了，下了卡就能上去，所以这里只会因为没有格子而上不去，所以点扩建再尝试上卡
//            logOnly("英雄未上阵")
//            if (Config.rect4KuojianColor.hasWhiteColor()) {
//                logOnly("尝试点击一次扩建")
//                MRobot.singleClick(Config.zhandou_kuojianPoint)//点扩建
//                delay(Config.delayNor)
//                doUpHero(heroBean, position, true)
//            } else {
//                var changeOne = changeHeroWhenNoSpace(heroBean)
//                if (changeOne != null) {
//                    logOnly("没钱扩建，替换英雄")
//                    carDoing.downHero(changeOne)
//                    delay(delayNor)
//                    doUpHero(heroBean, position)
//                } else {
//                    logOnly("再次尝试点击扩建")
//                    while (!Config.rect4KuojianColor.hasWhiteColor()) {
//                        delay(50)
//                    }
//                    MRobot.singleClick(Config.zhandou_kuojianPoint)//点扩建
//                    delay(Config.delayNor)
//                    doUpHero(heroBean, position, true)
//                }
//            }
////            if (!hasKuoJianClicked) {//有钱扩建就先不替换
////                logOnly("尝试点击一次扩建")
////                MRobot.singleClick(Config.zhandou_kuojianPoint)//点扩建
////                delay(Config.delayNor)
////                doUpHero(heroBean, position, true)
////            } else {
////                var changeOne = changeHeroWhenNoSpace(heroBean)
////                if (changeOne != null) {
////                    logOnly("没钱扩建，试试要不要替换英雄")
////                    carDoing.downHero(changeOne)
////                    delay(delayNor)
////                    doUpHero(heroBean, position)
////                } else {
////                    logOnly("再次尝试点击扩建")
////                    MRobot.singleClick(Config.zhandou_kuojianPoint)//点扩建
////                    delay(Config.delayNor)
////                    doUpHero(heroBean, position, true)
////                }
////            }
//        }

    }

    suspend fun doGetPreHeros() = suspendCancellableCoroutine<List<HeroBean?>?> {
        GlobalScope.launch {
            val img = getImage(App.rectWindow)
            val hero1 =
                async { getHeroAtRect(img.getSubImage(Config.zhandou_hero1CheckRect), 0) }

            val hero2 =
                async { getHeroAtRect(img.getSubImage(Config.zhandou_hero2CheckRect), 1) }
            val hero3 =
                async { getHeroAtRect(img.getSubImage(Config.zhandou_hero3CheckRect), 2) }

            val h1 = hero1.await()
            val h2 = hero2.await()
            val h3 = hero3.await()
            if (h1 == null && h2 == null && h3 == null) {
                it.resume(null)
            } else {
                it.resume(arrayListOf(h1, h2, h3))
            }
        }
    }

    open suspend fun getPreHeros(timeout: Long = 2300) = suspendCancellableCoroutine<List<HeroBean?>?> {
        val startTime = System.currentTimeMillis()
        GlobalScope.launch {
            var h1: HeroBean? = null
            var h2: HeroBean? = null
            var h3: HeroBean? = null

            try {
                withTimeout(timeout) {
                    while (h1 == null || h2 == null || h3 == null) {
                        if (!running) {
                            break
                        }
                        val img = getImage(App.rectWindow)
                        val hero1 = if (h1 == null) {
                            async { getHeroAtRect(img.getSubImage(Config.zhandou_hero1CheckRect), 0) }
                        } else null
                        val hero2 = if (h2 == null) {
                            async { getHeroAtRect(img.getSubImage(Config.zhandou_hero2CheckRect), 1) }
                        } else null
                        val hero3 = if (h3 == null) {
                            async { getHeroAtRect(img.getSubImage(Config.zhandou_hero3CheckRect), 2) }
                        } else null

                        if (h1 == null) {
                            h1 = hero1?.await()
                            if (h1 != null) {
                                log("识别到英雄:${h1?.heroName} 耗时：${System.currentTimeMillis() - startTime}")
                            }
                        }
                        if (h2 == null) {
                            h2 = hero2?.await()
                            if (h2 != null) {
                                log("识别到英雄:${h2?.heroName} 耗时：${System.currentTimeMillis() - startTime}")
                            }
                        }
                        if (h3 == null) {
                            h3 = hero3?.await()
                            if (h3 != null) {
                                log("识别到英雄:${h3?.heroName} 耗时：${System.currentTimeMillis() - startTime}")
                            }
                        }
//                        var i=0;
//                        if(h1!=null)i++
//                        if(h2!=null)i++
//                        if(h3!=null)i++
//                        if(i>=2)break
//                        if (h1 == null || h2 == null || h3 == null) {//省去最后的100ms
//                            delay(50)
//                        }
                    }
                    logOnly("getPreHeros cost time:${System.currentTimeMillis() - startTime}")
                    it.resume(arrayListOf(h1, h2, h3))
                }
            } catch (e: Exception) {
                if (h1 == null && h2 == null && h3 == null) {
                    it.resume(null)
                } else {
                    it.resume(arrayListOf(h1, h2, h3))
                }
            }
        }
    }

    //原来上卡判断是否上去了，耗时189ms，识别手卡200ms-500ms不等，日志大部分在400ms左右，
    //更改3卡分别截图到只截图一张大图后（截图的api内部是同步的,所以即使使用时加了async和await，但截图的耗时还是线性想加的，只不过对比hero的时候是并行的)，
    // 等待测试结果。像上面189，单次识别就要40多ms，里面还有个delay30ms的。所以两轮过去（一般两三轮比较就差不多知道上去没有了），大约就是3个40+2个30，大约就是180左右
    //所以日志大部分上卡耗时都是189左右，剩下的就是比较的时间，可见比较耗时贼少，主要还是截图浪费了时间。
    open suspend fun getHeroAtRect(img: BufferedImage, position: Int) = suspendCancellableCoroutine<HeroBean?> {
//        val hero = heros.filter {//满了就不会出现在预选卡里，减少比较
//            !it.isFull()
//        }.firstOrNull {
////            ImgUtil.isImageSim(img, it.img)
////            ImgUtil.isHeroSim(img,it.img)
//            it.fitImage(img, position)
//        }
        val hero = heros.firstOrNull {
//            ImgUtil.isImageSim(img, it.img)
//            ImgUtil.isHeroSim(img,it.img)
            it.fitImage(img, position)
        }
        if (Config.debug) {
            log(img)
        }
        if (hero != null) {
            logOnly("getHeroAtRect ${hero?.heroName ?: "无结果"}")
        } else logOnly("getHeroAtRect null")
        it.resume(hero)

    }

    suspend fun defaultDealHero(heros: List<HeroBean?>, heroSorted: List<HeroBean>, luanbu: Boolean = false): Int {
        heroSorted.forEach {
            var index = heros.indexOf(it)
            if (index > -1) {
                if (it.heroName == "guangqiu") {
                    if (luanbu || carDoing.hasNotFull()) {
                        return index
                    } else {
                        return -1
                    }
                }
                return index
            }
        }
        return -1
    }

    open override fun onGuanChange(guan: Int) {
    }

    open suspend fun isKeyDownNeed(code: Int): Boolean {
        return false
    }

    val doubleClickDelay = 200L
    var lastKeyCode = -1
    var lastKeyPressTime = 0L
    private var clickTimer: Timer? = null

    override suspend fun onKeyDownOri(keyCode: Int): Boolean {
        val currentTime = System.currentTimeMillis()
        // 判断是否为同一按键的快速连续按下
        if (keyCode == lastKeyCode &&
            currentTime - lastKeyPressTime <= doubleClickDelay
        ) {
            logOnly("双击耗时:${currentTime - lastKeyPressTime}")
            // 双击事件
            GlobalScope.launch {
                onKeyDoubleDown(keyCode)
            }
            // 重置状态
            resetClickState()
        } else {
            // 单击事件 - 延迟执行，等待可能的第二次点击
            scheduleSingleClick(keyCode, currentTime)
        }


        return isKeyDownNeed(keyCode)
    }

    /**
     * 安排单击事件的执行
     */
    private fun scheduleSingleClick(keyCode: Int, pressTime: Long) {
        // 取消之前的单击定时器
        clickTimer?.cancel()

        // 更新按键状态
        lastKeyCode = keyCode
        lastKeyPressTime = pressTime

        // 创建新的定时器检测是否为单击
        clickTimer = Timer()
        clickTimer?.schedule(timerTask {
            // 时间到了仍未收到第二次点击，判定为单击
            GlobalScope.launch {
                onKeyDown(keyCode)
            }
            resetClickState()
        }, doubleClickDelay)
    }

    /**
     * 重置点击状态
     */
    private fun resetClickState() {
        lastKeyCode = -1
        lastKeyPressTime = 0L
        clickTimer?.cancel()
        clickTimer = null
    }

    open suspend fun onKeyDown(code: Int): Boolean {
        return false
    }

    open suspend fun onKeyDoubleDown(code: Int) {

    }

    suspend fun keyDownHero(code: Int): Boolean {
        var downIndex = when (code) {
            KeyEvent.VK_NUMPAD2 -> 0
            KeyEvent.VK_NUMPAD1 -> 1
            KeyEvent.VK_NUMPAD5 -> 2
            KeyEvent.VK_NUMPAD4 -> 3
            KeyEvent.VK_NUMPAD8 -> 4
            KeyEvent.VK_NUMPAD7 -> 5
            KeyEvent.VK_NUMPAD0 -> 6
            else -> {
                -1
            }
        }
        if (downIndex > -1) {
            waiting = true
            carDoing.downPosition(downIndex)
            return true
        }
        return false
    }
}