package ui.zhandou.xuanwo

import HSpace
import MainData
import androidx.compose.foundation.Image
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toPainter
import androidx.compose.ui.unit.dp
import button
import ui.zhandou.UIKeyListenerManager
import ui.zhandou.data.ZhanDouModel
import ui.zhandou.hezuoControl
import java.awt.image.BufferedImage

class XuanWoModel() : ZhanDouModel("漩涡") {
    //    override var subModels = arrayListOf("1","2")
    override var subModels: SnapshotStateList<String> = mutableStateListOf("瓦王波")
    override var subSelected: MutableState<String> = mutableStateOf("瓦王波")

    companion object {
        val imgs = mutableStateListOf<BufferedImage>()
        val renwuKa = mutableStateOf<String>("feiting")
    }

    override fun onStartPre() {
        super.onStartPre()
        when (subSelected.value) {
            "瓦王波" -> {
                App.setLaunchModel(App.model_xuanwo_wawangbo)
            }

        }
    }

    @Composable
    override fun doMainPage() {
        Column(Modifier.focusable(true)) {
            hezuoControl()
            if (subSelected.value == "战女任务") {
                Row {
                    Text("任务卡:")
                    OutlinedTextField(renwuKa.value, {
                        renwuKa.value = it
                    })
                }
            }
            LazyRow {
                itemsIndexed(imgs) { _, item ->
                    Image(item.toPainter(), null, Modifier.width(30.dp).height(30.dp))
                }
            }
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


            Text("全局：按1 刷木，按9改变wiating状态（可以停止刷木）")

        }
    }
}