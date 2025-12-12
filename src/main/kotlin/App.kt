import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import com.sun.jna.platform.win32.User32
import com.sun.jna.platform.win32.WinDef
import com.sun.jna.platform.win32.WinUser
import data.Config
import data.Config.pointClose
import data.MPoint
import data.MRect
import data.Recognize
import database.DataManager
import kotlinx.coroutines.*
import model.CarDoing
import tasks.Hero
import tasks.IGameLaunch
import tasks.anyue.zhanjiang.AyZhanNvGameLaunch
import tasks.duizhan.faqiang.FaQiangGameLaunch
import tasks.duizhan.longquan.LongQuanGameLaunch
import tasks.gameUtils.GameUtil
import tasks.hanbing.deprecated.mengyan.MengyanGameLaunch
import tasks.hanbing.zhanjiang.HBZhanNvGameLaunch
import tasks.hezuo.zhannvsha.ZhanNvGameLaunch
import tesshelper.Tess
import utils.AYUtil
import utils.ImgUtil
import utils.MRobot
import utils.Window
import java.awt.event.KeyEvent.*
import java.awt.image.BufferedImage
import java.io.File
import java.text.SimpleDateFormat
import javax.imageio.ImageIO
import kotlin.math.abs

object App {
    var reCheckStar = false

    var caijing = mutableStateOf(false)
    var timerText = mutableStateOf("定时关闭")
    var windowClose = mutableStateOf(0)
    var state = mutableStateOf(0)
    var tfWindow: WinDef.HWND? = null

    //    var windowRect :MRect = MRect.createWH(0,0,1000,607)
    var rectWindow: MRect = MRect.createWH(0, 0, 1000, 607)
    val caijiPath = Config.caiji_main_path + "\\window"


    val model_duizhan = 0x00100000
    val model_hezuo = 0x00200000
    val model_hanbing = 0x00400000
    val model_anyue = 0x00800000
    val model_xuanwo = 0x01000000

    val modelCaiji = -2
    val model_faqiang = model_duizhan or 0x00000001
    val model_longquan = model_duizhan or 0x00000002


    val model_hezuo_zhannv = model_hezuo or 0x00000001

    val model_hanbing_zhannv = model_hanbing or 0x00000001
//    val model_hanbing_zhannv4 = model_hanbing or 0x00000004
    val model_hanbing_5zhan_ziqiang = model_hanbing or 0x00000008
    val model_hanbing_5zhan_boer = model_hanbing or 0x00000010
    val model_hanbing_huoling_boer = model_hanbing or 0x00000011
    val model_hanbing_mengyan = model_hanbing or 0x00000002

    val model_hanbing_5zhan_ziqiang_fuka = model_hanbing or 0x10000008

    val model_anyue_zhanjiang = model_anyue or 0x00000001
    val model_anyue_5zhan = model_anyue or 0x00000002
    val model_anyue_bo = model_anyue or 0x00000003

    val model_xuanwo_wawangbo = model_xuanwo or 0x00000001
    val model_xuanwo_wawangziqiang = model_xuanwo or 0x00000002
    val model_xuanwo_huolingbo = model_xuanwo or 0x00000004

    val _launchModel = mutableStateOf(model_duizhan)
    var mLaunchModel: Int
        set(value) {
            _launchModel.value = value
        }
        get() {
            return _launchModel.value
        }


    var gameLaunch: IGameLaunch? = null
    var mInited = false

    var thisWindow: WinDef.HWND? = null

    var keyListeners = arrayListOf<KeyListener>()


    interface KeyListener {
        suspend fun onKeyDownOri(code: Int): Boolean
    }

    var closeCallBack: (() -> Unit)? = null
    fun initPath(callback: () -> Unit) {
        this.closeCallBack = callback
        var s = File(javaClass.getResource("")?.path ?: "").path
        s = s.substring(0, s.indexOf("build"))
        logOnly(s)
        Config.appRootPath = s
        logOnly("caiji path  ${Config.caiji_main_path}")
        logOnly("resPath ${Tess.resPath}")
        init()
    }

    fun init() {
        doSomeTest()
        findTfAndMoveTo00()
//        thisWindow = Window.findWindowWithName("塔防助手")
        addKeyListener()
        DataManager.init()
        AYUtil.doInit()
    }

    fun restartGame() {
        GlobalScope.launch {
            pointClose.click()
            delay(1000)
            autoStartGame()
        }
    }

    var faceList = arrayListOf<BufferedImage>()
    fun caijiBiaoqing() {
        faceList.clear()
        GlobalScope.launch {
            while (true) {
                var img = getImage(Config.rectOfCryFace)
                if (!isImgExist(img)) {
                    faceList.add(img)
                    ImageIO.write(img, "png", File(caijiPath, "${System.currentTimeMillis()}_${faceList.size}.png"))
                }
            }
        }
    }

