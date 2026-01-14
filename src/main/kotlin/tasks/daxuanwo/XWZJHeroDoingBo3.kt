package tasks.daxuanwo

import data.HeroCreator
import kotlinx.coroutines.delay
import java.awt.event.KeyEvent

class XWZJHeroDoingBo3 : BaseSimpleXWHeroDoing() {
    val tieqi = HeroCreator.tieqi.create()
    val zhanjiang = HeroCreator.zhanjiang.create()
    val jiaonv = HeroCreator.jiaonv.create()
    val niutou = HeroCreator.niutou.create()
    val yuren = HeroCreator.yuren.create()
    val feiting = HeroCreator.feiting.create()
    val tianshi = HeroCreator.tianshi.create()

    val sishen = HeroCreator.sishen.create()
    val hunqiu = HeroCreator.hunqiu.create()

   
    val guangqiu = HeroCreator.guangqiu.create()


    var lastHun = 0L

    private suspend fun backHun(index: Int): Int {
        if (index > -1) {
            if (System.currentTimeMillis() - lastHun > 2000) {
                lastHun = System.currentTimeMillis()
                return index
            } else {
                delay(2000 - (System.currentTimeMillis() - lastHun))
                lastHun = System.currentTimeMillis()
                return index
            }
        }
        return index
    }

    override fun initHeroes() {
        super.initHeroes()
        g49StartBoss = {
            var index = it.indexOf(hunqiu)
            backHun(index)
        }
        heros = arrayListOf(
            sishen, tieqi, zhanjiang, hunqiu, niutou, yuren, feiting, tianshi, guangqiu, jiaonv
        )
        addGuanDeal(0) {
            over {
                fulls(zhanjiang, niutou,jiaonv,sishen, feiting)
            }
            chooseHero {
                if (zhanjiang.isInCar()) {
                    upAny(zhanjiang, feiting, niutou,jiaonv,sishen)
                } else upAny(zhanjiang)
            }
        }

        addGuanDeal(18) {
            over {
                fulls(zhanjiang, niutou, feiting, tieqi, sishen,jiaonv)
            }
            chooseHero {
                upAny(zhanjiang, niutou, feiting, tieqi, sishen,jiaonv)
            }
        }


        addGuanDeal(38) {
            over {
                fulls(zhanjiang, niutou, feiting, tieqi, jiaonv, tianshi, yuren)
            }
            chooseHero {
                upAny(zhanjiang, niutou, feiting, tieqi, tianshi, yuren, jiaonv)
            }
            onStart {
                carDoing.downHero(sishen)
                delay(3000)
            }
        }
        addGuanDeal(40){

            onlyDo {
                carDoing.downHero(tianshi)
            }
        }

        addGuanDealWithHerosFull(47, listOf(tianshi))


        add49(feiting)


        addGuanDeal(50){

            onlyDo {
                carDoing.downHero(tianshi)
            }
        }
        //内部实际是52关开始
        add50(listOf(zhanjiang, niutou, feiting, tieqi, sishen, jiaonv, yuren),listOf(yuren,jiaonv,))

        add69(listOf(yuren,tianshi),hunqiu)
        curGuanDeal = guanDealList.get(0)
    }


    override suspend fun onKeyDown(code: Int): Boolean {

        if (code == KeyEvent.VK_NUMPAD0) {
            if (curGuan == 49) {
                g49 = 1
                return true
            }
        }

        return super.onKeyDown(code)
    }
}