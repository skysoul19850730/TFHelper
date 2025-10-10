package tasks.hezuo.xin5z

import App
import data.Config
import data.HeroBean
import data.HeroCreator
import getImage
import getImageFromRes
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import log
import tasks.SimpleHeZuoHeroDoing
import utils.ImgUtil
import java.awt.image.BufferedImage

class ZhanXin5HeroDoing : SimpleHeZuoHeroDoing() {//默认赋值0，左边，借用左边第一个position得点击，去识别车位置后再更改


    val zhanjiang = HeroCreator.zhanjiangb.create()
    val tieqi = HeroCreator.tieqi.create()
    val gugu = HeroCreator.gugu.create()
    val yuren = HeroCreator.shitou.create()
    val niutou2 = HeroCreator.niutou2.create()
    val gongjiang = HeroCreator.gongjiang.create()
    val maomi = HeroCreator.maomi.create()
    val guangqiu = HeroCreator.guangqiu.create()
    val huanqiu = HeroCreator.huanqiu.create()
    val baoku = HeroCreator.baoku.create()


    override fun initHeroes() {
        heros = arrayListOf(zhanjiang, tieqi, gugu, yuren, niutou2, gongjiang, maomi, guangqiu, huanqiu, baoku)

        guanDealList.add(GuanDeal(0, { fulls(zhanjiang, tieqi, gugu, niutou2, gongjiang, baoku, yuren) }, {
//            if (!zhanjiang.isInCar()) {
//                upAny(zhanjiang, baoku)
//            } else {//新战将，漏就漏点兵，尽量避免出现刷不到战将的情况
            upAny(zhanjiang, tieqi, gugu, niutou2, gongjiang, baoku, yuren)
//            }
        }))

        changeZhuangbei(200, { longxin })

        guanDealList.add(GuanDeal(301, {
            fulls(maomi)
        }, {
            upAny(maomi)
        }, onGuanDealStart = {
            carDoing.downHero(gongjiang)
        }))

        curGuanDeal = guanDealList.get(0)
    }

    var startTime = 0L
    override fun onStart() {
        super.onStart()
        startTime = System.currentTimeMillis()
        GlobalScope.launch {
            log("开始识别哭脸")
            var cryImgs = arrayListOf<BufferedImage>().apply {
                add(getImageFromRes("cryface1.png"))
                add(getImageFromRes("cryface3.png"))
            }
            var ok = true
            while (ok) {

                var img = getImage(Config.rectOfCryFace)
                var hasCryFace = cryImgs.find {
                    ImgUtil.isImageSim(it, img)
                } != null
                if (hasCryFace) {
                    log("识别到哭脸，退出程序，稍后重启")
                    ok = false
                    App.stop()
                    App.restartGame()
                    break
                }
                delay(100)
                ok = (guankaTask?.currentGuanIndex ?: 0) < 10 && running
            }
            log("关卡超过10，停止识别哭脸")
        }
    }

    override fun onStop() {
        var gk = guankaTask?.currentGuanIndex ?: 0
        super.onStop()
        val endTime = System.currentTimeMillis()
        val cost = (endTime - startTime) / 1000
        var time = "合作完毕：$gk 关，用时： ${cost / 60}分${cost % 60}秒"
        log(time)
    }

    override suspend fun afterHeroClick(heroBean: HeroBean) {
        super.afterHeroClick(heroBean)

    }

}