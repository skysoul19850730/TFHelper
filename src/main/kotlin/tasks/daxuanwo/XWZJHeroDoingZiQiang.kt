package tasks.daxuanwo

import data.HeroCreator
import kotlinx.coroutines.delay
import java.awt.event.KeyEvent

class XWZJHeroDoingZiQiang : BaseSimpleXWHeroDoing() {
    val tieqi = HeroCreator.tieqi.create()
    val zhanjiang = HeroCreator.zhanjiang.create()
    val yuren = HeroCreator.yuren.create()
    val feiting = HeroCreator.feiting.create()
    val gugu = HeroCreator.gugu.create()

    val niutou = HeroCreator.niutou.create()
    val sishen = HeroCreator.sishen.create()

    val muqiu = HeroCreator.hunqiu.create()
    val guangqiu = HeroCreator.guangqiu.create()
    val haiyao = HeroCreator.haiyao.create()

    var lastHun = 0L

    private suspend fun backHun(index: Int): Int {
        if (index > -1) {
            if (System.currentTimeMillis() - lastHun > 2000) {
                return index
            } else {
                delay(2000 - (System.currentTimeMillis() - lastHun))
                return index
            }
        }
        return index
    }
    override fun initHeroes() {
        super.initHeroes()
        g49StartBoss = {
            var index = it.indexOf(muqiu)
            backHun(index)
        }
        heros = arrayListOf(tieqi,zhanjiang,yuren,feiting,gugu,niutou,sishen,muqiu,guangqiu,haiyao)
        addGuanDeal(0){
            over {
                fulls(zhanjiang,gugu,feiting,sishen,niutou)
            }
            chooseHero {
                if(zhanjiang.isInCar()) {
                    upAny(zhanjiang,gugu,feiting,sishen,niutou)
                }else upAny(zhanjiang)
            }
        }

        addGuanDeal(18){
            over {
                fulls(zhanjiang,sishen,feiting,tieqi,niutou,gugu,yuren)
            }
            chooseHero{
                upAny(zhanjiang,sishen,feiting,tieqi,niutou,gugu,yuren)
            }
        }

        addGuanDeal(41){
            over {
                haiyao.isFull()
            }

            chooseHero {
                carDoing.downHero(yuren)
                upAny(haiyao)
            }
        }

        add49(gugu)

        curGuanDeal = guanDealList.get(0)
    }

    override suspend fun onKeyDown(code: Int): Boolean {

        return super.onKeyDown(code)
    }
}