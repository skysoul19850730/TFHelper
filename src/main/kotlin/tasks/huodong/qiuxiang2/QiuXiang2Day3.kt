package tasks.huodong.qiuxiang2

import data.HeroCreator

class QiuXiang2Day3 : BaseQiuxiang2() {
    val dianfa = HeroCreator.dianfa.create()
    val hugong = HeroCreator.hugong.create()
    val gugu = HeroCreator.gugu.create()
    val houyi = HeroCreator.houyi.create()
    val shenv = HeroCreator.shenv.create()
    val maomi = HeroCreator.maomi.create()
    val bingqi = HeroCreator.bingqi.create()
    val moqiu = HeroCreator.moqiu.create()
    val bingqiu = HeroCreator.bingqiu.create()
    val shexian = HeroCreator.shexian.create()




    override fun initHeroes() {
        heros = arrayListOf(dianfa, gugu, maomi, shexian, bingqiu, bingqi, houyi, shenv, hugong, moqiu)

        guanDealList.add(GuanDeal(
            startGuan = 0,
            isOver = {
                fulls(hugong,dianfa,gugu,houyi,shenv,shexian)
            },
            chooseHero = {
                upAny(hugong,dianfa,gugu,houyi,shenv,shexian)
            }
        ))
        guanDealList.add(GuanDeal(
            startGuan = 50,
            isOver = {
                fulls(maomi)
            },
            chooseHero = {
                upAny(maomi)
            }
        ))

        guanDealList.add(GuanDeal(
            startGuan = 91,
            onlyDoSomething = {
                carDoing.downHero(shexian)
            }
        ))

        gudingShuaQiuTask("moqiu",99,5000,null,100)

        guanDealList.add(GuanDeal(
            startGuan = 101,
            isOver = {
                fulls(shexian)
            },
            chooseHero = {
                upAny(shexian)
            }
        ))

        guanDealList.add(GuanDeal(
            startGuan = 141,
            isOver = {
                fulls(bingqi)
            },
            chooseHero = {
                carDoing.downHero(shenv)
                upAny(bingqi)
            }
        ))

        gudingShuaQiuTask("moqiu",149,5000,null,150)

        guanDealList.add(GuanDeal(
            startGuan = 151,
            isOver = {
                fulls(shenv)
            },
            chooseHero = {
                carDoing.downHero(bingqi)
                upAny(shenv)
            }
        ))

        curGuanDeal = guanDealList.get(0)
    }


}