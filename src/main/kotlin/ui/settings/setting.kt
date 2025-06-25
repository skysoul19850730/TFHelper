package ui.settings

import App
import HSpace
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.ComposeWindow
import androidx.compose.ui.awt.awtEvent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.toPainter
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.isPrimaryPressed
import androidx.compose.ui.input.pointer.isSecondaryPressed
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.layout.layout
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import data.Config
import data.MPoint
import data.MRect
import getImage
import grayBtn
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import log
import moveMouse
import tasks.gameUtils.GameUtil
import test
import toPainter
import ui.weights.MCheckBox
import ui.weights.button
import ui.weights.showInputDialog
import utils.ImgUtil
import utils.MRobot
import java.awt.Point
import java.io.File
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

object SettingData {

}

@Composable
@Preview
fun BoxScope.settingPage() {

    var rateDialog = mutableStateOf(false)

    var bbm = mutableStateOf(false)
    var bbm2 = mutableStateOf(false)

    val showPointConfirm = remember {  mutableStateOf(false)}

    Row(Modifier.padding(top = 20.dp)) {
        Column {
            MCheckBox("在家", Config.isHome4Setting)
            MCheckBox("调试", Config._debug)
            button("x:${GameUtil.clickPoint.value.x} Y:${GameUtil.clickPoint.value.y}"){
                showPointConfirm.value = true
            }
            button(if (GameUtil.maiLuing.value) "停止卖鹿" else "卖小鹿") {
                GameUtil.maiLuing.value = !GameUtil.maiLuing.value
                if (GameUtil.maiLuing.value) {
                    GameUtil.statrMaiLu()
                } else {
                    GameUtil.stopMailu()
                }
            }
            button(if(App.autoSaving.value) "停止采集" else "自动采集"){
                if(App.autoSaving.value){
                    App.stopAutoSave()
                }else{
                    App.startAutoSave(200)
                }
            }
            Row {
                Icon(Icons.Filled.Delete, null, Modifier.clickable {
                    GlobalScope.launch {
                        MRobot.singleClick(MPoint(100, 100))
                    }
                    GameUtil.SomeDelay.value -= 100L
                })
                Text("${GameUtil.SomeDelay.value}")
                Icon(Icons.Filled.Add, null, Modifier.clickable {
                    GameUtil.SomeDelay.value+=100L
                })
            }
        }
        HSpace(50)
        Column {
            Row {
                Text("比较相似度设置:")
                Icon("ic_up.png".toPainter(), null, Modifier.clickable {
                    ImgUtil._norRate.value += 0.01
                })
                Text("${ImgUtil.simRate}", Modifier.clickable {
                    rateDialog.value = true
                })
                Icon("ic_down.png".toPainter(), null, Modifier.clickable {
                    ImgUtil._norRate.value -= 0.01
                })
            }
        }
    }
    button("初始化", Modifier.align(Alignment.BottomCenter)) {
        App.init()
    }
    grayBtn("测试", modifier = Modifier.align(Alignment.BottomEnd)) {
        test()
    }
    grayBtn("鼠标", modifier = Modifier.align(Alignment.BottomStart)) {
        moveMouse()
    }

//    showInputDialog("输入时长分钟数，可以浮点型", "例如：120", timeInputDialog) {
//        try {
//            App.startTimerDown(it.toFloat())
//            true
//        } catch (e: Exception) {
//            log(e.message ?: "转换失败")
//            false
//        }
//    }

    showInputDialog("输入相似度，范围0-1之间小数", "例如：0.75", rateDialog) {
        try {
            var v = it.toDouble()
            if (v > 0 && v < 1) {
                ImgUtil._norRate.value = v
            }
            true
        } catch (e: Exception) {
            false
        }
    }
    if(showPointConfirm.value) {
        pointGetWindow(showPointConfirm)
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun pointGetWindow(customScreen: MutableState<Boolean>) {
    Window({
        customScreen.value = false
    }, undecorated = true, resizable = false) {
        pointGetDialog(this.window, customScreen)
        this.window.setLocation(0, 0)
    }
}

@ExperimentalComposeUiApi
@Composable
private fun pointGetDialog(window: ComposeWindow, customScreen: MutableState<Boolean>) {
    val img = getImage(App.rectWindow)
    window.setBounds(0, 0, img.width, img.height)

    MaterialTheme {
        Box(Modifier.width(img.width.dp).height(img.height.dp).border(1.dp, Color.Green)) {

            Image(
                img.toPainter(), null, Modifier.width(img.width.dp)
                    .height(img.height.dp)
                    .onPointerEvent(PointerEventType.Press) {
                        if (it.buttons.isPrimaryPressed) {
                            var startPoint = it.awtEvent.point!!
                            GameUtil.clickPoint.value = MPoint(startPoint.x, startPoint.y)
                        }
                        window.dispose()
                        customScreen.value = false
                    }, alignment = Alignment.TopStart
            )

        }
    }
}