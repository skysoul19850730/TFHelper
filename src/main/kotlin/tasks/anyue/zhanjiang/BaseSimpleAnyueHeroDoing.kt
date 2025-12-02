package tasks.anyue.zhanjiang

import MainData.job79TimeDt
import data.HeroBean
import getImage
import kotlinx.coroutines.*
import log
import logOnly
import tasks.SimpleHeZuoHeroDoing
import tasks.XueLiang
import ui.zhandou.UIKeyListenerManager
import utils.AYUtil
import java.awt.event.KeyEvent
import java.awt.image.BufferedImage
import kotlin.math.abs

abstract class BaseSimpleAnYueHeroDoing() : SimpleHeZuoHeroDoing(), UIKeyListenerManager.UIKeyListener {

    var beimu = false
    var beiDiJing = false

    var hero19: HeroBean? = null
    var heroXiaoye: HeroBean? = null

    var DownLoadPositionFromKeyFor19 = -1
    var DownLoadPositionFromKeyFor39 = -1


    override suspend fun onKeyDown(code: Int): Boolean {
        //如果龙王识别出错可以按快捷下对应卡牌，但不知道快捷键按下得时间，所以不能延时进行上卡，只能快捷键9来恢复上卡

        if (code == KeyEvent.VK_NUMPAD9) {//9强制改变waitting，防止waiting有逻辑错误不上卡
            waiting = !waiting
            return false
        }

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

            if (DownLoadPositionFromKeyFor39 > -1) {
                down39(null, arrayListOf(DownLoadPositionFromKeyFor39))
            }

            return true
        }

