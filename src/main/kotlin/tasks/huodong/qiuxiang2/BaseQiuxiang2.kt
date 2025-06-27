package tasks.huodong.qiuxiang2

import data.Config
import data.HeroBean
import getImage
import getImageFromRes
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import tasks.SimpleHeZuoHeroDoing
import tasks.XueLiang
import utils.ImgUtil
import java.awt.event.KeyEvent
import java.awt.image.BufferedImage
import kotlin.math.abs

abstract class BaseQiuxiang2 : SimpleHeZuoHeroDoing() {

    var damu = false
    var dabing = false

    var bingQiu :HeroBean? = null
    var muQiu:HeroBean?=null



    override suspend fun onKeyDown(code: Int): Boolean {
        if (code == KeyEvent.VK_NUMPAD9) {//9强制改变waitting，防止waiting有逻辑错误不上卡
            waiting = !waiting
            return false
        }
        if(curGuan==49 || curGuan == 48){
           var position = when(code){
                KeyEvent.VK_NUMPAD4->3
                KeyEvent.VK_NUMPAD5->2
                KeyEvent.VK_NUMPAD7->5
                KeyEvent.VK_NUMPAD8->4
               else ->-1
            }
            carDoing.downPosition(position)

            if(code == KeyEvent.VK_NUMPAD0){
                waiting = false
            }
            return true
        }
        return super.onKeyDown(code)
    }

    fun addHuan(guan:Int, zhuangbei: ()->Boolean){
        guanDealList.add(
            GuanDeal(
                guan,
                isOver = zhuangbei,
                chooseHero = {
                    upAny(zhuangbei = zhuangbei)
                }
            )
        )
    }

    fun add49(){

    }

    fun add149(){
        if(dabing||damu){
            guanDealList.add(GuanDeal(
                149,
                isOver = {
                    curGuan > 149
                },
                chooseHero = {
                    if(!xueChecked){
                        check3Attach()
                    }
                    delay(1000)
                    if(dabing){
                        upAny(bingQiu!!)
                    }else{
                        upAny(muQiu!!)
                    }
                }
            ))

        }
    }


    var curXue = 0f
    var xueChecked = false

    suspend fun check3Attach(){
        if(curXue == 0f){
            curXue = XueLiang.getXueLiang()
        }

        var tem = XueLiang.getXueLiang()
        var count = 0
        while(curGuan<150){
            if(abs(curXue-tem)>0.03) {
                count++
                curXue = tem
            }
            delay(200)
            tem = XueLiang.getXueLiang()
            if(count>=3){
                xueChecked = true
                GlobalScope.launch {
                    delay(10000)
                    xueChecked = false
                }
                return
            }
        }
    }

}