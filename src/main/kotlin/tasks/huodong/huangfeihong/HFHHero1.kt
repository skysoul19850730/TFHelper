package tasks.huodong.huangfeihong

import data.HeroCreator

class HFHHero1 : BaseHFH() {

    var bingqi = HeroCreator.bingqi.create()
    var gugu = HeroCreator.gugu.create()
    var hugong = HeroCreator.hugong.create()
    var niutou = HeroCreator.niutou.create()
    var dianfa = HeroCreator.dianfa.create()
    var yuren = HeroCreator.yuren.create()
    var xiaochou = HeroCreator.xiaochou.create()
    var guangqiu = HeroCreator.guangqiu.create()
    var moqiu = HeroCreator.moqiu.create()
    var shexian = HeroCreator.shexian.create()

    override fun initHeroes() {
        heros = arrayListOf(bingqi, gugu, hugong, niutou, dianfa, yuren, xiaochou, guangqiu, moqiu, shexian)
        upHeros = arrayListOf(bingqi, gugu, hugong, dianfa, yuren, xiaochou, shexian)
        qiu = moqiu
        qiuTime = 5000L

        super.initHeroes()

    }
}