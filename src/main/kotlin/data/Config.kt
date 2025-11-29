package data

import androidx.compose.runtime.mutableStateOf
import java.awt.Color
import java.awt.Point

object Config {

    val _debug = mutableStateOf(false)

    val debug get() = _debug.value

    var viewFailAdv = mutableStateOf(true)
    var touxiangAuto = mutableStateOf(false)
    var touxiangAll = mutableStateOf(false)

    var appRootPath = ""

    val platform_moniqi = 0
    val platform_wx = 1
    val platform_qq = 2


    var isHome4Setting = mutableStateOf(false)
    var platform = mutableStateOf(platform_wx)
    val platName: String
        get() = if (isHome) "moniqi" else "xiaochengxu"
    val dtTopMargin: Int
        get() {
            return when (platform.value) {
                platform_moniqi -> 9
                platform_qq -> 0
                else -> if (!isHome) 0 else -3
            }
        }

    val dtHeight: Int
        get() {
            return when (platform.value) {
                platform_moniqi -> 598
                platform_qq -> 605
                else -> if (!isHome) 607 else 609
            }
        }

    val dtLeftMargin: Int
        get() {
            return when (platform.value) {
                platform_moniqi -> -1
                platform_qq -> if (!isHome) 0 else -3
                else -> if (!isHome) 0 else -1
            }
        }
    val isHome
        get() = isHome4Setting.value


    val pointClose = MPoint(974, 23)
    val windowWidth = 1000
    val caiji_main_path
        get() = "${appRootPath}tfres"
    val data_main_path
        get() = "${appRootPath}database"

    //      get() =  if (isHome) "C:\\Users\\85963\\asd\\untitled1\\tfres" else "C:\\Users\\Administrator\\IdeaProjects\\intellij-sdk-code-samples\\untitled1\\tfres"
    val topbarHeight = 44

    //核心区域是1000 * 563

    val delayLong = 2000L

    val delayNor = 300L


    val heroW = 50
    val heroH = 70
    val heroT = 460 + topbarHeight

    var zhandou_zhuangbeiCheckRect: MRect = MRect.createWH(30, 500 + topbarHeight, 40, 40)

    var zhandou_hero1CheckRect: MRect = MRect.createWH(393, heroT, heroW, heroH)
    var zhandou_hero2CheckRect: MRect = MRect.createWH(475, heroT, heroW, heroH)
    var zhandou_hero3CheckRect: MRect = MRect.createWH(557, heroT, heroW, heroH)

    val zhandou_hezuo_guankaRect = MRect.createWH(488, 81, 26, 18)

    //    val guankaRect2 = data.MRect.createWH(472,30,55,25)
    val zhandou_hezuo_bossRect = data.MRect.createWH(910, 26 + topbarHeight, 50, 50)
    val zhandou_hezuo_bossNameRect = data.MRect.createWH(825, 52, 73, 21)

    //    val shaiziPoint = Point(285,300+ topbarHeight)
    var zhandou_shuaxinPoint = MPoint(700, 500 + topbarHeight)
    var zhandou_kuojianPoint = MPoint(300, 500 + topbarHeight)

    val hezuo_startPoint = MPoint(610, 550)

    //和好友一起
    val hezuo_friend = MPoint(330, 490)

    //加入房间
    val hezuo_Join = MPoint(600, 420)
    val hezuo_Join_Sure = MPoint(460, 420, Color(35, 147, 255))
    val hezuo_room_input_game = MPoint(400, 350)

    val hezuo_room_input_wx = MPoint(380, 300)
    val hezuo_room_input_wx_over = MPoint(440, 360)

    val hezuo_room_join_close = MPoint(679, 215)

    val hezuo_0tip_ok = MPoint(620, 450)


    val adv_close = MPoint(956, 73)
    val adv_point = MPoint(500, 510)

    val Color_ChuangZhang = Color(7, 250, 210)


    val point7p_houche = MPoint(118, 220)
    val point7p_qianche = MPoint(216, 220)

    val rectKuojian = MRect.createWH(292, 570, 57, 21)
    val rectShuaxin = MRect.createWH(690, 570, 40, 22)
//es8  es4 ye5   6e0 y80  y50 y56 e04

    val hbMSCloud = MPoint(610, 180, Color(238, 234, 125))
    val hbFSCloud = MPoint(610, 180, Color(150, 156, 204))
    val hbSSCloud = MPoint(610, 180, Color(81, 81, 81))
    val hbZSCloud = MPoint(610, 180, Color(81, 81, 81))


    val rect4ShuakaColor = MRect.createWH(693, 574, 20, 15)
    val rect4KuojianColor = MRect.createWH(288, 574, 25, 15)

    val pointHeroChoose = MPoint(940, 320)
    val pointHeroChooseBack = MPoint(42, 83)
    val pointHeroDuiZhan = MPoint(700, 85)
    val pointHeroDuiZhanFail = MPoint(780, 85)
    val pointDuiZhanRenshu = MPoint(43, 161)
    val pointDuiZhanRenshuOk = MPoint(620, 450)

    //600 270 200 130
    val colorLeishenLanqiu = Color(255, 255, 255)
    val colorLeishenHongqiu = Color(255, 45, 85)

    //    val rectCheckOfLeishen = MRect.createWH(700,270,50,25)
    val rectCheckOfLeishen = MRect.createWH(566, 273, 220, 120)
    val count4SureLeishen = 30

    val leishenqiuXueTiao = Color(211, 71, 71)
    val leishenqiuXueTiao2 = Color(225, 150, 130)
    val leishenqiuXueTiaoRect = MRect.createWH(530, 244, 270, 10)


    val xiongmaoFS = Color(186, 71, 231)
    val xiongmaoGJ = Color(10, 180, 100)
    val xiongmaoZS = Color(225, 110, 48)
    val xiongmaoSS = Color(251, 74, 74)
    val xiongmaoQiuRect = MRect.createWH(700, 300, 50, 50)

    /**
     * 小程序图标位置
     */
    val pointOfLApp = MPoint(3788 - 1920, 950)

    /**
     * 打开小程序 loading结束时的按钮位置，可以设置比如超时10s后点击。正常不可能loading10s，这里暂时不处理 游戏升级的情况
     * custom_481_453_537_495
     */
    val rectOfStartApp = MRect.create4P(481, 453, 537, 495)

    /**
     * 哭脸表情custom_102_140_166_202
     */
    val rectOfCryFace = MRect.create4P(102, 140, 166, 202)


    val Color_AY19 = Color(246, 1, 1)
    val Color_AY19_Over = Color(100, 255, 255)
    val Color_AY19_Over2 = Color(60, 255, 255)
//    val Color_AY39_Left = Color(255, 200, 151)
//    val Color_AY39_Right = Color(140, 190, 230)
//    val fLineY = 618
//    val sLineY = 678
    //40 60

    val Color_AY39_Left = Color(245, 200, 155)
    val Color_AY39_Right = Color(140, 210, 240)
    val Color_AY39_Null = Color(30, 30, 30)

    var qianRect = MRect.createWH(174,569,88,30)


    val changtuRect = MRect.create4P(30,223,978,548)

    val AY_Puke_rect = MRect.createWH(596,256,20,30)
    val AyPukeXuePoint = MPoint(693,232, Color(212,71,71))
}