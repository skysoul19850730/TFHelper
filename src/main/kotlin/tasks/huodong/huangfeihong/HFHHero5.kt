package tasks.huodong.huangfeihong

import data.HeroCreator

class HFHHero5 : BaseHFH() {//野法皮）

    var xiongmao = HeroCreator.xiongmao.create()
    var daoke = HeroCreator.daoke.create()
    var huoling = HeroCreator.huoling.create()
    
    var sishen = HeroCreator.sishen.create()
    var bingqi = HeroCreator.bingqi.create()
    var niutou = HeroCreator.niutou.create()


    var hunqiu = HeroCreator.hunqiu.create()
    var shexian = HeroCreator.shexian.create()
    var guangqiu = HeroCreator.guangqiu.create()
    var moqiu = HeroCreator.moqiu.create()

    override fun initHeroes() {
        heros = arrayListOf(daoke, shexian, hunqiu, huoling, xiongmao, sishen, bingqi, guangqiu, niutou, moqiu)
        upHeros = arrayListOf(daoke, niutou, huoling, xiongmao, sishen, bingqi,shexian)
        qiu = hunqiu
        qiuTime = 2000L

        super.initHeroes()

    }
}