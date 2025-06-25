package ui

import App
import HSpace16
import HWeight
import MainData
import MainData.carPositions
import VSpace
import androidx.compose.animation.*
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.ComposeWindow
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.toPainter
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import data.Recognize
import getImageFromRes
import grayBtn
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import listerners.UIListenerManager
import log
import model.CarPosition
import tasks.gameUtils.GameUtil
import ui.MainUIData.curMenu
import ui.MainUIData.curZDModel
import ui.MainUIData.showCaiji
import ui.MainUIData.showLog
import ui.MainUIData.swipeHero
import ui.caiji.caijiPage
import ui.dialogs.timerDialog
import ui.settings.settingPage
import ui.theme.TFTheme
import ui.weights.MRaidoGroup
import ui.zhandou.anyue.AnYueModel
import ui.zhandou.data.ZhanDouModel
import ui.zhandou.duizhan.DuiZhanModel
import ui.zhandou.hanbing.HanBingModel
import ui.zhandou.hezuo.HeZuoModel
import ui.zhandou.tiankong.TianKongModel
import zdBasePage
import java.awt.image.BufferedImage

object MainUIData {

    val menuList = arrayListOf<MainMenu>().apply {
        add(MainMenu("设置"))
        add(MainMenu(zdModel = DuiZhanModel()))
        add(MainMenu(zdModel = HeZuoModel()))
        add(MainMenu(zdModel = HanBingModel()))
        add(MainMenu(zdModel = TianKongModel()))
        add(MainMenu(zdModel = AnYueModel()))
    }

    var curMenu = mutableStateOf(menuList.get(0))

    var curZDModel = mutableStateOf<ZhanDouModel?>(null)

    lateinit var window: ComposeWindow
    val showLog = mutableStateOf(false)
    val showCaiji = mutableStateOf(false)

    var swipeHero :MutableState<CarPosition?> = mutableStateOf(null)
}

@Composable
@Preview
fun launcher() {
    TFTheme {
        Box {
            mainContainer()

            AnimatedVisibility(
                showLog.value, enter = slideIn {
                    IntOffset(it.width, 0)
                } + fadeIn(),
                exit = slideOut {
                    IntOffset(it.width, 0)
                } + fadeOut(), modifier = Modifier.align(Alignment.TopEnd)
            ) {
                Box(
                    Modifier.fillMaxHeight().width(300.dp)
                        .padding(top = menuRowHeight + 12.dp, end = 12.dp, start = 12.dp)
                ) {
                    logPage(this)
                }
            }


            AnimatedVisibility(
                showCaiji.value, enter = slideIn {
                    IntOffset(it.width, 0)
                } + fadeIn(),
                exit = slideOut {
                    IntOffset(it.width, 0)
                } + fadeOut(), modifier = Modifier.align(Alignment.TopEnd)
            ) {
                Box(
                    Modifier.fillMaxHeight().width(160.dp)
                        .padding(top = menuRowHeight + 12.dp, end = 12.dp, start = 12.dp)
                ) {
                    caijiPage()
                }
            }
        }
    }
}

val colorMenuRowBg = Color(java.awt.Color(221, 227, 233).rgb)
val menuWidth = 80.dp
val menuRowHeight = 40.dp

