package tasks.hanbing2

import data.Config
import data.HeroBean
import data.MRect
import getImage
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import log
import logOnly
import saveTo
import tasks.SimpleHeZuoHeroDoing
import tasks.XueLiang
import ui.zhandou.UIKeyListenerManager
import utils.HBUtil
import utils.ImgUtil.forEach4Result
import java.awt.event.KeyEvent
import java.awt.image.BufferedImage
import java.io.File

abstract class BaseSimpleHBHeroDoing() : SimpleHeZuoHeroDoing(), UIKeyListenerManager.UIKeyListener {

    var beimu = false

    var heroDoNotDown129: HeroBean? = null


    var chuanzhangDownLoadPositionFromKey = -1
    var longWangDownLoadPositionFromKey = -1

    var position199 = -1

    override fun onChuanzhangClick(position: Int) {
        chuanzhangDownLoadPositionFromKey = position
    }

    override suspend fun onKeyDown(code: Int): Boolean {
        //如果龙王识别出错可以按快捷下对应卡牌，但不知道快捷键按下得时间，所以不能延时进行上卡，只能快捷键9来恢复上卡

        if (code == KeyEvent.VK_NUMPAD9) {//9强制改变waitting，防止waiting有逻辑错误不上卡
            waiting = !waiting
            return true
        }

        if (guankaTask?.currentGuanIndex == 129 || guankaTask?.currentGuanIndex == 128 || guankaTask?.currentGuanIndex == 127 ||
            guankaTask?.currentGuanIndex == 99 || guankaTask?.currentGuanIndex == 98 || guankaTask?.currentGuanIndex == 97
        ) {//船长
            chuanzhangDownLoadPositionFromKey = -1
            chuanzhangDownLoadPositionFromKey = when (code) {
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


        if (guankaTask?.currentGuanIndex == 199 || guankaTask?.currentGuanIndex == 198) {
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

            if (position199 > -1) {
                return true
            }
        }
        if (guankaTask?.currentGuanIndex == 209 || guankaTask?.currentGuanIndex == 208) {
            click210Pos = when (code) {
                KeyEvent.VK_NUMPAD2 -> 0
                KeyEvent.VK_NUMPAD1 -> 1
                KeyEvent.VK_NUMPAD5 -> 2
                KeyEvent.VK_NUMPAD4 -> 3
                KeyEvent.VK_NUMPAD8 -> 4
                KeyEvent.VK_NUMPAD7 -> 5
                KeyEvent.VK_NUMPAD0 -> 6
                else -> -1
            }

            if (click210Pos > -1) {
                if(!carDoing.dianming210s.contains(click210Pos)) {
                    carDoing.dianming210s.add(click210Pos)
                }
                return true
            }
        }


        return false
    }

    var chuanZhangObeserver = false
    var chuanzhangDownCount = 0


    open fun onHeroPointByChuanzhang(hero: HeroBean): Boolean {
        return hero != heroDoNotDown129
    }

    var mChuanZhangJob: Job? = null
    fun stopChuanZhangOberserver() {
        chuanZhangObeserver = false
        mChuanZhangJob?.cancel()
        mChuanZhangJob = null
    }

    fun startChuanZhangOberserver() {
        if (chuanZhangObeserver) return
        chuanZhangObeserver = true

        mChuanZhangJob = GlobalScope.launch {
            while (chuanZhangObeserver) {
                //4 100 300 370


                var img = getImage(App.rectWindow, null)

                if (chuanzhangDownLoadPositionFromKey > -1) {
                    log("船长 ：接收到快捷键事件")
                    if (chuanzhangDownLoadPositionFromKey < 100) {//100代表对面车
                        var hero = carDoing.carps[chuanzhangDownLoadPositionFromKey].mHeroBean
                        if (hero != null && onHeroPointByChuanzhang(hero)) {
                            carDoing.downHero(hero)
                            waiting = false
                        }
                    }
                    onChuanZhangPoint(img)
                    chuanzhangDownLoadPositionFromKey = -1
                } else {

                    var index = carDoing.getChuanZhangMax(img)
                    var index2 = otherCarDoing.getChuanZhangMax(img)
//                    var index2:Pair<Int, Float>? = null
                    if (index != null || index2 != null) {
                        if (index != null && (index2 == null || index.second > index2.second || index.second > 0.2)) {
                            var hero = carDoing.carps.get(index.first).mHeroBean
                            log("检测到被标记  位置：$index  英雄：${hero?.heroName}")
                            if (hero != null && onHeroPointByChuanzhang(hero)) {
                                carDoing.downHero(hero)
                                waiting = false
                            }
                        }
                        onChuanZhangPoint(img)
                    } else {
                        var fitCount = 0
                        MRect.createWH(4, 100, 300, 370).forEach4Result { x, y ->
                            if (img.getRGB(x, y) == Config.Color_ChuangZhang.rgb) {
                                fitCount++
                            }
                            fitCount > 1000
                        }
                        if (fitCount > 1000) {
                            onChuanZhangPoint(img)
                        }
                    }
                }
            }
        }
    }
    fun startChuanZhangOberserver2() {
        if (chuanZhangObeserver) return
        chuanZhangObeserver = true

        mChuanZhangJob = GlobalScope.launch {
            while (chuanZhangObeserver) {
                //4 100 300 370


                delay(200)
                var img = getImage(App.rectWindow, null)

                if (chuanzhangDownLoadPositionFromKey > -1) {
                    log("船长 ：接收到快捷键事件")
                    if (chuanzhangDownLoadPositionFromKey < 100) {//100代表对面车
                        var hero = carDoing.carps[chuanzhangDownLoadPositionFromKey].mHeroBean
                        if (hero != null && onHeroPointByChuanzhang(hero)) {
                            carDoing.downHero(hero)
                            waiting = false
                        }
                    }
                    onChuanZhangPoint(img)
                    chuanzhangDownLoadPositionFromKey = -1
                } else {
                    val point = HBUtil.chuanzhang(img)

                    var index = carDoing.getChuanZhangPosition(point)
                    var index2 = otherCarDoing.getChuanZhangPosition(point)

                    if(index>-1 || index2>-1){
                        if(index>-1) {
                            var hero = carDoing.carps.get(index).mHeroBean
                            log("检测到被标记  位置：$index  英雄：${hero?.heroName}")
                            if (hero != null && onHeroPointByChuanzhang(hero)) {
                                carDoing.downHero(hero)
                                waiting = false
                            }
                        }else{
                            log("检测到被标记  位置另一辆车：$index ")
                        }
                        onChuanZhangPoint(img)
                    }else{
                        var fitCount = 0
                        MRect.createWH(4, 100, 300, 370).forEach4Result { x, y ->
                            if (img.getRGB(x, y) == Config.Color_ChuangZhang.rgb) {
                                fitCount++
                            }
                            fitCount > 600
                        }
                        if (fitCount > 600) {
                            onChuanZhangPoint(img)
                        }
                    }
                }
            }
        }
    }

    suspend fun onChuanZhangPoint(img2: BufferedImage? = null) {
        var img = img2 ?: getImage(App.rectWindow)
        logOnly("船长点名啦")
//        img.saveTo(File(App.caijiPath, "${System.currentTimeMillis()}.png"))//目前稳定，不用再采集了
        chuanzhangDownCount++
        var isSencodDianming = chuanzhangDownCount % 2 == 0
        if (!isSencodDianming) {//第一次点卡后等3秒再开始识别
            log("第一次点名,3秒后再开始监听")
            delay(3000)
        } else {//第二次点卡后 刷6秒补卡然后停止（这个时间慢慢校验)要撞船了
            if (chuanZhangObeserver) {
//                            delay(10000)
                log("第二次点名,刷卡7.5秒后，然后暂停刷卡")
                beimu = true
                dropThisDeal = true//重新刷新，不用之前的卡（可能被手动动过，比如爱神那里上下圣骑）
                waiting = false
                delay(7500)
                waiting = true
                log("第二次点名,已经刷卡7.5秒，暂停刷卡，10.5秒开启监听")
                GlobalScope.launch {
                    delay(13500)
                    beimu = false //waiting前，把beimu改为false
//                    waiting = false
                    log("第二次点名,暂停刷卡13.5秒后恢复刷卡")
                }
                delay(10500)//5秒后 效果消失，继续补卡，并监听点名
                log("第二次点名,延迟10.5秒,恢复监听")
//                waiting = false
            }
        }
    }

    suspend fun deal149(heros: List<HeroBean?>): Int {
        while (qiu149State == 1 && heroBean149!!.isInCar()) {
            delay(200)
        }
        if (qiu149State == 0) {
            carDoing.downHero(heroBean149!!)
        }
        var index = heros.indexOf(heroBean149)
        if (index < 0) {
            return index
        }
        while (qiu149State == 0) {
            delay(200)
        }
        if (qiu149State == 1) {
            GlobalScope.launch {
                delay(4000)
                qiu149State = 0

            }
            return index
        }
        if (qiu149State == 2) {
            if (heroBean149!!.isFull()) {
                waiting = true
                return -1
            }
            return index
        }
        return -1
    }

    var heroBean149: HeroBean? = null
    var qiu149State = 0//0下，1上，2结束
    var leishenOberser = false
    fun startLeishenOberserver() {
        if (leishenOberser) return
        leishenOberser = true
        var leishenStart = System.currentTimeMillis()
        var checkCount = 0
        GlobalScope.launch {
            delay(5000)//5秒左右才出球
            //按截图算，大约10秒一个球（9.5）
            while (leishenOberser) {

                var img = getImage(App.rectWindow)

                if (Config.leishenqiuXueTiaoRect.hasColorCount(
                        Config.leishenqiuXueTiao, testImg = img
                    ) > 50 || Config.leishenqiuXueTiaoRect.hasColorCount(Config.leishenqiuXueTiao2, testImg = img) > 50
                ) {

                    var count = Config.rectCheckOfLeishen.hasColorCount(Config.colorLeishenHongqiu, testImg = img)
                    if (count > 200) {
                        onLeiShenRedBallShow()
                        log("检测到红球")
                        log(img)
                        checkCount++
                        if (checkCount == 6) {
                            onLeiShenSixBallOver()
                            leishenOberser = false
                            var time = System.currentTimeMillis()
                            log("识别完6个球，耗时：${time - leishenStart} ms")
                        }
                        delay(5000)
                    } else {
                        //其实如果外层的血条逻辑足够准的话（因为不知道血条会不会被挡死，但挡死也就进不来了）.这里就不用再判断了，毕竟蓝色判断不准
                        //即：如果有血条那么一定有球，如果不是红球，则就是蓝球。（经过实验，红球的判断相对很准，无非就是调下数值，如果被挡的厉害,反正如果不是红球，待检测区域redcount基本都是0)
                        var count2 = Config.rectCheckOfLeishen.hasColorCount(Config.colorLeishenLanqiu, testImg = img)
                        if (count2 > 2000) {
                            onLeiShenBlueBallShow()
                            log("检测到蓝球")
                            log(img)
                            checkCount++
                            if (checkCount == 6) {
                                onLeiShenSixBallOver()
                                leishenOberser = false
                                var time = System.currentTimeMillis()
                                log("识别完6个球，耗时：${time - leishenStart} ms")
                            }
                            delay(5000)
                        } else {
                            delay(20)
                        }
                    }

                }
            }
        }
    }

    open fun onLeiShenSixBallOver() {
        GlobalScope.launch {
            delay(10000)
            qiu149State =2
        }
    }

    open fun onLeiShenRedBallShow() {
        qiu149State = 1
    }

    open fun onLeiShenBlueBallShow() {
        qiu149State = 0
    }


    //x  400-700  y 300, wh 60

//    private fun is199Bai(image: BufferedImage)

    var step199Super = 0//0准备打白球阶段，1//打成白球后，2//点名阶段。点名完成后回到0

    var bai = false
    private var baiqiuCountSuper = 0
    suspend fun deal199Super(heros: List<HeroBean?>): Int {
        if (step199Super == 0) {
            bai = false
            var deal = deal199Step0()
            if (deal.isOver.invoke()) {
                step199Super = 1
                return -1
            } else {
                return deal.chooseHero.invoke(heros)
            }
        } else if (step199Super == 1) {

            while (!bai) {
                delay(100)
                var img = getImage(App.rectWindow)
                bai = HBUtil.is199Bai(img)
            }
            //打成了白球

            var deal = deal199Step1()
            if (deal.isOver.invoke()) {
                //打白//例如副卡下战将
                XueLiang.observerXueDown()
                log("白球撞上了，进入step2")

                baiqiuCountSuper++
//                if (baiqiuCountSuper < 1) {
                    GlobalScope.launch {
                        delay(32000)
                        step199Super = 3
                    }
//                }else{
//                    step199Super = 3
//                    return -1
//                }
                step199Super = 2
                return deal199Step2().chooseHero.invoke(heros)

            } else {
                return deal.chooseHero.invoke(heros)
            }
        } else if(step199Super==2) {
            val deal = deal199Step2()
            var dianmingIndex = carDoing.getHB199Selected()

            if (position199 > -1 || dianmingIndex > -1) {
                carDoing.downPosition(position199)
                carDoing.downPosition(dianmingIndex)
                position199 = -1
            }


            if (carDoing.hasAllOpenSpace() || carDoing.hasNotFull()) {
                return deal.chooseHero.invoke(heros)
            } else {
                while (step199Super == 2) {
                    var dianmingIndex = carDoing.getHB199Selected()
                    if (position199 > -1 || dianmingIndex > -1) {
                        carDoing.downPosition(position199)
                        carDoing.downPosition(dianmingIndex)
                        position199 = -1
                        return deal.chooseHero.invoke(heros)
                    }
                    delay(100)
                }
                return -1
            }
        }else{
            val deal = deal199Step3()
            return deal.chooseHero.invoke(heros)
        }
    }

    open fun deal199Step0(): GuanDeal {
        return GuanDeal(1)
    }

    open fun deal199Step1(): GuanDeal {
        return GuanDeal(1)
    }

    open fun deal199Step2(): GuanDeal {
        return GuanDeal(1)
    }
    open fun deal199Step3(): GuanDeal {
        return GuanDeal(1)
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



    open fun onXiongMaoQiuGot(qiu: String) {
        log("熊猫识别到球：$qiu")
        hasQiu = true
    }

    var xiongmaoOberserver = false
    var hasQiu = false
    fun startXiongMaoOberser() {
        if (xiongmaoOberserver) return
        xiongmaoOberserver = true
        var leishenStart = System.currentTimeMillis()
        var checkCount = 0
        GlobalScope.launch {
            //按截图算，大约10秒一个球（9.5）
            while (xiongmaoOberserver) {

                var img = getImage(App.rectWindow)
                hasQiu = false
                if (Config.xiongmaoQiuRect.hasColorCount(
                        Config.xiongmaoFS, testImg = img
                    ) > 50
                ) {
                    onXiongMaoQiuGot("fs")
                } else if (Config.xiongmaoQiuRect.hasColorCount(
                        Config.xiongmaoGJ, testImg = img
                    ) > 50
                ) {
                    onXiongMaoQiuGot("gj")
                } else if (Config.xiongmaoQiuRect.hasColorCount(
                        Config.xiongmaoZS, testImg = img
                    ) > 50
                ) {
                    onXiongMaoQiuGot("zs")
                } else if (Config.xiongmaoQiuRect.hasColorCount(
                        Config.xiongmaoSS, testImg = img
                    ) > 50
                ) {
                    onXiongMaoQiuGot("ss")
                }

                if (hasQiu) {//识别到一个球后，延迟5秒再识别，节省
                    delay(7000)
                }
            }
        }
    }



    var click210Pos = -1
    var lastClick210Pos = -1
    var upList210 :ArrayList<HeroBean> = arrayListOf()

    fun addGuan210(upList: List<HeroBean>){
        upList210.clear()
        upList210.addAll(upList)
        addGuanDeal(209){
            over { false }
            chooseHero {
                deal210(this)
            }
            onStart {

                GlobalScope.launch {
                    delay(10000)
                    while(curGuan<210 && running){

                        var index = carDoing.getHB210Selected()
                        if(index>-1){
                            click210Pos = index
                            delay(25000)//30秒一个黑洞
                        }else{
                            delay(500)
                        }

                    }

                }

            }
            des = "哪里被标记为黑洞就点哪里"
        }
    }


    private suspend fun deal210(heros: List<HeroBean?>): Int {

        if (!upList210.all {
                it.isFull()
            }) {
            return heros.upAny(*upList210.toTypedArray())
        }

        while (click210Pos == -1 || lastClick210Pos == click210Pos) {
            delay(200)
        }
        val carPos = carDoing.carps.get(click210Pos)
        val hero = carPos.mHeroBean
        if (hero != null) {
            carDoing.resetHero(hero)
            carPos.isUnEnable = true

            val lastH = upList210.last()
            carDoing.downHero(lastH)
            upList210.remove(lastH)
            lastClick210Pos = click210Pos
            return heros.upAny(*upList210.toTypedArray())
        } else {
            lastClick210Pos = click210Pos
        }
        return -1

    }


    override fun onStart() {
        super.onStart()
        UIKeyListenerManager.addKeyListener(this)
    }

    override fun onStop() {
        super.onStop()
        UIKeyListenerManager.removeKeyListener(this)
        chuanZhangObeserver = false
        App.stopAutoSave()
    }
}