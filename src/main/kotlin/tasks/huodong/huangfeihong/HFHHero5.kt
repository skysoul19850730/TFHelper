package tasks.huodong.huangfeihong

import data.HeroCreator

class HFHHero5 : BaseHFH() {//野法皮）

    var huofa = HeroCreator.huofa.create()
    var daoke = HeroCreator.daoke.create()
    var yanmo = HeroCreator.yanmo.create()
    
    var gugu = HeroCreator.gugu.create()
    var bingqi = HeroCreator.bingqi.create()
    var feiji = HeroCreator.feiji.create()


    var dasheng = HeroCreator.dasheng.create()
    var xiongmao = HeroCreator.xiongmao.create()
    var guangqiu = HeroCreator.guangqiu.create()
    var moqiu = HeroCreator.moqiu.create()

    override fun initHeroes() {
        heros = arrayListOf(daoke, xiongmao, dasheng, yanmo, huofa, gugu, bingqi, guangqiu, feiji, moqiu)
        upHeros = arrayListOf(daoke, feiji, yanmo, huofa, gugu, bingqi,)
        qiu = moqiu
        qiuTime = 5500

        super.initHeroes()

    }
}