    private fun isImgExist(img: BufferedImage): Boolean {
        faceList.forEach {
            if (ImgUtil.isImageSim(img, it)) {
                return true
            }
        }
        return false
    }

    fun autoStartGame() {
        GlobalScope.launch {
            if (mLaunchModel and model_hezuo != 0 || mLaunchModel and model_hanbing != 0) {
                //如果是合作模式，等10s，给对方退出的机会
                delay(10000)
            }
            Config.pointOfLApp.click(null)
            delay(50)
            Config.pointOfLApp.click(null)

            delay(1000)
            init()
            withTimeoutOrNull(5000) {
                while (!ImgUtil.isImageInRect("appstart.png", Config.rectOfStartApp)) {
                    delay(500)
                }
            }
//            delay(5000)

            Config.rectOfStartApp.clickPoint.clickPc()

            withTimeoutOrNull(5000) {
                while (!(Recognize.BtnOk.isFit())) {
                    delay(1000)
                }
            }
            Recognize.BtnOk.click()
            delay(1000)
            start()
        }
    }

    private fun doSomeTest() {
//        for(i in 1..4){
//            getImageFromFile(File(caijiPath,"12_05_53_560.png"))
//                .saveSubTo(CarDoing.starCheckRect,File(caijiPath,"startLv5.png"))
//        }
//        getImageFromFile(File(caijiPath,"ll1.png"))
//            .saveSubTo(CarDoing.saleCheckRect,File(caijiPath,"salecheck.png"))
//        var img1 = getImageFromFile(File(caijiPath,"l2.png")).getSubImage(Recognize.heroStar2.rectFinal)
//        var img1 = getImageFromFile(File(caijiPath,"salecheck.png"))
//        var img2 = getImageFromFile(File(caijiPath,"salecheck.png"))
//      var result =  ImgUtil.isImageSim(img1,img2)
//        println("识别星级 $result")
    }


    fun setLaunchModel(model: Int) {
        if (mLaunchModel == model) return
        gameLaunch?.stop()
        mLaunchModel = model

        gameLaunch = when (mLaunchModel) {
            //对战
            model_faqiang -> {
                FaQiangGameLaunch()
            }

            model_longquan -> {
                LongQuanGameLaunch()
            }

            //合作
            model_hezuo_zhannv -> {
                ZhanNvGameLaunch()
            }

            //寒冰
            model_hanbing_zhannv, model_hanbing_5zhan_ziqiang,model_hanbing_5zhan_boer,model_hanbing_huoling_boer
            ,model_hanbing_5zhan_ziqiang_fuka-> {
                HBZhanNvGameLaunch()
            }

            model_hanbing_mengyan -> {
                MengyanGameLaunch()
            }

            model_anyue_zhanjiang, model_anyue_5zhan,model_anyue_bo -> {
                AyZhanNvGameLaunch()
            }

            model_xuanwo_wawangbo,model_xuanwo_huolingbo, model_xuanwo_wawangziqiang->{
                HBZhanNvGameLaunch()
            }


            else -> null
        }
        gameLaunch?.init()
    }

    fun start() {
        if (mLaunchModel != 0) {
            state.value = 1
            gameLaunch?.start()
        }
    }

    fun stop() {
        state.value = 0
        gameLaunch?.stop()
        GameUtil.ShuaMoValue.value = false
        GameUtil.stopShuaMo()
    }


    fun save() {
        var name = SimpleDateFormat("hh_mm_ss_SSS").format(System.currentTimeMillis())
        rectWindow.saveImgTo(File(caijiPath, "${name}.png"))
    }


    var autoSaving = mutableStateOf(false)
    var autoSaveJob: Job? = null
    fun startAutoSave(time: Long = 200) {
        if (autoSaving.value) return
        autoSaving.value = true
        autoSaveJob = GlobalScope.launch {
            while (autoSaving.value) {
                log("自动保存一张图片")
                save()
                delay(time)
            }
        }
    }

    fun stopAutoSave() {
        autoSaveJob?.cancel()
        autoSaveJob = null
        autoSaving.value = false
    }


