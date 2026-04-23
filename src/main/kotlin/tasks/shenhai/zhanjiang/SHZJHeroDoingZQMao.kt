package tasks.shenhai.zhanjiang

import data.HeroCreator
import kotlinx.coroutines.delay
import tasks.shenhai.BaseSimpleSHHeroDoing

class SHZJHeroDoingZQMao : BaseSimpleSHHeroDoing() {
    val tieqi = HeroCreator.tieqi.create()
    val zhanjiang = HeroCreator.zhanjiang.create()
    val gugu = HeroCreator.gugu.create()
    val tuling = HeroCreator.tuling.create()
    val feiting = HeroCreator.feiting.create()
    val maomi = HeroCreator.maomi.create()
    val tianshi = HeroCreator.tianshi.create()

    val moqiu = HeroCreator.moqiu.create()
    val hunqiu = HeroCreator.hunqiu.create()
    val guangqiu = HeroCreator.guangqiu.create()

    override fun initHeroes() {
        super.initHeroes()
        heros = arrayListOf(tieqi, zhanjiang, maomi, feiting, tuling, tianshi, moqiu, hunqiu, guangqiu, gugu)

        addGuanDeal(0) {
            over {
                fulls(zhanjiang, maomi, gugu, feiting)
            }
            chooseHero {
                if (zhanjiang.isInCar()) {
                    upAny(zhanjiang, maomi, gugu, feiting)
                } else upAny(zhanjiang)
            }
        }

        addGuanDealWithHerosFull(28, listOf(zhanjiang, feiting, gugu, tieqi, tuling, maomi))


        var start = 48
        for (i in start..100) {

            val lastNum = i % 10

            if(lastNum == 8){
                addGuanDealWithHerosFull(i, listOf(tianshi), delay = 2000)
            }
            if(lastNum==9){
                gudingShuaQiuTask("hunqiu",i,2000, overGuan = i+1)
            }
            if(lastNum == 0){
                downHero(i,tianshi)
            }

        }


        curGuanDeal = guanDealList.get(0)
    }

    override suspend fun onKeyDown(code: Int): Boolean {

        return super.onKeyDown(code)
    }
}