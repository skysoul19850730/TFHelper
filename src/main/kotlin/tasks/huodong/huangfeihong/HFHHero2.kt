package tasks.huodong.huangfeihong

import data.HeroCreator

class HFHHero2 : BaseHFH() {//野法皮）

    var maomi = HeroCreator.maomi.create()
    var gugu = HeroCreator.gugu.create()
    var dijing = HeroCreator.dijing.create()
    var tianshi = HeroCreator.tianshi.create()
    var bingqi = HeroCreator.bingqi.create()
    var leishen = HeroCreator.leishen.create()


    var moqiu = HeroCreator.moqiu.create()
    var hunqiu = HeroCreator.niutou.create()
    var wuyi = HeroCreator.shexian.create()
    var guangqiu = HeroCreator.guangqiu.create()

    override fun initHeroes() {
        heros = arrayListOf(gugu, wuyi, leishen, moqiu, maomi, tianshi, bingqi, guangqiu, hunqiu, dijing)
        upHeros = arrayListOf(gugu, tianshi, dijing, maomi, leishen, bingqi)
        qiu = moqiu
        qiuTime = 5000

        super.initHeroes()

    }
}