    private fun findTfAndMoveTo00(): Boolean {
        log("检测微信塔防窗口...")
//        val tf = Window.findWindowWithName("塔防精灵")
//        Config.platform.value = platform_moniqi
        var wName = when (Config.platform.value) {
            Config.platform_moniqi -> "雷电模拟器"
            else -> "塔防精灵"
        }
        val tf = Window.findWindowWithName(wName)
        if (tf == null) {
            log("未检测到窗口")
            return false
        }
        val rect = Window.getWindowRect(tf)
        val info = WinUser.WINDOWINFO()
        User32.INSTANCE.GetWindowInfo(tf, info)
        var extHeight = info.cbSize
        log("extheight :${extHeight} rect:${rect} ${info.rcWindow} ${info.dwWindowStatus}")
        var height2 =
            (((rect.bottom - rect.top - extHeight) * 1f / (rect.right - rect.left)) * Config.windowWidth + extHeight).toInt()
        var result = true
        log("move to $height2")
        var height = 607 //实际截图到得大小是607
        rectWindow = MRect.createWH(0, 0, Config.windowWidth, height)
//        var left = if(platform.value == platform_moniqi)0 else -1
        var left = Config.dtLeftMargin
        curLeft = left
        curTop = Config.dtTopMargin
        width = Config.windowWidth + 2 * abs(Config.dtLeftMargin)
        this.height = Config.dtHeight

        tfWindow = tf
        moveWindow()
        return true

    }

    var curLeft = 0
    var curTop = 0
    var width = 0
    var height = 0

    private fun moveWindow() {
        User32.INSTANCE.MoveWindow(
            tfWindow,
            curLeft,
            curTop,
            width,
            height,
            true
        )
    }


    @Composable
    fun subButtons() {
        grayBtn("保存英雄") {
            Hero.save()
        }
        grayBtn("保存窗口") {
            save()
        }
    }

    var WindowClose = 1
    var listeing = false
    fun addKeyListener() {
//        thisWindow ?: return
        listeing = true

        GlobalScope.launch(Dispatchers.IO) {
            WindowClose = 1
            var WindowOpen = 2
            var fuckHoutai = 3
            var result = User32.INSTANCE.RegisterHotKey(null, WindowClose, WinUser.MOD_CONTROL, VK_F8)
            User32.INSTANCE.RegisterHotKey(null, WindowOpen, WinUser.MOD_CONTROL, VK_F7)
            User32.INSTANCE.RegisterHotKey(null, fuckHoutai, WinUser.MOD_CONTROL, VK_F9)
//            User32.INSTANCE.RegisterHotKey(null, VK_LEFT, 0, VK_LEFT)
//            User32.INSTANCE.RegisterHotKey(null, VK_RIGHT, 0, VK_RIGHT)
//            User32.INSTANCE.RegisterHotKey(null, VK_UP, 0, VK_UP)
//            User32.INSTANCE.RegisterHotKey(null, VK_DOWN, 0, VK_DOWN)
            User32.INSTANCE.RegisterHotKey(null, VK_NUMPAD0, 0, VK_NUMPAD0)
            User32.INSTANCE.RegisterHotKey(null, VK_NUMPAD1, 0, VK_NUMPAD1)
            User32.INSTANCE.RegisterHotKey(null, VK_NUMPAD2, 0, VK_NUMPAD2)
            User32.INSTANCE.RegisterHotKey(null, VK_NUMPAD3, 0, VK_NUMPAD3)
            User32.INSTANCE.RegisterHotKey(null, VK_NUMPAD4, 0, VK_NUMPAD4)
            User32.INSTANCE.RegisterHotKey(null, VK_NUMPAD5, 0, VK_NUMPAD5)
            User32.INSTANCE.RegisterHotKey(null, VK_NUMPAD6, 0, VK_NUMPAD6)
            User32.INSTANCE.RegisterHotKey(null, VK_NUMPAD7, 0, VK_NUMPAD7)
            User32.INSTANCE.RegisterHotKey(null, VK_NUMPAD8, 0, VK_NUMPAD8)
            User32.INSTANCE.RegisterHotKey(null, VK_NUMPAD9, 0, VK_NUMPAD9)


            log("addKeyLister result $result")
            if (result) {
                while (listeing) {
                    var msg = WinUser.MSG()
                    log("before get message")
                    var result = User32.INSTANCE.GetMessage(msg, null, 0, 0)
//                    var result = User32.INSTANCE.PeekMessage(msg,null,0,0,WindowClose)
//                    log("addkey result $result")
//                    if (result != -1) {
//                        User32.INSTANCE.TranslateMessage(msg)
//                        User32.INSTANCE.DispatchMessage(msg)
//                    }

//                    if (msg.wParam.toInt() == WindowClose) {
//                    } else if (msg.wParam.toInt() == WindowOpen) {
//                        stop()
//                    }

                    log("lparm: ${msg.lParam} ,wparm:${msg.wParam},msg: ${msg.message}")


                    val keyCode = msg.wParam.toInt()
                    if(keyCode in listOf(WindowClose,WindowOpen,fuckHoutai)){

                    }else if (keyListeners.isNotEmpty()) {
                        var handle = false
                        keyListeners.forEach {
                            if (it.onKeyDownOri(msg.wParam.toInt())) {
                                handle = true
                            }
                        }
                        if (handle) {
                            log("按键被拦截处理，本次不再处理")
                            continue
                        }
                    }
                    GlobalScope.launch {
                        log("按键未被拦截处理，继续处理")
                        when (msg.wParam.toInt()) {
                            WindowClose -> {
                                closeApp()
                            }

                            WindowOpen -> {
                                stop()
                            }

                            fuckHoutai->{
                                MRobot.houtai = !MRobot.houtai
                            }

                            VK_NUMPAD0 -> {
                                GlobalScope.launch {
                                    Config.zhandou_shuaxinPoint.click()
                                }
                            }


                            VK_NUMPAD3 -> {//刷魔
                                if (GameUtil.ShuaMoValue.value == true) {
                                    GameUtil.stopShuaMo()
                                    GameUtil.ShuaMoValue.value = false
                                } else {
                                    GameUtil.startShuaMo("")
                                    GameUtil.ShuaMoValue.value = true
                                }
                            }

                            VK_NUMPAD7 -> {//刷木
                                if (GameUtil.ShuaMoValue.value == true) {
                                    GameUtil.stopShuaMo()
                                    GameUtil.ShuaMoValue.value = false
                                } else {
                                    GameUtil.startShuaMo("muqiu")
                                    GameUtil.ShuaMoValue.value = true
                                }
                            }

                            VK_NUMPAD4 -> {
                                MRobot.singleClickPc(Config.zhandou_hero1CheckRect.clickPoint)
                            }

                            VK_NUMPAD5 -> {
                                MRobot.singleClickPc(Config.zhandou_hero2CheckRect.clickPoint)
                            }

                            VK_NUMPAD6 -> {
                                MRobot.singleClickPc(Config.zhandou_hero3CheckRect.clickPoint)
                            }

                            VK_NUMPAD8 -> {
                                MRobot.singleClickPc(CarDoing.salePoint)
                            }

                            else -> {
                                when (msg.wParam.toInt()) {
                                    VK_UP -> {
                                        curTop--
                                    }

                                    VK_DOWN -> curTop++
                                    VK_LEFT -> curLeft--
                                    VK_RIGHT -> curLeft++
                                }
                                log("$curLeft top:  $curTop")
                                moveWindow()
                            }
//                        VK_LEFT -> {
//                            CarDoing.moveLeft()
//                        }
//                        VK_RIGHT -> {
//                            CarDoing.moveRight()
//                        }
                        }
                    }
//                    delay(1000)
                }
            }
        }

    }

