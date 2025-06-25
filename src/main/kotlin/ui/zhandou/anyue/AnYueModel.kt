package ui.zhandou.anyue

import HSpace
import MainData
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Icon
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toPainter
import androidx.compose.ui.unit.dp
import button
import data.Config
import toPainter
import ui.MainUIData
import ui.weights.MCheckBox
import ui.zhandou.UIKeyListenerManager
import ui.zhandou.data.ZhanDouModel
import ui.zhandou.hezuoControl
import utils.ImgUtil
import java.awt.image.BufferedImage

class AnYueModel() : ZhanDouModel("暗月") {
    //    override var subModels = arrayListOf("1","2")
    override var subModels: SnapshotStateList<String> = mutableStateListOf("五战波","五战","弓波")
    override var subSelected: MutableState<String> = mutableStateOf("五战波")

    companion object {
        val imgs = mutableStateListOf<BufferedImage>()
        val renwuKa = mutableStateOf<String>("")
    }

    override fun onStartPre() {
        super.onStartPre()
        when (subSelected.value) {
            "五战波" -> {
                App.setLaunchModel(App.model_anyue_zhanjiang)
            }

            "五战" -> {
                App.setLaunchModel(App.model_anyue_5zhan)
            }

            "弓波" ->{
                App.setLaunchModel(App.model_anyue_bo)
            }

        }
    }

    @Composable
    override fun doMainPage() {
        Column(Modifier.focusable(true)) {
            hezuoControl()
            LazyRow {
                itemsIndexed(imgs) { _, item ->
                    Image(item.toPainter(), null, Modifier.width(30.dp).height(30.dp))
                }
            }
            Text("狂将上场时间："+ MainData.kuangjiangUpTime.value)
            Text("关卡阶段：${MainData.curGuanKaName.value}")
            Text(("关卡操作：${MainData.curGuanKaDes.value}"))
            OutlinedTextField(MainData.guan.value.toString(), {

                var guan :Int = try {
                    it.toInt()
                } catch (e:Exception){
                    0
                }
                UIKeyListenerManager.onGuanFix (guan)

            })
            Row {
                button("waiting") {
                    UIKeyListenerManager.onWaitingClick()
                }
                HSpace(40)
                button("按键3") {
                    UIKeyListenerManager.onKey3Down()
                }
            }

            if(MainData.guan.value == 79){
                Text("79小野误差设置:正数代表延后一些，复数代表提前一些")
                Row {

                    Icon("ic_up.png".toPainter(), null, Modifier.clickable {
                        MainData.job79TimeDt.value += 300L
                    })
                    Text("${MainData.job79TimeDt.value}", Modifier.clickable {
                    })
                    Icon("ic_down.png".toPainter(), null, Modifier.clickable {
                        MainData.job79TimeDt.value -= 300L
                    })
                }
            }

        }
    }
}