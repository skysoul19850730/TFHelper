package ui.caiji

import addJiexiHeroResult
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Card
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import button
import grayBtn
import log
import showOtherWindow
import tasks.Boss
import tasks.Guanka
import tasks.Hero
import test
import ui.weights.showInputDialog
import java.awt.image.BufferedImage

@Composable
fun caijiPage() {
    val showJieXiHeroUI = remember { mutableStateOf(false) }
    val timeInputDialog = remember { mutableStateOf(false) }
    val customScreen = remember { mutableStateOf(false) }
    var img4Custom by remember { mutableStateOf<BufferedImage?>(null) }

    Card(Modifier.fillMaxWidth().height(450.dp), elevation = 4.dp, backgroundColor = Color(0xffa1a1a1)) {
        Column(Modifier.padding(horizontal = 12.dp)) {
            grayBtn("output") {
                Hero.aotuCaiji()
            }
            grayBtn("解析英雄") {
                Hero.caijianIng = false
                Hero.jiexiHeros()
                showJieXiHeroUI.value = true
            }
//            grayBtn(App.timerText) {
//                if (App.timerText.value == "定时关闭") {
//                    timeInputDialog.value = true
//                }
//            }
//            grayBtn("测试") {
//                test()
//            }
            grayBtn("自定义裁剪") {
                customScreen.value = true
            }
            App.subButtons()
            grayBtn("管卡采集") {
                if (!Guanka.caijiing) {
                    Guanka.startCaiji()
                } else {
                    Guanka.stopCaiji()
                }
            }
            grayBtn("保存boss") {
                Boss.save()
            }

        }
    }


    addJiexiHeroResult(showJieXiHeroUI)
    showInputDialog("输入时长分钟数，可以浮点型", "例如：120", timeInputDialog) {
        try {
            App.startTimerDown(it.toFloat())
            true
        } catch (e: Exception) {
            log(e.message ?: "转换失败")
            false
        }
    }
    if (customScreen.value) {
        showOtherWindow(customScreen)
    }
}