package tasks.daxuanwo

import data.HeroCreator
import kotlinx.coroutines.delay
import java.awt.event.KeyEvent

class XWZJHeroDoingDairen : BaseSimpleXWHeroDoing() {
    val tieqi = HeroCreator.tieqi.create()
    val zhanjiang = HeroCreator.zhanjiang.create()
    val yuren = HeroCreator.yuren.create()
    val feiting = HeroCreator.feiting.create()
    val sishen = HeroCreator.sishen.create()

    val niutou = HeroCreator.niutou.create()
    val moqiu = HeroCreator.moqiu.create()

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
        auto59 = true
        g49StartBoss = {
            var index = it.indexOf(muqiu)
            backHun(index)
        }
        heros = arrayListOf(tieqi,zhanjiang,yuren,feiting,sishen,niutou,moqiu,muqiu,guangqiu,haiyao)
        addGuanDeal(0){
            over {
                fulls(zhanjiang,sishen,feiting,niutou)
            }
            chooseHero {
                if(zhanjiang.isInCar()) {
                    upAny(zhanjiang,sishen,feiting,niutou)
                }else upAny(zhanjiang)
            }
        }

        addGuanDeal(18){
            over {
                fulls(zhanjiang,feiting,tieqi,niutou,sishen,yuren,haiyao)
            }
            chooseHero{
                upAny(zhanjiang,feiting,tieqi,niutou,sishen,yuren,haiyao)
            }
        }


        add49WithQiu(feiting,moqiu,5000)

        curGuanDeal = guanDealList.get(0)
    }

    override suspend fun onKeyDown(code: Int): Boolean {

        return super.onKeyDown(code)
    }
}