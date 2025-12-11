package tasks.daxuanwo

import data.HeroCreator
import kotlinx.coroutines.delay
import java.awt.event.KeyEvent

class XWZJHeroDoingBo : BaseSimpleXWHeroDoing() {
    val tieqi = HeroCreator.tieqi.create()
    val zhanjiang = HeroCreator.zhanjiang.create()
    val sishen = HeroCreator.sishen.create()
    val niutou = HeroCreator.niutou.create()
    val yuren = HeroCreator.yuren.create()
    val feiting = HeroCreator.feiting.create()

    val haiyao = HeroCreator.haiyao.create()
    val bingqi = HeroCreator.bingqi.create()

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
            haiyao, tieqi, zhanjiang, bingqi, niutou, yuren, feiting, hunqiu, guangqiu, sishen
        )
        addGuanDeal(0) {
            over {
                fulls(zhanjiang, niutou, sishen, feiting)
            }
            chooseHero {
                if (zhanjiang.isInCar()) {
                    upAny(zhanjiang, feiting, niutou, sishen)
                } else upAny(zhanjiang)
            }
        }

        addGuanDeal(18) {
            over {
                fulls(zhanjiang, niutou, feiting, tieqi, sishen, haiyao)
            }
            chooseHero {
                upAny(zhanjiang, niutou, feiting, tieqi, sishen, haiyao)
            }
        }


        addGuanDeal(38) {
            over {
                fulls(zhanjiang, niutou, feiting, tieqi, sishen, haiyao, yuren)
            }
            chooseHero {
                upAny(zhanjiang, niutou, feiting, tieqi, sishen, haiyao, yuren)
            }
        }

        add49(feiting)


        add50(listOf(zhanjiang, niutou, feiting, tieqi, sishen, bingqi, yuren),listOf(tieqi,yuren))

        add69()
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