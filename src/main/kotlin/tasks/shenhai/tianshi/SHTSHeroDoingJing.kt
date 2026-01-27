package tasks.shenhai.tianshi

import data.HeroCreator
import kotlinx.coroutines.delay
import tasks.shenhai.BaseSimpleSHHeroDoing
import java.awt.event.KeyEvent

class SHTSHeroDoingJing : BaseSimpleSHHeroDoing() {
    val tianshi = HeroCreator.tianshi.create()
    val xiaoye = HeroCreator.xiaoye.create()
    val dianfa = HeroCreator.dianfa.create()
    val sishen = HeroCreator.sishen.create()
    val shexian = HeroCreator.shexian.create()
    val niutou = HeroCreator.niutou.create()
    val tuling = HeroCreator.tuling.create()

    val moqiu = HeroCreator.moqiu.create()
    val hunqiu = HeroCreator.hunqiu.create()
    val guangqiu = HeroCreator.guangqiu.create()

    override fun initHeroes() {
        super.initHeroes()
        heros = arrayListOf(xiaoye,dianfa,sishen,shexian,tuling,niutou,moqiu,hunqiu,guangqiu,tianshi)

        addGuanDeal(0) {
            over {
                fulls(dianfa,niutou)
            }
            chooseHero {
                if (dianfa.isInCar()) {
                    upAny(dianfa, niutou,shexian)
                } else upAny(dianfa)
            }
        }


        addGuanDeal(18){
            over {
                niutou.isFull() && xiaoye.isInCar()
            }
            chooseHero {
                upAny(dianfa,xiaoye, niutou,shexian)
            }
        }

        addGuanDeal(28){
            over{
                fulls(dianfa,niutou,shexian,xiaoye,sishen,tuling)
            }
            chooseHero {
                upAny(dianfa,niutou,shexian,xiaoye,sishen,tuling)
            }
        }


        var start = 48
        for (i in start..100) {

            if (i % 10 == 0) {

                addGuanDeal(i){
                    onlyDo {
                        carDoing.downHero(tianshi)
                    }
                }
            } else if (i % 10 == 9) {
                addGuanDeal(i){
                    over {
                        curGuan>i
                    }
                    chooseHero {
                        if(tianshi.isFull()){
                            backHun(indexOf(hunqiu))
                        }else{
                            upAny(tianshi)
                        }

                    }
                }
            }

        }


        curGuanDeal = guanDealList.get(0)
    }
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
    override suspend fun onKeyDown(code: Int): Boolean {

        return super.onKeyDown(code)
    }
}