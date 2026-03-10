package tasks.huodong.huangfeihong

import data.HeroCreator

class HFHHero3 : BaseHFH() {

    var bingqi = HeroCreator.bingqi.create()
    var gugu = HeroCreator.gugu.create()
    var dianfa = HeroCreator.dianfa.create()
    var hugong = HeroCreator.hugong.create()
    var xiaopao = HeroCreator.shenv.create()
    var yanmo = HeroCreator.yanmo.create()


    var hunqiu = HeroCreator.hunqiu.create()
    var guangqiu = HeroCreator.guangqiu.create()
    var moqiu = HeroCreator.moqiu.create()
    var shenv = HeroCreator.shenv.create()

    override fun initHeroes() {
        heros = arrayListOf(bingqi, gugu, dianfa, hugong, xiaopao, yanmo, hunqiu, guangqiu, moqiu, shenv)
        upHeros = arrayListOf(bingqi, gugu, dianfa, xiaopao, yanmo, hugong)

        qiu = moqiu
        qiuTime = 5000L
        super.initHeroes()

    }
}