        if (guankaTask?.currentGuanIndex == 19 || guankaTask?.currentGuanIndex == 18) {//19
            DownLoadPositionFromKeyFor19 = -1
            DownLoadPositionFromKeyFor19 = when (code) {
                KeyEvent.VK_NUMPAD2 -> 0
                KeyEvent.VK_NUMPAD1 -> 1
                KeyEvent.VK_NUMPAD5 -> 2
                KeyEvent.VK_NUMPAD4 -> 3
                KeyEvent.VK_NUMPAD8 -> 4
                KeyEvent.VK_NUMPAD7 -> 5
                KeyEvent.VK_NUMPAD0 -> 6
                KeyEvent.VK_NUMPAD3 -> 100
                else -> {
                    return false
                }
            }
            return true
        }
        return false
    }

    var g19Obeserver = false

    var mG19Job: Job? = null
    fun stop19Oberserver() {
        g19Obeserver = false
        mG19Job?.cancel()
        mG19Job = null
    }

    open fun onHeroPointByG19(hero: HeroBean): Boolean {
        return true
    }

    fun start19Oberserver(isNew: Boolean = false) {
        if (g19Obeserver) return
        g19Obeserver = true

        mG19Job = GlobalScope.launch {
            while (g19Obeserver) {
                //4 100 300 370
                var img = getImage(App.rectWindow, null)

                if (DownLoadPositionFromKeyFor19 > -1) {
                    log("g19 ：接收到快捷键事件")
                    if (DownLoadPositionFromKeyFor19 < 100) {//100代表对面车
                        var hero = carDoing.carps[DownLoadPositionFromKeyFor19].mHeroBean
                        if (hero != null && onHeroPointByG19(hero)) {
                            if (!isNew) {
                                waiting = true
                            }
                            carDoing.downHero(hero)
                            if (!isNew) {
                                preHero(null)//上地精
                            }
                            //检测到没有被选中为止
                            dijingIng = true

                            var canDownDijing = false
                            var overred = false
                            var canDownDijingDelay = false
//                                delay(500)//这里给个下卡时间，再检测有没有点名结束，否则如果正好下卡点出了卡片，这时检测红圈和蓝色方块来确定有没有结束点名就不正确了
                            while (!canDownDijing && !canDownDijingDelay) {
                                delay(50)
                                var img2 = getImage(App.rectWindow, null)
                                canDownDijing = carDoing.carps.get(DownLoadPositionFromKeyFor19).over19Selected(img2)
                                if (!canDownDijing && !overred) {
                                    var reding3 =
                                        carDoing.carps.get(DownLoadPositionFromKeyFor19).isAy19SelectedV2(img2)
                                    if (!reding3) {
                                        log("没有识别到19点名一次结束,检测到没有红圈了，1秒后下卡，1秒内检测到蓝色over就立马下")
                                        overred = true
                                        GlobalScope.launch {
                                            delay(1000)
                                            canDownDijingDelay = true
                                        }

                                    }
                                }
                            }
//                                delay(2500)
                            delay(500)
                            dijingIng = false
                            if (!isNew) {
                                waiting = false
                            }

                        }
                    }
                    onChuanZhangPoint(img)
                    DownLoadPositionFromKeyFor19 = -1
                } else {

                    var index = carDoing.getAY19Selected(img)
                    if (index > -1) {
                        var hero = carDoing.carps.get(index).mHeroBean
                        if (hero != null) {
                            if (onHeroPointByG19(hero)) {
                                log("检测到被标记  位置：$index  英雄：${hero?.heroName}")
                                if (!isNew) {
                                    waiting = true
                                }
                                carDoing.downHero(hero)
                                if (!isNew) {
                                    preHero(null)//上地精
                                }
                                log("下卡后，dijing设置为true")
                                dijingIng = true

                                var canDownDijing = false
                                var overred = false
                                var canDownDijingDelay = false
//                                delay(500)//这里给个下卡时间，再检测有没有点名结束，否则如果正好下卡点出了卡片，这时检测红圈和蓝色方块来确定有没有结束点名就不正确了
                                try {
                                    withTimeout(4000) {
                                        while (!canDownDijing && !canDownDijingDelay) {
                                            delay(50)
                                            var img2 = getImage(App.rectWindow, null)
                                            canDownDijing = carDoing.carps.get(index).over19Selected(img2)
                                            if (!canDownDijing && !overred) {
                                                var reding3 = carDoing.carps.get(index).isAy19SelectedV2(img2)
                                                if (!reding3) {
                                                    log("没有识别到19点名一次结束,检测到没有红圈了，1秒后下卡，1秒内检测到蓝色over就立马下")
                                                    overred = true
                                                    GlobalScope.launch {
                                                        delay(1000)
                                                        canDownDijingDelay = true
                                                    }

                                                }
                                            }
                                        }
                                    }
                                } catch (e: Exception) {
                                    log("没有识别到19点名一次结束,也未检测到红圈消失，超时后下卡继续")
                                }
                                delay(500)
                                dijingIng = false
                                if (!isNew) {
                                    waiting = false
                                }
                            } else {//比如点的战将，那么这里就延迟下，否则会一直循环的
                                delay(2200)
                            }

                        }
                        onChuanZhangPoint(img)
                        DownLoadPositionFromKeyFor19 = -1
                    }
                }
            }
        }
    }

    protected var dijingIng = false //true 上地精  false 下地精

    private suspend fun onChuanZhangPoint(img2: BufferedImage? = null) {
//        var img = img2 ?: getImage(App.rectWindow)
        logOnly("船长点名啦")
//        img.saveTo(File(App.caijiPath, "${System.currentTimeMillis()}.png"))//目前稳定，不用再采集了
    }

    override suspend fun doAfterHeroBeforeWaiting(heroBean: HeroBean) {
        if (heroBean.heroName == "muqiu") {
            delay(500)
            return
        }
        super.doAfterHeroBeforeWaiting(heroBean)
    }

    suspend fun deal19(heros: List<HeroBean?>): Int {
        var curG = guankaTask?.currentGuanIndex ?: 0
        val dijing = hero19 ?: run {
            waiting = true
            return -1
        }
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
                log("找到地精了")
                while (!dijingIng && curG < 20) {//加管卡，避免卡死。超过20关就不会再卡了
                    delay(50)
                    log("等待地精变为true")
                    curG = guankaTask?.currentGuanIndex ?: 0
                }
                if (curG >= 20) {
                    return -1//如果超过20关，就直接返回-1，否则这会base里不会下19点名牌（boss结束了），就没空位，这里返回地精就上不去。。。。
                }
                log("地精true了，赶紧上地精")
                return index
            } else {//地精不在车上，且预选里没地精，就一定有以下几个英雄,随便上哪个继续找地精
                var ttheros = carDoing.carps.filter { it.mHeroBean != null && it.mHeroBean != dijing }.map {
                    it.mHeroBean!!
                }
                return defaultDealHero(heros, ttheros)
            }


        }
        return -1
    }

    var heros39Up4 = arrayListOf<HeroBean>()

    suspend fun deal39(heros: List<HeroBean?>): Int {
        //车满的时候就等血量变低就上卡，都可以上。
        //上卡过程中，点了卡，血量没变时，只能上车上的卡，血量变了就可以上所有的卡
        if (guankaTask?.currentGuanIndex ?: 0 > 39) {
            return -1
        }
        freshStatus39()
        if (status39 == 2) {//刚掉完血，啥都可以上，下次会再次刷39status
            if (carDoing.hasNotFull() || carDoing.hasOpenSpace()) {
                return defaultDealHero(heros, heros39Up4)
            } else {//都满了就等点名（第一次也走这里，因为status39默认赋值为2）
                while (status39 != 1 && curGuan < 40) {
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
                while (status39 != 2 && curGuan < 40) {
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
            delay(2000)//这里新增下延迟，如果延迟后关卡已经变了，证明已经速杀了，不再下卡
            if(curGuan>39)return
            down39(img, pos)
        }
        if (abs(xue - lastXue) > 0.03) {//上卡过程中撞过了，没撞过就不用继续识别
            log("血量发生变化，所有卡都上")
            delay(200)
            status39 = 2
        }
    }

    suspend fun down39(img2: BufferedImage?, pos: List<Int>) {
        var img = img2 ?: getImage(App.rectWindow);
        lastXue = XueLiang.getXueLiang(img)//记录点名时的血量
        pos.forEach {
            carDoing.downPosition(it)
        }
        status39 = 1
        log("监听到点名了，只有车上的可以上")
    }

    override fun onStart() {
        super.onStart()
        UIKeyListenerManager.addKeyListener(this)
    }

    override fun onStop() {
        super.onStop()
        UIKeyListenerManager.removeKeyListener(this)
        g19Obeserver = false
        App.stopAutoSave()
    }


    private var job79: Job? = null
    private var job79StartTime = 0L
    var xiaoyeJumping = false

    private suspend fun beiye() {
        val xiaoye = heroXiaoye ?: return
        carDoing.downHero(xiaoye)
        preHero(xiaoye)
        waiting = false
    }

    private suspend fun shangye() {
        preHero(null)
        job79StartTime = System.currentTimeMillis()
    }

    fun tiaoye79() {
        val xiaoye = heroXiaoye ?: return
        xiaoyeJumping = true
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
            delay(1600)
            if (guankaTask?.currentGuanIndex == 99 || guankaTask?.currentGuanIndex == 98) {
                //99多8秒废话
                delay(8000)
            }
            job79StartTime = System.currentTimeMillis()
            while (xiaoyeJumping) {
                delay(2000)
//                App.save()//如果现在计算是准的，那这里应该可以只截图一张扑克牌，不用autosave，那样保存太多图片，如果不准，则需要继续使用autosave来观察规则
                beiye()
                var a = 8000
                while (a - (System.currentTimeMillis() - job79StartTime) + job79TimeDt.value > 0) {
                    delay(50)
                    if (tiaozhengXiaoye) {
                        a = 5000
                    }
                }
//                if(tiaozhengXiaoye){//可以根据截图，调整这里时间，比如99打完第一段，boss开始讲话到释放攻击的时间，上面的while就遇到 tiaozhengxiaoye时直接中断重新计时delay时间，后面就继续保持之前节奏了
//                    delay(3000)
//                }
                tiaozhengXiaoye = false
                shangye()
            }
        }
    }

    var tiaozhengXiaoye = false

    fun stopTiaoye79() {
        xiaoyeJumping = false
        job79?.cancel()
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
            onKeyDown(KeyEvent.VK_NUMPAD3)
        }
    }

    override fun onGuanFix(guan: Int) {
        guankaTask?.setCurGuanIndex(guan)
    }

}