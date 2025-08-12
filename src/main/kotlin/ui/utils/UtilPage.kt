package ui.utils

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import ui.utils.washview.WashPage
import ui.weights.button

@Composable
fun UtilPage(){

    var page by remember { mutableStateOf(1) }

    Row(Modifier.fillMaxSize()) {
        Column {
            button("洗点查看"){
                page = 1
            }

        }

        when(page){
            1 -> WashPage()
        }


    }

}