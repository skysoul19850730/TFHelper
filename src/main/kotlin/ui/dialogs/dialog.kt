package ui.dialogs

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import log
import ui.weights.showInputDialog

@Composable
fun timerDialog(showState: MutableState<Boolean>) {
    showInputDialog("输入时长分钟数，可以浮点型", "例如：120", showState) {
        try {
            App.startTimerDown(it.toFloat())
            true
        } catch (e: Exception) {
            log(e.message ?: "转换失败")
            false
        }
    }
}