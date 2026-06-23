package tasks.shenhai.zhanjiang

import data.HeroCreator
import kotlinx.coroutines.delay
import tasks.shenhai.BaseSimpleSHHeroDoing

class SHZJHeroDoingZQZhangyu : BaseSimpleSHHeroDoing() {
    val tieqi = HeroCreator.tieqi.create()
    val zhanjiang = HeroCreator.zhanjiang.create()
    val yuren = HeroCreator.yuren.create()
    val feiting = HeroCreator.feiting.create()
    val shuiling = HeroCreator.shuiling.create()
    val tuling = HeroCreator.tuling.create()
    val gugu = HeroCreator.gugu.create()

    val moqiu = HeroCreator.moqiu.create()
    val hunqiu = HeroCreator.hunqiu.create()
    val guangqiu = HeroCreator.guangqiu.create()


    var lastMoTime = System.currentTimeMillis()
    var lastHunTime = System.currentTimeMillis()
    override fun initHeroes() {
        super.initHeroes()
        heros = arrayListOf(tieqi, zhanjiang, yuren, feiting, tuling, shuiling, moqiu, hunqiu, guangqiu, gugu)

        addGuanDeal(0) {
            over {
                fulls(zhanjiang, shuiling, feiting, gugu, tieqi, tuling, yuren)
            }
            chooseHero {
                if (zhanjiang.isInCar()) {
                    upAny(zhanjiang, shuiling, feiting, gugu, tieqi, tuling, yuren, zhuangbei = { longxin })
                } else upAny(zhanjiang)
            }
        }


        var start = 68
        for (i in start..100) {

            if (i % 10 == 9) {
                addGuanDeal(i) {
                    over {
                        curGuan > i
                    }
                    chooseHero {
//                       val moIndex = indexOf(moqiu)
                        val hunIndex = indexOf(hunqiu)


                        // fun needMo():Boolean{
                        //     val nowTime = System.currentTimeMillis()
                        //     return moIndex>-1 && nowTime-lastMoTime>5500
                        // }
                        fun needHun(): Boolean {
                            val nowTime = System.currentTimeMillis()
                            return hunIndex > -1 && nowTime - lastHunTime > 4000
                        }
                        if (hunIndex > -1) {
                            while (running) {
                                // if(needMo()){
                                //     lastMoTime = System.currentTimeMillis()
                                //     return@chooseHero moIndex
                                //  }
                                if (needHun()) {
                                    lastHunTime = System.currentTimeMillis()
                                    return@chooseHero hunIndex
                                }
                                delay(300)
                            }
                        }

                        return@chooseHero -1
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