package tasks.daxuanwo

import data.HeroCreator
import kotlinx.coroutines.delay
import java.awt.event.KeyEvent

class XWZJHeroDoingZiQiang : BaseSimpleXWHeroDoing() {
    val dianfa = HeroCreator.dianfa.create()
    val zhanjiang = HeroCreator.niutou.create()
    val wuyi = HeroCreator.wuyi.create()
    val feiting = HeroCreator.feiting.create()
    val gugu = HeroCreator.gugu.create()

    val tianshi = HeroCreator.tianshi.create()
    val moqiu = HeroCreator.moqiu.create()

    val muqiu = HeroCreator.hunqiu.create()
    val guangqiu = HeroCreator.guangqiu.create()
    val jiaonv = HeroCreator.jiaonv.create()

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
        heros = arrayListOf(dianfa,zhanjiang,wuyi,feiting,gugu,tianshi,moqiu,muqiu,guangqiu,jiaonv)
        addGuanDeal(0){
            over {
                fulls(zhanjiang,gugu,feiting,tianshi,wuyi,jiaonv,dianfa)
            }
            chooseHero {
//                if(zhanjiang.isInCar()) {
                    upAny(feiting,zhanjiang,gugu,tianshi,wuyi,jiaonv,dianfa)
//                }else upAny(zhanjiang)
            }
        }

        add49WithQiu(gugu,moqiu,5000)

//        add50(listOf(zhanjiang,feiting,dianfa,tianshi,gugu,wuyi,jiaonv), listOf(jiaonv,wuyi))
//        add69(auto = true)
        curGuanDeal = guanDealList.get(0)
    }

    override suspend fun onKeyDown(code: Int): Boolean {

        return super.onKeyDown(code)
    }
}