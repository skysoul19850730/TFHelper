package tasks.huodong.huangfeihong

import data.HeroCreator

class HFHHero4 : BaseHFH() {//野法皮）

    var gugu = HeroCreator.gugu.create()
    var dianfa = HeroCreator.dianfa.create()
    var yuren = HeroCreator.yuren.create()
    var xiongmao = HeroCreator.xiongmao.create()
    var bingqi = HeroCreator.bingqi.create()
    var niutou = HeroCreator.niutou.create()


    var wangjiang = HeroCreator.wangjiang.create()
    var dapao = HeroCreator.dapao.create()
    var guangqiu = HeroCreator.guangqiu.create()
    var moqiu = HeroCreator.moqiu.create()

    override fun initHeroes() {
        heros = arrayListOf(dianfa, dapao, wangjiang, yuren, gugu, xiongmao, bingqi, guangqiu, niutou, moqiu)
        upHeros = arrayListOf(dianfa, niutou, yuren, gugu, xiongmao, bingqi)
        qiu = moqiu
        qiuTime = 5000L

        super.initHeroes()

    }
}