package tasks.shenhai.zhanjiang

import data.HeroCreator
import kotlinx.coroutines.delay
import tasks.shenhai.BaseSimpleSHHeroDoing

class SHTSHeroDoingBo : BaseSimpleSHHeroDoing() {
    val bingqi = HeroCreator.bingqi.create()
    val niutou = HeroCreator.niutou.create()
    val longwang = HeroCreator.longwang.create()
    val tianshi = HeroCreator.tianshi.create()
    val sishen = HeroCreator.sishen.create()
    
    val feiting = HeroCreator.feiting.create()
    val xiaoye = HeroCreator.xiaoye.create()

    val hunqiu = HeroCreator.hunqiu.create()
    val huanqiu = HeroCreator.huanqiu.create()
    val guangqiu = HeroCreator.guangqiu.create()

    override fun initHeroes() {
        super.initHeroes()
        heros = arrayListOf(bingqi,longwang,xiaoye,feiting,sishen,niutou,hunqiu,huanqiu,guangqiu,tianshi)

        addGuanDeal(0) {
            over {
                fulls(tianshi, niutou, sishen, xiaoye, longwang, bingqi, feiting)
            }
            chooseHero {
                if (!carDoing.carps.get(0).hasHero() || !carDoing.carps.get(1).hasHero()) {
                    upAny(sishen, xiaoye, bingqi, niutou, feiting)
                } else if (!carDoing.carps.get(2).hasHero() || !carDoing.carps.get(3).hasHero()) {
                    //如果01都上好后，2，3有空的，则上mids
                    var index = upAny(tianshi, longwang, feiting)
                    if (index > -1) {
                        index
                    } else {
                        //如果没有mids预选，可以上 0，1位置的英雄
                        val list = carDoing.carps.take(2).map {
                            it.mHeroBean!!
                        }
                        upAny(*list.toTypedArray(), feiting)
                    }
                } else {
                    upAny(tianshi, niutou, sishen, xiaoye, longwang, bingqi, feiting)
                }
            }
        }


        addGuanDeal(109){
            onlyDo {
                carDoing.downHero(longwang)
                carDoing.downHero(tianshi)
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