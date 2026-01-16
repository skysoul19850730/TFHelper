package tasks.shenhai

import data.Config
import data.HeroBean
import data.MPoint
import data.MRect
import getImage
import getImageFromRes
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import tasks.SimpleHeZuoHeroDoing
import tasks.XueLiang
import tasks.daxuanwo.utils.WX59
import ui.zhandou.UIKeyListenerManager
import utils.ImgUtil
import utils.MRobot
import java.awt.event.KeyEvent

// 9  29 都自动执行
abstract class BaseSimpleSHHeroDoing() : SimpleHeZuoHeroDoing(), UIKeyListenerManager.UIKeyListener {


    override suspend fun onKeyDown(code: Int): Boolean {
        //如果龙王识别出错可以按快捷下对应卡牌，但不知道快捷键按下得时间，所以不能延时进行上卡，只能快捷键9来恢复上卡
        return false
    }


    override fun onGuanFix(guan: Int) {
        guankaTask?.setCurGuanIndex(guan)
    }

    override fun onLongWangZsClick() {
    }

    override fun onLongWangFsClick() {
    }

    override fun onKey3Down() {
    }

    override fun onWaitingClick() {
        waiting = !waiting
    }


    override fun onStart() {
        super.onStart()
        UIKeyListenerManager.addKeyListener(this)
    }

    override fun onStop() {
        super.onStop()
        UIKeyListenerManager.removeKeyListener(this)
        App.stopAutoSave()
    }
}