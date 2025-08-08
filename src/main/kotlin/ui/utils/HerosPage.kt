package ui.utils

import VSpace16
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.PopupAlertDialogProvider
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import button
import data.Config
import database.DataManager
import resFile
import ui.weights.showInputDialog

object HerosPageData {
    val heros = resFile("${Config.platName}/heros").listFiles().distinctBy {
        it.name.replace(Regex("\\d"), "")
    }.map {
        it.name.replace(Regex("\\d"), "")
    }.sorted()

    var state: LazyListState? = null

    val washMap = mutableStateMapOf<String, WashDataBean>()

    init {
        val map = DataManager.initWashData()
        washMap.putAll(map)
    }

}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HerosPage(model: Int, chooseHeros: SnapshotStateList<String>) {
    val heros = HerosPageData.heros

    var dialogHero = remember {  mutableStateOf("")}

    if (HerosPageData.state == null) {
        HerosPageData.state = rememberLazyListState()
    }

    LazyVerticalGrid(cells = GridCells.Fixed(4), state = HerosPageData.state!!, contentPadding = PaddingValues(12.dp)) {

        heros.forEach {
            item {
                Box(modifier = Modifier.height(60.dp).padding(12.dp).border(2.dp, Color.Black).clickable {
                    if (model == 1) {
                        dialogHero.value = it
                    } else {
                        if (chooseHeros.contains(it)) {
                            chooseHeros.remove(it)
                        } else {
                            chooseHeros.add(it)
                        }
                    }

                }, contentAlignment = Alignment.Center) {
                    Text(it)
                }
            }
        }

    }

    if(!dialogHero.value.isNullOrEmpty()) {
        showEditDialog(dialogHero)
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun showEditDialog(dialogHero: MutableState<String>) {

    val state = remember { mutableStateOf<Int?>(null) }

    val showState = remember { mutableStateOf(false) }

    LaunchedEffect(state.value) {
        if (state.value == null) {
            showState.value = false
        } else {
            showState.value = true
        }
    }

    val data = remember { mutableStateOf(HerosPageData.washMap[dialogHero.value] ?: WashDataBean()) }

    if (dialogHero.value.isNotEmpty()) {
        with(PopupAlertDialogProvider) {
            AlertDialog({
                dialogHero.value = ""
            }) {
                Column(Modifier.padding(12.dp)) {
                    Text("${dialogHero.value}洗点情况:")
                    VSpace16()
                    button("伤害减免：${data.value.jianfang}") {
                        state.value = 4
                    }
                    VSpace16()
                    button("魔抗：${data.value.mokang}") {
                        state.value = 1
                    }
                    VSpace16()
                    button(
                        "物抗：${data.value.wukang}"
                    ) {
                        state.value = 2
                    }
                    VSpace16()
                    button("纯粹减免：${data.value.chuncuikang}") {
                        state.value = 3
                    }
                    VSpace16()

                    button("保存") {
                        HerosPageData.washMap[dialogHero.value] = data.value
                        DataManager.saveWashData()
                        dialogHero.value = ""
                    }
                }
            }
        }
    }

    showInputDialog("输入点数：", "", showState) {
        val newd = data.value.copy()
        when (state.value) {
            1 -> {
                newd.mokang = it.toFloat()
            }

            2 -> {
                newd.wukang = it.toFloat()
            }

            3 -> {
                newd.chuncuikang = it.toFloat()
            }

            4 -> {
                newd.jianfang = it.toFloat()
            }

        }
        data.value = newd
        state.value = null
        true
    }
}

class WashDataBean() {
    var mokang: Float = 0f
    var wukang: Float = 0f
    var chuncuikang: Float = 0f
    var jianfang: Float = 0f

    fun copy():WashDataBean{
        return WashDataBean().apply {
            mokang = this@WashDataBean.mokang
            wukang = this@WashDataBean.wukang
            chuncuikang = this@WashDataBean.chuncuikang
            jianfang = this@WashDataBean.jianfang
        }
    }
}