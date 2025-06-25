package ui.zhandou.data

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import tasks.IGameLaunch
import ui.MainUIData

abstract class ZhanDouModel(val name: String) {

    open var subModels = mutableStateListOf<String>()

    open var subSelected = mutableStateOf("")

    @Composable
    abstract fun doMainPage()

    @Composable
    fun mainPage(scope: BoxScope) {
        scope.apply {
            doMainPage()
        }
    }

    protected open fun onStartPre() {

    }

    open fun onStartClick() {
        if (isRunning()) {
            MainUIData.curZDModel.value = null
            App.stop()
        } else {
            MainUIData.curZDModel.value = this
            onStartPre()
            App.start()
        }
    }

    open fun isRunning(): Boolean {
        return App.state.value > 0
    }
}