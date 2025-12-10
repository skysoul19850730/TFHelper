package tasks.huodong.huangfeihong

import data.HeroCreator

class HFHHero3 : BaseHFH() {

    var bingqi = HeroCreator.bingqi.create()
    var gugu = HeroCreator.gugu.create()
    var dianfa = HeroCreator.dianfa.create()
    var niutou = HeroCreator.niutou.create()
    var wugui = HeroCreator.wugui.create()
    var yuren = HeroCreator.yuren.create()


    var shitou = HeroCreator.shitou.create()
    var guangqiu = HeroCreator.guangqiu.create()
    var wangjiang = HeroCreator.wangjiang.create()
    var shexian = HeroCreator.shexian.create()

    override fun initHeroes() {
        heros = arrayListOf(bingqi, gugu, dianfa, niutou, wugui, yuren, shitou, guangqiu, wangjiang, shexian)
        upHeros = arrayListOf(bingqi, gugu, dianfa, wugui, yuren, niutou, shexian)

        super.initHeroes()

    }
}