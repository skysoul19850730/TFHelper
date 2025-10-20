package ui.zhandou.tiankong

import MainData
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toPainter
import androidx.compose.ui.unit.dp
import data.Config
import tasks.IGameLaunch
import tasks.hezuo.zhannvsha.ZhanNvGameLaunch
import tasks.huodong.HuodongUtil
import ui.MainUIData
import ui.weights.MCheckBox
import ui.weights.MRaidoGroup
import ui.zhandou.data.ZhanDouModel
import ui.zhandou.hezuo.HeZuoModel
import ui.zhandou.hezuoControl
import java.awt.image.BufferedImage

class TianKongModel() : ZhanDouModel("活动") {
    //    override var subModels = arrayListOf("1","2")
    override var subModels: SnapshotStateList<String> = mutableStateListOf("当前活动","自由单车","自由合作")
    override var subSelected: MutableState<String> = mutableStateOf("当前活动")

    companion object {
        val imgs = mutableStateListOf<BufferedImage>()

        val zr_heros = mutableStateOf<String>("")
        val zr_qiu= mutableStateOf<String>("")
        val zr_gongcheng = mutableStateOf<String>("")
        val sz_heros = mutableStateOf<String>("")

    }

    @Composable
    override fun doMainPage() {
        Column {
            hezuoControl()

            LazyColumn {
                itemsIndexed(HeZuoModel.imgs) { _, item ->
                    Image(item.toPainter(), null, Modifier.width(100.dp).height(100.dp))
                }
            }

            if(subSelected.value == "自定义"){
                Row {
                    Text("阵容英雄:")
                    OutlinedTextField(zr_heros.value,{
                        zr_heros.value = it
                    })
                }
                Row {
                    Text("阵容球类:")
                    OutlinedTextField(zr_qiu.value,{
                        zr_qiu.value = it
                    })
                }
                Row {
                    Text("阵容工程:")
                    OutlinedTextField(zr_gongcheng.value,{
                        zr_gongcheng.value = it
                    })
                }
                Row {
                    Text("上阵英雄:")
                    OutlinedTextField(sz_heros.value,{
                        sz_heros.value = it
                    })
                }


            }
        }
    }

    override fun onStartPre() {
        super.onStartPre()
        when (subSelected.value) {
            "大圣弓箭" -> {
//                App.setLaunchModel(App.model_tiankong)
            }
        }
    }

    override fun onStartClick() {
        if (HuodongUtil.state.value) {
            HuodongUtil.stop()
        } else {
            var model =  when (subSelected.value) {

                "当前活动" -> {
                    1001
                }
                "自由单车" -> {
                    1002
                }
                "自由合作" ->{
                    1003
                }

                else -> 0
            }
            HuodongUtil.start(model)
        }
    }

    override fun isRunning(): Boolean {
        return HuodongUtil.state.value
    }
}