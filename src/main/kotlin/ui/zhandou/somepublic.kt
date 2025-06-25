package ui.zhandou

import MainData
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import listerners.UIListenerManager
import ui.weights.MCheckBox
import ui.weights.button

@Composable
fun hezuoControl(){
    Column {
        MCheckBox("waiting",MainData.isWaiting)
        button("重新检测星级"){
            UIListenerManager.reCheckStars()
        }
    }
}