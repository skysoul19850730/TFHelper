package tasks.huodong.shuihu2

import data.HeroBean
import data.HeroCreator
import kotlinx.coroutines.delay
import tasks.SimpleHeZuoHeroDoing
import tasks.XueLiang
import kotlin.math.max

class Day7 : SimpleHeZuoHeroDoing() {
    val dianfa = HeroCreator.dianfa.create()
    val longwang = HeroCreator.longwang.create()
    val yanmo = HeroCreator.yanmo.create()
    val bingqi = HeroCreator.bingqi.create()
    val xiaoye = HeroCreator.xiaoye.create()
    val zhongkui = HeroCreator.zhongkui.create()
    val ganglie = HeroCreator.ganglie.create()
    val huofa = HeroCreator.huofa.create()
    val muqiu = HeroCreator.muqiu.create()
    val haiyao = HeroCreator.haiyao.create()

    override fun initHeroes() {
        heros = arrayListOf(dianfa, longwang, yanmo, bingqi, xiaoye, zhongkui, ganglie, huofa, muqiu, haiyao)

        guanDealList.add(GuanDeal(0, isOver = {
            fulls(dianfa, huofa, xiaoye, zhongkui) && inCars(longwang, yanmo)
        }, chooseHero = {
            if (carDoing.openCount() < 2) {
                upAny(dianfa, huofa, xiaoye, zhongkui)
            } else {
                if (carDoing.openCount() < 4) {
                    var hs = carDoing.carps.filter {
                        it.mHeroBean != null
                    }.map {
                        it.mHeroBean!!
                    }
                    upAny(longwang, yanmo, *hs.toTypedArray())
                } else {
                    upAny(dianfa, huofa, xiaoye, zhongkui, longwang, yanmo)
                }
            }
        }))

        guanDealList.add(GuanDeal(49, onlyDoSomething = {
            carDoing.downHero(longwang)
            carDoing.downHero(yanmo)
        }))
        guanDealList.add(GuanDeal(50, isOver = {
            inCars(longwang, yanmo)
        }, chooseHero = {
            upAny(longwang, yanmo)
        }))
        guanDealList.add(GuanDeal(90, isOver = {
            inCars(yanmo) && bingqi.isFull()
        }, chooseHero = {
            upAny(bingqi)
        }, onGuanDealStart = {
            carDoing.downHero(longwang)
        }))



        guanDealList.add(
            GuanDeal(
                100,
                isOver = { ganglie.isFull() },
                chooseHero = {
                    upAny(ganglie)
                },
                onGuanDealStart = {
                    carDoing.downHero(bingqi)
                })
        )
        guanDealList.add(
            GuanDeal(
                110,
                isOver = { haiyao.isFull() },
                chooseHero = {
                    upAny(haiyao)
                },
                onGuanDealStart = {
                    carDoing.downHero(ganglie)
                })
        )

        guanDealList.add(
            GuanDeal(
                120,
                isOver = { ganglie.isFull() },
                chooseHero = {
                    upAny(ganglie)
                },
                onGuanDealStart = {
                    carDoing.downHero(haiyao)
                })
        )
        guanDealList.add(
            GuanDeal(
                130,
                isOver = { haiyao.isFull() },
                chooseHero = {
                    upAny(haiyao)
                },
                onGuanDealStart = {
                    carDoing.downHero(ganglie)
                })
        )
        curGuanDeal = guanDealList.get(0)
    }


}