@Composable
@Preview
private fun mainContainer() {
    val timeInputDialog = remember { mutableStateOf(false) }
    Column(Modifier.fillMaxSize().clickable(interactionSource = MutableInteractionSource(),indication = null) {
        showCaiji.value = false
        showLog.value = false
    }) {
        Row(Modifier.background(colorMenuRowBg)) {//菜单
            menuRaw(MainUIData.menuList) {
                HWeight()
                Row(
                    Modifier.fillMaxHeight(), verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {

                    Text("采集", Modifier.clickable {
                        showCaiji.value = !showCaiji.value
                    })
                    Text("日志", Modifier.clickable {
                        showLog.value = !showLog.value
                    })

                    if (App.timerText.value != "定时关闭") {
                        Text("${App.timerText.value}后关闭", fontSize = 14.sp)
                    }else {
                        Text("定时关闭",Modifier.clickable {
                            timeInputDialog.value = true
                        })
                    }
                }
                HSpace16()
            }


        }
        Row {
            Box(Modifier.weight(1f).fillMaxHeight().padding(12.dp)) {
                if (MainUIData.curMenu.value.title() == "设置") {
                    settingPage()
                } else if (MainUIData.curMenu.value.zdModel != null) {
                    zdBasePage(curMenu.value.zdModel!!)
                }
            }
            mCarInfo()
        }


    }

    timerDialog(timeInputDialog)
}

class MainMenu(val name: String? = null, val zdModel: ZhanDouModel? = null) {
    fun title(): String {
        if (name != null) return name
        else if (zdModel != null) return zdModel.name
        return ""
    }
}


@Composable
private fun menuRaw(list: List<MainMenu>, rowLeft: @Composable RowScope.() -> Unit) {

//    Box(Modifier.height(menuRowHeight).background(colorMenuRowBg).){
//        var x = 0
//        list.forEach {
//            menu(it)
//        }
//    }

    Row(Modifier.height(menuRowHeight).background(colorMenuRowBg)) {
        list.forEach {
            menu(it)
        }
        rowLeft.invoke(this)
    }
}

private fun MainMenu?.isSelect(): Boolean {
    if (this == null) return false
    return curMenu.value == this
}

//@Composable
//private fun menuSpace(left: MainMenu?, right: MainMenu?) {
//    Box(Modifier.width(10.dp).height(menuRowHeight).drawBehind {
//        drawRect(Color.Transparent)
//        if (!left.isSelect() && !right.isSelect()) {
//        } else if (left.isSelect()) {
//            Path().apply {
//                moveTo(0f, menuRowHeight.value)
//                lineTo(0f, menuRowHeight.value - 10.dp.value)
//                quadraticBezierTo(0f, menuRowHeight.value, 10.dp.value, menuRowHeight.value)
//                lineTo(0f, menuRowHeight.value)
//                drawPath(this, Color.White)
//            }
//        } else if (right.isSelect()) {
//            Path().apply {
//                moveTo(0f, menuRowHeight.value)
//                quadraticBezierTo(10.dp.value, menuRowHeight.value, 10.dp.value, menuRowHeight.value - 10.dp.value)
//                lineTo(10.dp.value, menuRowHeight.value)
//                lineTo(0f, menuRowHeight.value)
//                drawPath(this, Color.White)
//            }
//        }
//    })
//}


//class DrawPoint(var x:Float,var y:Float){
//    fun moveX(dx:Float){
//        x+=dx
//    }
//    fun moveY(dy:Float){
//        y+=dy
//    }
//}
//
//fun Path.moveTo(point: DrawPoint){
//    moveTo(point.x,point.y)
//}
//fun Path.lineTo(point: DrawPoint)

val menuRadio = 10.dp.value

@Composable
private fun menu(menu: MainMenu) {
    var checked: Boolean = menu.isSelect()
    Box(Modifier.fillMaxHeight().width(menuWidth).drawBehind {
        if (checked) {
            Path().apply {
                moveTo(0f, size.height)
                relativeQuadraticBezierTo(menuRadio, 0f, menuRadio, -menuRadio)
                relativeLineTo(0f, -(size.height - 8.dp.value - 2 * menuRadio))
                relativeQuadraticBezierTo(0f, -menuRadio, menuRadio, -menuRadio)
                relativeLineTo(size.width - 4 * menuRadio, 0f)
                relativeQuadraticBezierTo(menuRadio, 0f, menuRadio, menuRadio)
                relativeLineTo(0f, (size.height - 8.dp.value - 2 * menuRadio))
                relativeQuadraticBezierTo(0f, menuRadio, menuRadio, menuRadio)
                drawPath(this, Color.White)
            }
        } else {
            drawRect(Color.Transparent)
        }
    }.clickable {
        MainUIData.curMenu.value = menu
    }, contentAlignment = Alignment.Center) {
        Text(
            menu.title(), color = Color.Black, fontSize = 14.sp
        )
    }


}

@Composable
fun mCarInfo() {
    var autoFreshName = mutableStateOf("huanqiu")
    Column(Modifier.width(180.dp).padding(12.dp).border(1.dp,Color.Gray, RoundedCornerShape(4.dp)).padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        VSpace(6)
        Text("战车信息：第${MainData.guan.value}波")
        VSpace(16)
        Row {
            Column(Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("装备:")
                Text("${MainData.zhuangbei.value}")
            }
            mCarPosInfo(carPositions.getOrNull(6),Modifier.weight(1f))
        }

        Row {
            mCarPosInfo(carPositions.getOrNull(5),Modifier.weight(1f))
            mCarPosInfo(carPositions.getOrNull(4),Modifier.weight(1f))
        }
        Row {
            mCarPosInfo(carPositions.getOrNull(3),Modifier.weight(1f))
            mCarPosInfo(carPositions.getOrNull(2),Modifier.weight(1f))
        }
        Row {
            mCarPosInfo(carPositions.getOrNull(1),Modifier.weight(1f))
            mCarPosInfo(carPositions.getOrNull(0),Modifier.weight(1f))
        }

        grayBtn("重新检测星级") {
            UIListenerManager.reCheckStars()
        }
        grayBtn((if (GameUtil.ShuaMoValue.value) "停止单刷" else "开始单刷")) {
            log("选择的是${autoFreshName.value}")
            GameUtil.ShuaMoValue.value = !GameUtil.ShuaMoValue.value
            if (GameUtil.ShuaMoValue.value) {
                GameUtil.startShuaMo(autoFreshName.value)
            } else {
                GameUtil.stopShuaMo()
                autoFreshName.value = ""
            }
        }
        MRaidoGroup(autoFreshName, arrayListOf("muqiu", "moqiu"))

        Spacer(Modifier.weight(1f))

        Button({
            if (curZDModel.value != null) {//战斗中
                curZDModel.value?.onStartClick()
            } else if (curMenu.value.zdModel != null) {
                curMenu.value.zdModel?.onStartClick()
            }
        }, Modifier.width(80.dp).height(80.dp)) {
            Text(if (curZDModel.value == null) "开始" else "暂停")
        }
        VSpace(16)
    }
}

@Composable
fun mCarPosInfo(pos: CarPosition?, modifier: Modifier = Modifier) {
    val img = pos?.mHeroBean?.imgList?.last()
    val star = pos?.mHeroBean?.currentLevel ?: -1
    val starIcon: BufferedImage? = when (star) {
        1 -> getImageFromRes(Recognize.heroStar1.resNameFinal)
        2 -> getImageFromRes(Recognize.heroStar2.resNameFinal)
        3 -> getImageFromRes(Recognize.heroStar3.resNameFinal)
        4 -> getImageFromRes(Recognize.heroStar4.resNameFinal)
        5 -> getImageFromRes(Recognize.heroStar5.resNameFinal)
        else -> null
    }
    Column(Modifier.height(66.dp).padding(horizontal = 5.dp).then(modifier), horizontalAlignment = Alignment.CenterHorizontally) {
        Box(Modifier.border(1.dp,if(swipeHero.value == pos && pos!=null)Color.Red else Color.Gray).width(25.dp).height(35.dp).clickable {
            if(swipeHero.value==null){
                swipeHero.value = pos
            }else if(swipeHero.value == pos){
                swipeHero.value = null
            }else{
                pos?.swipeHeroWithPos(swipeHero.value!!)
                swipeHero.value = null
            }
        }) {
            if (img != null) {
                Image(img.toPainter(), "", Modifier.fillMaxSize())
            } else {
                Text("空", modifier = Modifier.align(Alignment.Center), color = Color.Gray, fontSize = 20.sp)
            }
            if(swipeHero.value == pos && pos!=null) {
                Text("下卡",
                    modifier = Modifier.fillMaxWidth().align(Alignment.BottomCenter)
                        .background(Color.DarkGray.copy(alpha = 0.6f)).clickable {
                        GlobalScope.launch {
                            pos?.downHero()
                        }
                    },
                    fontSize = 10.sp
                )
            }
        }
        VSpace(5)
        if (starIcon != null) {
            Row {
                Text("-", Modifier.clickable {
                    var level = pos?.mHeroBean?.currentLevel ?: 0
                    if (level > 1) {
                        pos?.mHeroBean?.currentLevel = level - 1
                    }
                    var list = arrayListOf<CarPosition>().apply {
                        addAll(carPositions)
                    }
                    MainData.carPositions.clear()
                    carPositions.addAll(list)
                    log("星级出现问题，手动调整减少星级")
                }.padding(3.dp))
                Image(starIcon.toPainter(), "", Modifier.width(16.dp).height(16.dp))
                Text("+", Modifier.clickable {
                    var level = pos?.mHeroBean?.currentLevel ?: 0
                    if (level < 4) {
                        pos?.mHeroBean?.currentLevel = level + 1
                    }
                    var list = arrayListOf<CarPosition>().apply {
                        addAll(carPositions)
                    }
                    MainData.carPositions.clear()
                    carPositions.addAll(list)
                }.padding(3.dp))
            }
        }
    }
}