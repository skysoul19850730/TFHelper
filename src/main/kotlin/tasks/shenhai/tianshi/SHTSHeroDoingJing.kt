package tasks.shenhai.tianshi

import data.HeroCreator
import kotlinx.coroutines.delay
import tasks.shenhai.BaseSimpleSHHeroDoing
import java.awt.event.KeyEvent

class SHTSHeroDoingJing : BaseSimpleSHHeroDoing() {
    val tianshi = HeroCreator.tianshi.create()
    val gugu = HeroCreator.gugu.create()
    val dianfa = HeroCreator.dianfa.create()
    val sishen = HeroCreator.sishen.create()
    val feiting = HeroCreator.feiting.create()
    val niutou = HeroCreator.niutou.create()
    val tuling = HeroCreator.tuling.create()

    val moqiu = HeroCreator.moqiu.create()
    val hunqiu = HeroCreator.hunqiu.create()
    val guangqiu = HeroCreator.guangqiu.create()

    override fun initHeroes() {
        super.initHeroes()
        heros = arrayListOf(gugu,dianfa,sishen,feiting,tuling,niutou,moqiu,hunqiu,guangqiu,tianshi)
//
//        addGuanDeal(0) {
//            over {
//                fulls(tianshi,dianfa,niutou,gugu,tuling,feiting,sishen)
//            }
//            chooseHero {
//                upAny(tianshi,dianfa,gugu,tuling,niutou,sishen,feiting)
//            }
//        }
//
//        var start = 58
//        for (i in start..90) {
//
//            if (i % 10 == 9) {
//               gudingShuaQiuTask("moqiu",i,5000)
//            }
//
//        }

        gudingShuaQiuTask("hunqiu",89,2800)
        gudingShuaQiuTask("hunqiu",99,2800)


        curGuanDeal = guanDealList.get(0)
    }
    override suspend fun onKeyDown(code: Int): Boolean {

        return super.onKeyDown(code)
    }
}