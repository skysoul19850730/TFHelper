package tasks.huodong.qiuxiang2

import data.HeroCreator

class QiuXiang2Day7 : BaseQiuxiang2() {
    val yuren = HeroCreator.yuren.create()
    val zhanjiang2 = HeroCreator.yanmo.create()
    val saman2 = HeroCreator.dianfa.create()
    val niutou2 = HeroCreator.longwang.create()
    val wangjiang2 = HeroCreator.gugu.create()
    val guangqiu = HeroCreator.shexian.create()

    val bingqi = HeroCreator.maomi.create()

    val maomi = HeroCreator.youling.create()
    val huanqiu = HeroCreator.shengqi.create()
    val muqiu = HeroCreator.bingqiu.create()


    override fun initHeroes() {
        heros = arrayListOf(yuren, zhanjiang2, saman2, niutou2, wangjiang2, maomi, bingqi, guangqiu, huanqiu, muqiu)

        bingQiu = muqiu
        dabing = true

        guanDealList.add(GuanDeal(
            startGuan = 0,
            isOver = {
                fulls(yuren, zhanjiang2, saman2, niutou2, wangjiang2, guangqiu)
            },
            chooseHero = {
                upAny(yuren, zhanjiang2, saman2, niutou2, wangjiang2, guangqiu)
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

        add149()

        curGuanDeal = guanDealList.get(0)
    }


}