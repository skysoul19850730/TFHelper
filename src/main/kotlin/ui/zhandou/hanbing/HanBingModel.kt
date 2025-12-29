package ui.zhandou.hanbing

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

class HanBingModel() : ZhanDouModel("寒冰") {
    //    override var subModels = arrayListOf("1","2")
    override var subModels: SnapshotStateList<String> = mutableStateListOf("战女任务", "5战自强","5战自强副卡","5战波儿","火灵波儿")
    override var subSelected: MutableState<String> = mutableStateOf("5战自强")

    companion object {
        val imgs = mutableStateListOf<BufferedImage>()
        val renwuKa = mutableStateOf<String>("dapao")
    }

    override fun onStartPre() {
        super.onStartPre()
        when (subSelected.value) {
            "战女任务" -> {
                App.setLaunchModel(App.model_hanbing_zhannv)
            }

//            "战女自己" -> {
//                App.setLaunchModel(App.model_hanbing_zhannv4)
//            }

            "5战自强" -> {
                App.setLaunchModel(App.model_hanbing_5zhan_ziqiang)
            }
            "5战自强副卡" -> {
                App.setLaunchModel(App.model_hanbing_5zhan_ziqiang_fuka)
            }
            "5战波儿" -> {
                App.setLaunchModel(App.model_hanbing_5zhan_boer)
            }
            "火灵波儿" ->{
                App.setLaunchModel(App.model_hanbing_huoling_boer)
            }

//            "梦魇" -> {
//                App.setLaunchModel(App.model_hanbing_mengyan)
//            }
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
            if (MainData.guan.value in 91..99) {
//                button("战士"){
//                    UIKeyListenerManager.onLongWangZsClick()
//                }
                button(text = "法师") {
                    UIKeyListenerManager.onLongWangFsClick()
                }

                Text("未识别时：点击按钮或者小键盘快捷键3，可以下沙皇。之后 主动点击waiting或小键盘9可以改变waiting来上卡，也可以等倒计时10秒结束（因为可能未识别，也点击慢了，如果等10s，就等于上卡晚了，不利于输出，这时可以主动改变waiting）")
            }
            if (MainData.guan.value in 121..129) {
                Column {
                    Row {
                        button("5") {
                            UIKeyListenerManager.onChuanzhangClick(5)
                        }
                        button("4") {
                            UIKeyListenerManager.onChuanzhangClick(4)
                        }
                    }
                    Row {
                        button("3") {
                            UIKeyListenerManager.onChuanzhangClick(3)
                        }
                        button("2") {
                            UIKeyListenerManager.onChuanzhangClick(2)
                        }
                    }
                    Row {
                        button("1") {
                            UIKeyListenerManager.onChuanzhangClick(1)
                        }
                        button("0") {
                            UIKeyListenerManager.onChuanzhangClick(0)
                        }
                    }
                }
                Text("识别出问题时：点击对应位置按钮或快捷键1245780，对应车上对应位置的影响下卡，会自动上回")
            }

        }
    }
}