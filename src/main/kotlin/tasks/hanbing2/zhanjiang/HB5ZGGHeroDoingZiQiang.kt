package tasks.hanbing2.zhanjiang

import data.HeroBean
import data.HeroCreator
import kotlinx.coroutines.delay
import tasks.XueLiang
import tasks.hanbing2.BaseSimpleHBHeroDoing
import java.awt.event.KeyEvent

class HB5ZGGHeroDoingZiQiang : BaseSimpleHBHeroDoing() {

    val isRenwu = false

    val zhanjiang = HeroCreator.zhanjiang2.create()
    val tieqi = HeroCreator.tieqi.create()
    val gugu = HeroCreator.gugu.create()
    val wangjiang = HeroCreator.wangjiang2.create()
    val sishen = HeroCreator.sishen2.create()
    val yuren = HeroCreator.yuren.create()
    val dianfa = HeroCreator.dianfa.create()
    val haiyao = HeroCreator.haiyao.create()
    val huanqiu = HeroCreator.huanqiu.create()
    val guangqiu = HeroCreator.guangqiu.create()

    override fun initHeroes() {
        super.initHeroes()
        heros = arrayListOf(zhanjiang, tieqi, gugu, wangjiang, sishen, yuren, dianfa, haiyao, huanqiu, guangqiu)
        heroDoNotDown129 = zhanjiang

        guanDealList.add(GuanDeal(0, isOver = {
           fulls(zhanjiang,haiyao,gugu,tieqi,dianfa,yuren)
        }, chooseHero = {
            if (zhanjiang.isGold()) {
               upAny(zhanjiang,haiyao,gugu,tieqi,dianfa,yuren)
            } else {
                upAny(zhanjiang)
            }
        }))

        guanDealList.add(GuanDeal(10, isOver = {
            fullBase() && longxin
        }, chooseHero = {
           carDoing.downHero(haiyao)
            upBase(zhuangbei = { longxin })
        }))

        guanDealList.add(GuanDeal(99, isOver = {
            false
        }, chooseHero = {
            while(zs99Clicked==0 && currentGuan()<100){
                delay(200)
            }
            if(zs99Clicked==1){
                carDoing.downHero(yuren)
                carDoing.downHero(tieqi)
                delay(10000)
                zs99Clicked=2
            }
            if(zs99Clicked==2){
                upAny(tieqi,yuren)
            }else -1

        }))

        guanDealList.add(GuanDeal(100, isOver = {
            fullBase()
        }, chooseHero = {
            carDoing.downHero(haiyao)
            upBase(zhuangbei = { longxin })
        }))

        guanDealList.add(
            GuanDeal(109, isOver = { false },
                chooseHero = {
                    delay(300)
                    val ind =upBase()
                    if(ind<0){
                        upAny(guangqiu)
                    }else ind
                })
        )

        guanDealList.add(GuanDeal(111, isOver = {
            fullBase() && qiangxi
        }, chooseHero = {
            if(needReCheckStar) {
                carDoing.reCheckStars()
                needReCheckStar = false
            }
            upBase(zhuangbei = { qiangxi })
        }, onGuanDealStart = {
            needReCheckStar = true
        }))

        guanDealList.add(
            GuanDeal(129, onlyDoSomething = {
                startChuanZhangOberserver()
            })
        )
        guanDealList.add(GuanDeal(130, isOver = {
            fullBase() && yandou
        }, chooseHero = {
            upBase { yandou }
        }, onGuanDealStart = {stopChuanZhangOberserver()}))

        guanDealList.add(GuanDeal(135, isOver = {
            wangjiang.isInCar()
        }, chooseHero = {
            carDoing.downHero(yuren)
            upAny(wangjiang)
        }))

        guanDealList.add(GuanDeal(140, isOver = {
            fulls(yuren,haiyao)
        }, chooseHero = {
            carDoing.downHero(wangjiang)
            carDoing.downHero(dianfa)
            upAny(yuren,haiyao)
        }))

        guanDealList.add(GuanDeal(150, isOver = {
            fullBase()&&qiangxi
        }, chooseHero = {
            carDoing.downHero(haiyao)
            carDoing.downHero(wangjiang)
            upBase(zhuangbei = { qiangxi })
        }))

        guanDealList.add(GuanDeal(180, isOver = {
            longxin
        }, chooseHero = {upAny(zhuangbei= { longxin })}))

        guanDealList.add(
            GuanDeal(189, isOver = {currentGuan() > 189},
            chooseHero = {
                delay(300)
                val ind =upBase()
                if(ind<0){
                    upAny(guangqiu)
                }else ind
//                checkHeroStarAndFull(this) { currentGuan() > 189 }
            })
        )

        guanDealList.add(GuanDeal(191, isOver = {
            fullBase() && longxin
        }, chooseHero = {
            if(needReCheckStar) {
                carDoing.reCheckStars()
                needReCheckStar = false
            }
            upBase(zhuangbei = { longxin })
        }, onGuanDealStart = {
            needReCheckStar = true
        }))

        curGuanDeal = guanDealList.first()
    }

    private fun currentGuan():Int{
        return guankaTask?.currentGuanIndex?:0
    }



    private suspend fun shuaMu(heros:List<HeroBean?>,over:()->Boolean):Int{
        var index = heros.indexOf(dianfa)
        if (index > -1 && !isRenwu) {
            while (!over.invoke() && (!XueLiang.isMLess(0.9f))) {
                delay(50)
            }
            return index
        }
        return  -1
    }

    fun fullBase(): Boolean {
        return fulls(zhanjiang, tieqi, gugu, dianfa, sishen, yuren)
    }

    fun List<HeroBean?>.upBase(zhuangbei: (() -> Boolean)? = null): Int {
        return upAny(zhanjiang, tieqi, gugu, sishen, dianfa, yuren, zhuangbei = zhuangbei)
    }

    private suspend fun checkHeroStarAndFull(heros: List<HeroBean?>,over: () -> Boolean): Int {
        if (carDoing.hasOpenSpace() || carDoing.hasNotFull()) {
            return heros.upBase()
        }
        while (!over.invoke()) {
            delay(500)
            carDoing.reCheckStars()

            if (carDoing.hasOpenSpace() || carDoing.hasNotFull()) {
                return heros.upBase()
            }
        }
        return -1
    }


    override fun onChuanzhangClick(position: Int) {
    }

    var zs99Clicked = 0
    override suspend fun onKeyDown(code: Int): Boolean {
        if(guankaTask?.currentGuanIndex ==128 || guankaTask?.currentGuanIndex == 129){
            if(code == KeyEvent.VK_NUMPAD3){
                zs99Clicked = 1
            }
        }

        return super.onKeyDown(code)
    }

}