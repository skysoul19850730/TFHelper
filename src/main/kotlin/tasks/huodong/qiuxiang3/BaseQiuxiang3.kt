package tasks.huodong.qiuxiang3

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

abstract class BaseQiuxiang3 : SimpleHeZuoHeroDoing() {



    var firstUpHeros = arrayListOf<HeroBean>()
    var upDownHeros = arrayListOf<HeroBean>()

    var up = false

    override fun initHeroes() {
        super.initHeroes()
        guanDealList.add(
            GuanDeal(
                startGuan = 0,
                isOver = {

                   false

                },
                chooseHero = {
                    if(firstUpHeros.all { it.isFull() }){

                        if(up){
                            if(!upDownHeros.all { it.isFull() }) {
                                upAny(*(upDownHeros.toTypedArray()))
                            }else{
                                while(up){
                                    delay(100)
                                }
                                -1
                            }
                        }else{
                            upDownHeros.forEach {
                                carDoing.downHero(it)
                            }
                            while(!up){
                                delay(100)
                            }
                            -1
                        }

                    }else {
                        upAny(*(firstUpHeros.toTypedArray()))
                    }
                }
            )
        )


        curGuanDeal = guanDealList.get(0)
    }

    override suspend fun onKeyDown(code: Int): Boolean {
        if(code == KeyEvent.VK_NUMPAD0){
            up = !up
        }

        return super.onKeyDown(code)
    }

}