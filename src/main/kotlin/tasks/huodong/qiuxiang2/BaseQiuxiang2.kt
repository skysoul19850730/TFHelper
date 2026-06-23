package tasks.huodong.qiuxiang2

import data.HeroBean
import kotlinx.coroutines.delay
import tasks.SimpleHeZuoHeroDoing
import java.awt.event.KeyEvent

abstract class BaseQiuxiang2 : SimpleHeZuoHeroDoing() {


    override fun onGuanChange(guan: Int) {
        super.onGuanChange(guan)
        if(curGuan == 99){
            App.startAutoSave()
        }else{
            App.stopAutoSave()
        }
    }

    override suspend fun onKeyDown(code: Int): Boolean {
        if (code == KeyEvent.VK_NUMPAD9) {//9强制改变waitting，防止waiting有逻辑错误不上卡
            waiting = !waiting
            return false
        }
        if (curGuan == 99 || curGuan == 98) {
            var position = when (code) {
                KeyEvent.VK_NUMPAD2 -> 0
                KeyEvent.VK_NUMPAD1 -> 1
                KeyEvent.VK_NUMPAD5 -> 2
                KeyEvent.VK_NUMPAD4 -> 3
                KeyEvent.VK_NUMPAD8 -> 4
                KeyEvent.VK_NUMPAD7 -> 5
                KeyEvent.VK_NUMPAD0 -> 6
                else -> -1
            }
            carDoing.downPosition(position)
            return true
        }
        return super.onKeyDown(code)
    }


    fun autoHuanAfter149() {
        changeZhuangbei(159, { qiangxi })
        changeZhuangbei(169, { yandou })
        changeZhuangbei(179, { qiangxi })
        changeZhuangbei(189, { yandou })
        changeZhuangbei(199, { qiangxi })
    }

}