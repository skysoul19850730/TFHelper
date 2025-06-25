package tasks.hanbing2.zhanjiang

import data.HeroBean
import data.HeroCreator
import kotlinx.coroutines.delay
import tasks.XueLiang
import tasks.hanbing2.BaseSimpleHBHeroDoing
import ui.zhandou.hanbing.HanBingModel

class HB5ZHeroDoingZiQiang : BaseSimpleHBHeroDoing() {

    val isRenwu = false

    val zhanjiang = HeroCreator.zhanjiang2.create()
    val tieqi = HeroCreator.tieqi.create()
    val saman = HeroCreator.saman2.create()
    val wangjiang = HeroCreator.wangjiang2.create()
    val sishen = HeroCreator.sishen2.create()
    val yuren = HeroCreator.yuren.create()
    val muqiu =  HeroBean(if(isRenwu) HanBingModel.renwuKa.value else "muqiu", 40, needCar = false, compareRate = 0.95)
    val bingqi = HeroCreator.bingqi.create()
    val huanqiu = HeroCreator.huanqiu.create()
    val guangqiu = HeroCreator.guangqiu.create()

    override fun initHeroes() {
        super.initHeroes()
        heros = arrayListOf(zhanjiang, tieqi, saman, wangjiang, sishen, yuren, muqiu, bingqi, huanqiu, guangqiu)
        heroDoNotDown129 = zhanjiang

        guanDealList.add(GuanDeal(0, isOver = {
            fullBase() && shengjian
        }, chooseHero = {
            if (zhanjiang.isGold()) {
                upBase(zhuangbei = { shengjian })
            } else {
                upAny(zhanjiang)
            }
        }))

        guanDealList.add(
            GuanDeal(91, isOver = { bingqi.isFull() },
                chooseHero = { upAny(bingqi) }, onGuanDealStart = { carDoing.downHero(yuren) })
        )

        guanDealList.add(GuanDeal(100, isOver = { yuren.isFull() && yandou },
            chooseHero = { upAny(yuren, zhuangbei = { yandou }) }, onGuanDealStart = { carDoing.downHero(bingqi) })
        )

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
        guanDealList.add(GuanDeal(150, isOver = {
           bingqi.isFull() && longxin
        }, chooseHero = {
            upAny(bingqi,zhuangbei = {longxin})
        }, onGuanDealStart = {
            carDoing.downHero(wangjiang)
        }))
        guanDealList.add(
            GuanDeal(159, isOver = {false},
            chooseHero = {
                shuaMu(this) {
                    (guankaTask?.currentGuanIndex?:0)>159
                }
            })
        )
        guanDealList.add(GuanDeal(160, isOver = {fullBase()}
        , chooseHero = {upBase()}
        , onGuanDealStart = {carDoing.downHero(bingqi)}))

        guanDealList.add(GuanDeal(170, isOver = {
            fullBase() && qiangxi
        }, chooseHero = {
            upBase(zhuangbei = { qiangxi })
        }))

        guanDealList.add(
            GuanDeal(179, isOver = { (guankaTask?.currentGuanIndex?:0)>179},
                chooseHero = {
                    shuaMu(this) {
                        (guankaTask?.currentGuanIndex?:0)>179
                    }
                })
        )

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
        var index = heros.indexOf(muqiu)
        if (index > -1 && !isRenwu) {
            while (!over.invoke() && (!XueLiang.isMLess(0.9f))) {
                delay(50)
            }
            return index
        }
        return  -1
    }

    fun fullBase(): Boolean {
        return fulls(zhanjiang, tieqi, saman, wangjiang, sishen, yuren)
    }

    fun List<HeroBean?>.upBase(zhuangbei: (() -> Boolean)? = null): Int {
        return upAny(zhanjiang, tieqi, saman, sishen, wangjiang, yuren, zhuangbei = zhuangbei)
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

}