package tasks.shenhai.zhanjiang

import data.HeroCreator
import kotlinx.coroutines.delay
import tasks.XueLiang
import tasks.shenhai.BaseSimpleSHHeroDoing
import java.awt.event.KeyEvent

class SHTSHeroDoingBo : BaseSimpleSHHeroDoing() {
    val zhanjiang = HeroCreator.zhanjiang.create()
    val niutou = HeroCreator.niutou.create()
    val tieqi = HeroCreator.tieqi.create()
    val tianshi = HeroCreator.tianshi.create()
    val sishen = HeroCreator.sishen.create()
    
    val feiting = HeroCreator.feiting.create()
    val dianfa = HeroCreator.dianfa.create()

    val hunqiu = HeroCreator.hunqiu.create()
    val huanqiu = HeroCreator.huanqiu.create()
    val guangqiu = HeroCreator.guangqiu.create()

    var midHeroes = listOf(tieqi,tianshi)
    var gState = 0
    override fun initHeroes() {
        super.initHeroes()
        heros = arrayListOf(zhanjiang,tieqi,dianfa,feiting,sishen,niutou,hunqiu,huanqiu,guangqiu,tianshi)


        addGuanDeal(0) {
            over {
                fulls(tianshi, niutou, sishen, dianfa, tieqi, zhanjiang, feiting)
            }
            chooseHero {
                if(!zhanjiang.isInCar()){
                    upAny(zhanjiang,feiting)
                }else if (!carDoing.carps.get(1).hasHero()) {
                    upAny(sishen, dianfa, zhanjiang, niutou, feiting)
                } else if (!carDoing.carps.get(2).hasHero() || !carDoing.carps.get(3).hasHero()) {
                    //如果01都上好后，2，3有空的，则上mids
                    var index = upAny(tianshi, tieqi, feiting)
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
                    upAny(tianshi, niutou, sishen, dianfa, tieqi, zhanjiang, feiting)
                }
            }
        }

        addGuanDeal(91){
            over {
                sishen.currentLevel == 3
            }
            chooseHero {
                upAny(sishen)
            }
            onStart {
                carDoing.downHero(sishen)
                delay(200)
            }
        }

        addGuanDeal(95){
            over {
                !running
            }
            chooseHero {
                if(gState==0){
                    //上满
                    if(midHeroes.all {
                        it.isFull()
                        }){
                        while(running && gState==0){
                            delay(200)
                        }

                        midHeroes.forEach {
                            carDoing.downHero(it)
                        }

                        -1
                    }else{
                        upAny(tieqi,tianshi, useGuang = false)
                    }

                }else{

                    val pre = upAny(midHeroes)
                    if(pre>-1){
                        XueLiang.observerXueDown(0.5f) {
                            gState != 1 || !running
                        }
                        delay(500)
                        gState = 0
                        pre
                    }else{
                        -1
                    }
                }
            }
        }

        curGuanDeal = guanDealList.get(0)
    }
    override suspend fun onKeyDown(code: Int): Boolean {
        if (code == KeyEvent.VK_NUMPAD0) {
            gState += 1
            if (gState >= 2) {
                gState = 0
            }
            return true
        }
        return super.onKeyDown(code)
    }
}