    fun closeApp(timeOver: Boolean = false) {
        log("closeApp")
        stop()
        testing = false
        doRemoveKey()
        GlobalScope.launch {
            MRobot.singleClickPc(pointClose)
            if (timeOver) {
                delay(4000)
            }
            closeCallBack?.invoke()
        }

//        MRobot.norClick(pointClose)

        timeJob?.cancel()
        if (timeOver) {//=2 时 是自动关闭
            sleepPc()
        }

    }

    private fun doRemoveKey() {
        log("removeKey")
        listeing = false
//        thisWindow ?: return
        User32.INSTANCE.UnregisterHotKey(null, WindowClose)
    }

    var timeJob: Job? = null

    var leftTime = -1

    fun startTimerDown(time: Float) {
        timeJob?.cancel()
        var times: Int = (time * 60).toInt() //秒

        timeJob = GlobalScope.launch {
            repeat(times) {
                leftTime = times - it
                timerText.value = ("${leftTime} 秒")
                if (checkTimer()) {
                    delay(1000)
                } else {
                    timeJob?.cancel("剩余时间不足")
//                    delay(1000)
                }
            }
            log("倒计时结束，windowClose = 2")
//            windowClose.value = 2
            closeApp(true)
        }

    }

    fun checkTimer(): Boolean {
        //如果不足18分钟，且不在合作中,就结束
        if (leftTime < 19 * 60 && leftTime > 0 && gameLaunch is ZhanNvGameLaunch && !(gameLaunch as ZhanNvGameLaunch).isHezuoIng) {
            return false
        }
        return true
    }

    fun sleepPc() {
        if (!Config.isHome) {
            log("sleeppc")
            GlobalScope.launch {
                delay(200)
                MRobot.singleClickPc(MPoint(10, 1055), null)
                MRobot.robot.delay(1000)
                MRobot.singleClickPc(MPoint(10, 1022), null)
                MRobot.robot.delay(1000)
                MRobot.singleClickPc(MPoint(10, 931), null)
            }

        }
    }
}