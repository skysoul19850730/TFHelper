package ui.utils.washview

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import button
import tasks.anyue.zhanjiang.AYWuZhanHeroDoingSimpleBoBack2
import ui.utils.HerosPage
import ui.utils.HerosPageData
import ui.utils.WashDataBean
import ui.utils.showEditDialog

@Composable
fun WashPage(){

    val list = remember { mutableStateListOf<String>()}
 var hideSingle by remember { mutableStateOf(false) }

    val anyueBo = AYWuZhanHeroDoingSimpleBoBack2().apply {
        initHeroes()
    }.heros.map {
        it.heroName.replace(Regex("\\d"), "")
    }
    val listModel = arrayListOf(Pair("暗月波",anyueBo))

    var dialogHero = remember { mutableStateOf("") }


    Column {
        Row {
            Text("长按编辑属性，点击展示属性：")
            button(if(hideSingle)"显示单个英雄属性" else "隐藏单个英雄属性"){
                hideSingle = !hideSingle
            }

        }

        LazyRow {
            items(listModel.size){
                button("${listModel[it].first}"){
                    list.clear()
                    list.addAll(listModel.get(it).second)
                }
            }
        }

        if(list.isNotEmpty()){

            var totalBean = WashDataBean()

            list.forEach {
                val data = HerosPageData.washMap[it] ?: WashDataBean()
                if(!hideSingle) {
                    Text("${it}属性：${data.toShowString()}", color = Color.Gray
                    , modifier = Modifier.clickable {
                        dialogHero.value = it
                        })
                }
                totalBean = totalBean.sum(data)
            }

            Text("总属性：${totalBean.toShowString()}", color = Color.Blue)

        }

        HerosPage(list)

        if (!dialogHero.value.isNullOrEmpty()) {
            showEditDialog(dialogHero)
        }
    }

}