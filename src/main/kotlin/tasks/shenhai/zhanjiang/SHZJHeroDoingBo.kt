package tasks.shenhai.zhanjiang

import data.HeroCreator
import kotlinx.coroutines.delay
import tasks.shenhai.BaseSimpleSHHeroDoing
import java.awt.event.KeyEvent

class SHZJHeroDoingBo : BaseSimpleSHHeroDoing() {
    val tieqi = HeroCreator.tieqi.create()
    val zhanjiang = HeroCreator.zhanjiang.create()
    val yuren = HeroCreator.yuren.create()
    val shexian = HeroCreator.shexian.create()
    val niutou = HeroCreator.niutou.create()
    val tuling = HeroCreator.tuling.create()
    val tianshi = HeroCreator.tianshi.create()

    val muqiu = HeroCreator.muqiu.create()
    val hunqiu = HeroCreator.hunqiu.create()
    val guangqiu = HeroCreator.guangqiu.create()

    override fun initHeroes() {
        super.initHeroes()
        heros = arrayListOf(tieqi,zhanjiang,yuren,shexian,tuling,niutou,muqiu,hunqiu,guangqiu,tianshi)

        addGuanDeal(0) {
            over {
                fulls(zhanjiang,niutou)
            }
            chooseHero {
                if (zhanjiang.isInCar()) {
                    upAny(zhanjiang, niutou,shexian)
                } else upAny(zhanjiang)
            }
        }


        addGuanDeal(18){
            over {
                niutou.isFull() && tieqi.isInCar()
            }
            chooseHero {
                upAny(zhanjiang,tieqi, niutou,shexian)
            }
        }

        addGuanDeal(28){
            over{
                fulls(zhanjiang,niutou,shexian,tieqi,yuren,tuling)
            }
            chooseHero {
                upAny(zhanjiang,niutou,shexian,tieqi,yuren,tuling)
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