package tasks.huodong.shuihu3

import data.Config
import data.HeroBean
import getImage
import getImageFromRes
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import tasks.SimpleHeZuoHeroDoing
import utils.ImgUtil
import java.awt.image.BufferedImage

abstract class BaseDay : SimpleHeZuoHeroDoing() {
    abstract var downUpHero: HeroBean

    abstract var upHeros: ArrayList<HeroBean>


    var uped = true

    override fun initHeroes() {
        super.initHeroes()
        guanDealList.add(GuanDeal(0, isOver = {
            upHeros.all {
                if (it == downUpHero) {
                    it.isInCar()
                } else it.isFull()
            }
        }, chooseHero = {
            upAny(*upHeros.toTypedArray())
        }))

        guanDealList.add(GuanDeal(1, isOver = {
            false
        }, chooseHero = {
            upDown(this)
        }))

        curGuanDeal = guanDealList.get(0)
        GlobalScope.launch {
            delay(50000)
            curGuanDeal = guanDealList.get(1)
            waiting = false
            cryFace()
        }
    }

    var downTime = 0L

    suspend fun upDown(heros: List<HeroBean?>): Int {
        while (uped) {//上的时候啥都不管，等下卡
            delay(100)
        }
        if(downUpHero.isInCar()) {
            carDoing.downHero(downUpHero)
            downTime = System.currentTimeMillis()
        }
        var index = heros.indexOf(downUpHero)
        if (index > -1) {
            while (System.currentTimeMillis()-downTime<1000) {
                delay(100)
            }
            uped = true
            return index
        } else return -1
    }

    fun cryFace() {
        GlobalScope.launch {

            var cryImgs = arrayListOf<BufferedImage>().apply {
                add(getImageFromRes("cryface1.png"))
                add(getImageFromRes("cryface3.png"))
            }

            while (true) {

                var img = getImage(Config.rectOfCryFace)
                var hasCryFace = cryImgs.find {
                    ImgUtil.isImageSim(it, img)
                } != null

                if (hasCryFace) {
                    uped = !uped
                    delay(3000)
                } else {
                    delay(200)
                }

            }

        }
    }

}