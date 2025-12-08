package tasks.huodong.huangfeihong

import data.HeroCreator

class HFHHero7 : BaseHFH() {//野法皮）

    var dianfa = HeroCreator.dianfa.create()
    var gugu = HeroCreator.gugu.create()
    var huofa = HeroCreator.huofa.create()
    var sishen = HeroCreator.sishen.create()
    var bingqi = HeroCreator.bingqi.create()
    var niutou = HeroCreator.niutou.create()


    var longwang = HeroCreator.longwang.create()
    var dapao = HeroCreator.dapao.create()
    var guangqiu = HeroCreator.guangqiu.create()
    var moqiu = HeroCreator.moqiu.create()

    override fun initHeroes() {
        heros = arrayListOf(gugu, dapao, longwang, huofa, dianfa, sishen, bingqi, guangqiu, niutou, moqiu)
        upHeros = arrayListOf(gugu, niutou, huofa, dianfa, sishen, bingqi,dapao)
        qiu = moqiu
        qiuTime = 5000L

        super.initHeroes()

    }
}