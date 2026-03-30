package tasks.huodong.qiuxiang2

import data.HeroBean
import kotlinx.coroutines.delay
import tasks.SimpleHeZuoHeroDoing
import java.awt.event.KeyEvent

abstract class BaseQiuxiang2 : SimpleHeZuoHeroDoing() {

    var damu = false
    var dabing = false

    var pBingQiu: HeroBean? = null
    var pHuanQiu: HeroBean? = null

    lateinit var upHeros: List<HeroBean>


    override fun onGuanChange(guan: Int) {
        super.onGuanChange(guan)
        if(curGuan == 49){
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
        if (curGuan == 49 || curGuan == 48) {
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

            full49 = false
            return true
        }
        if (curGuan == 99) {
            stop99 = true
        }
        return super.onKeyDown(code)
    }

    fun addStart() {
//        if (huanQiu == null) {
//            addGuanDealWithHerosFull(0, upHeros)
//        } else {
//            addGuanDealWithHerosFull(0, upHeros, zhuangbei = { qiangxi })
//        }
        addGuanDealWithHerosFull(
            0, upHeros, zhuangbei = if (pHuanQiu == null) null else {
                { qiangxi }
            }
        )
    }

    var stop99 = false
    fun add99() {
        if (pBingQiu == null) return
        gudingShuaQiuTask("bingqiu", 99, 2000,
            customOverJudge = {
                stop99
            })
    }

    var full49 = true
    fun add49() {
        addGuanDeal(49) {
            over {
                curGuan > 49
            }

            chooseHero {
                if (fulls(*upHeros.toTypedArray())) {
                    full49 = true
                }
                while (full49 && curGuan==49) {
                    delay(200)
                }
                upAny(upHeros)
            }
        }

        addGuanDealWithHerosFull(50, upHeros)
    }

    fun autoHuanAfter149() {
        changeZhuangbei(169, { yandou })
        changeZhuangbei(179, { qiangxi })
        changeZhuangbei(189, { yandou })
        changeZhuangbei(199, { qiangxi })
    }

}