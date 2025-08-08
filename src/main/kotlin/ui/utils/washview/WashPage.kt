package ui.utils.washview

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import ui.utils.HerosPage

@Composable
fun WashPage(){

    val list = mutableStateListOf<String>()

    var model = remember { mutableStateOf(1) }

    Column {
        Text(if(model.value == 1)"编辑" else "浏览")

        if(list.isNotEmpty()){

            Text("总属性：")


        }

        HerosPage(model.value,list)
    }

}