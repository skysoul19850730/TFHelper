package tasks.huodong.huangfeihong

import data.HeroCreator

class HFHHero2 : BaseHFH() {//野法皮）

    var feiji = HeroCreator.feiji.create()
    var gugu = HeroCreator.gugu.create()
    var xiongmao = HeroCreator.xiongmao.create()
    var dasheng = HeroCreator.dasheng.create()
    var bingqi = HeroCreator.bingqi.create()
    var daoke = HeroCreator.daoke.create()


    var huofa = HeroCreator.huofa.create()
    var niutou = HeroCreator.niutou.create()
    var shexian = HeroCreator.shexian.create()
    var guangqiu = HeroCreator.guangqiu.create()

    override fun initHeroes() {
        heros = arrayListOf(gugu, shexian, daoke, huofa, feiji, dasheng, bingqi, guangqiu, niutou, xiongmao)
        upHeros = arrayListOf(gugu, dasheng, xiongmao, feiji, daoke, bingqi,shexian)

        super.initHeroes()

    }
}