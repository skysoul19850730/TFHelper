package tasks.huodong.fengbao

import data.HeroBean
import data.HeroCreator
import tasks.SimpleHeZuoHeroDoing

class Fengbao1 : SimpleHeZuoHeroDoing() {

    val tieqi = HeroCreator.dianfa.create()
    val tianshi = HeroCreator.tianshi.create()
    val yuren = HeroCreator.yuren.create()
    val gugu = HeroCreator.gugu.create()
    val shengqi = HeroCreator.shengqi.create()
    val saman2 = HeroCreator.saman2.create()
    val wangjiang2 = HeroCreator.wangjiang2.create()
    val bingqiu = HeroCreator.bingqiu.create()
    val xiaoye = HeroCreator.xiaoye.create()
    val shexian = HeroCreator.shexian.create()
    override fun initHeroes() {
        super.initHeroes()
        guanDealList.add(GuanDeal(
            0,
            isOver = {
                fulls(tieqi, tianshi, yuren, gugu, saman2, xiaoye, shexian)
            },
            chooseHero = {
                upAny(tieqi, tianshi, yuren, gugu, saman2, xiaoye, shexian)
            }
        ))

        guanDealList.add(GuanDeal(
            90,
            isOver = {
                wangjiang2.isInCar()
            },
            chooseHero = {
                upAny(wangjiang2)
            },
            onGuanDealStart = {
                carDoing.downHero(saman2)
            }
        ))

        guanDealList.add(GuanDeal(
            100,
            isOver = {
                fulls(tieqi, tianshi, yuren, gugu, saman2, xiaoye, shexian)
            },
            chooseHero = {
                upAny(tieqi, tianshi, yuren, gugu, saman2, xiaoye, shexian)
            }, onGuanDealStart = {
                carDoing.downHero(wangjiang2)
            }
        ))

        curGuanDeal = guanDealList.get(0)
    }

}
