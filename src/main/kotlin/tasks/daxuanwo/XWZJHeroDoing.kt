package tasks.daxuanwo

import data.HeroCreator
import kotlinx.coroutines.delay
import java.awt.event.KeyEvent

class XWZJHeroDoing : BaseSimpleXWHeroDoing() {
    val kuangjiang = HeroCreator.kuangjiang.create()
    val tieqi = HeroCreator.tieqi.create()
    val zhanjiang = HeroCreator.zhanjiangb.create()
    val shitou = HeroCreator.shitou.create()
    val niutou = HeroCreator.niutou2.create()
    val yuren = HeroCreator.yuren.create()
    val feiting = HeroCreator.feiting.create()

    val muqiu = HeroCreator.muqiu.create()
    val guangqiu = HeroCreator.guangqiu.create()
    val bingqiu = HeroCreator.bingqiu.create()

    override fun initHeroes() {
        super.initHeroes()
        heros = arrayListOf(
            kuangjiang, tieqi, zhanjiang, shitou, niutou, yuren, feiting, muqiu, guangqiu, bingqiu
        )
        addGuanDeal(0){
            over {
                fulls(zhanjiang,niutou,feiting)
            }
            chooseHero {
                if(zhanjiang.isInCar()) {
                    upAny(zhanjiang, niutou, feiting)
                }else upAny(zhanjiang)
            }
        }

        addGuanDeal(10){
            over {
                fulls(zhanjiang,niutou,feiting,tieqi,shitou,kuangjiang,yuren)
            }
            chooseHero{
                upAny(zhanjiang,niutou,feiting,tieqi,shitou,kuangjiang,yuren)
            }
        }

        addGuanDeal(40){
            over {
                kuangjiang.isInCar()
            }
            chooseHero {
                upAny(kuangjiang)
            }
            onStart {
                carDoing.downHero(kuangjiang)
            }
        }

        addGuanDeal(49){
            over {
                curGuan>49
            }
            chooseHero {
                val index = indexOf(kuangjiang)
                if(index>-1){
                    while(g49 == 0){
                        delay(100)
                    }
                    carDoing.downHero(kuangjiang)
                    g49 = 0
                    delay(300)
                    index
                }else{
                    -1
                }

            }
        }

    }
    var g49 = 0

    override suspend fun onKeyDown(code: Int): Boolean {

        if(code == KeyEvent.VK_NUMPAD0){
            if(curGuan==49){
                g49 = 1
                return true
            }
        }

        return super.onKeyDown(code)
    }
}