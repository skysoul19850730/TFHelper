package ui.zhandou.duizhan

import VSpace16
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import data.Config
import ui.weights.MCheckBox
import ui.zhandou.data.ZhanDouModel
import ui.zhandou.hanbing.HanBingModel

class DuiZhanModel() : ZhanDouModel("对战") {
    //    override var subModels = arrayListOf("1","2")
    override var subModels: SnapshotStateList<String> = mutableStateListOf("龙拳", "法枪")
    override var subSelected: MutableState<String> = mutableStateOf("龙拳")
    override fun onStartPre() {
        super.onStartPre()
        when (subSelected.value) {
            "龙拳" -> {
                App.setLaunchModel(App.model_longquan)
            }
            "法枪" -> {
                App.setLaunchModel(App.model_faqiang)
            }
        }
    }
    @Composable
    override fun doMainPage() {
        Column {
            Row {
                Text("胜利：${MainData.sucCount.value}", color = Color.Red)
                Text("失败：${MainData.failCount.value}", color = Color.Gray)
            }
            MCheckBox("看失败广告", Config.viewFailAdv)
            MCheckBox("投降", Config.touxiangAuto)
            if (Config.touxiangAuto.value) {
                MCheckBox("全投降", Config.touxiangAll)
            }
            VSpace16()
            Text("Tips:快捷键0 投降;", color = Color.Red)
            VSpace16()
            Row {
                Text("任务卡:")
                OutlinedTextField(HanBingModel.renwuKa.value, {
                    HanBingModel.renwuKa.value = it
                })
            }
        }
    }
}