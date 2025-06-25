package ui.zhandou.hezuo

import HSpace16
import MainData
import MenuDialog
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextField
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
import button
import data.Config
import database.DataManager
import tasks.hezuo.zhannvsha.ZhanNvGameLaunch
import ui.MainUIData
import ui.weights.MCheckBox
import ui.weights.MRaidoGroup
import ui.weights.showInputDialog
import ui.zhandou.data.ZhanDouModel
import ui.zhandou.hezuoControl
import java.awt.image.BufferedImage

class HeZuoModel() : ZhanDouModel("合作") {
    //    override var subModels = arrayListOf("1","2")
    override var subModels: SnapshotStateList<String> = mutableStateListOf("战女")
    override var subSelected: MutableState<String> = mutableStateOf("战女")

    val hezuomoshi = mutableStateOf("自己")

    companion object {
        val imgs = mutableStateListOf<BufferedImage>()
    }

    override fun onStartPre() {
        super.onStartPre()
        ZhanNvGameLaunch.model = when (hezuomoshi.value) {
            "微信" -> 1
            "世界" -> 3
            else -> 0

        }
        when (subSelected.value) {
            "战女" -> {
                App.setLaunchModel(App.model_hezuo_zhannv)
            }
        }
    }

    @Composable
    override fun doMainPage() {
        var state = mutableStateOf(false)
        var menuState = mutableStateOf(false)
        val text = mutableStateOf(ZhanNvGameLaunch.parterner)
        Column {
            Row {
                MRaidoGroup(hezuomoshi, arrayListOf("自己", "世界", "微信"))
                ZhanNvGameLaunch.model = when (hezuomoshi.value) {
                    "微信" -> 1
                    "世界" -> 3
                    else -> 0

                }
                if(hezuomoshi.value=="微信"){
                    HSpace16()
                    Text(text.value,Modifier.clickable {
                        menuState.value = true
                    }.border(1.dp,Color.Gray).padding(4.dp))
                }
            }

            hezuoControl()

            LazyColumn {
                itemsIndexed(imgs) { _, item ->
                    Image(item.toPainter(), null, Modifier.width(100.dp).height(100.dp))
                }
            }

            showInputDialog("输入群名字","输入",state){
                DataManager.addGroup(it)
                ZhanNvGameLaunch.parterner = it
                text.value = it
                true
            }
            MenuDialog(menuState){
                DataManager.wxGroups.forEach {
                    button(it){
                        ZhanNvGameLaunch.parterner = it
                        text.value = it
                        menuState.value = false
                    }
                }
                button("添加"){
                    state.value = true
                    menuState.value = false
                }
            }
        }

    }
}