package tasks.huodong.huangfeihong

import data.HeroCreator

class HFHHero4 : BaseHFH() {//野法皮）

    var dianfa = HeroCreator.dianfa.create()
    var tianshi = HeroCreator.tianshi.create()
    var yanmo = HeroCreator.yanmo.create()
    var sishen = HeroCreator.sishen.create()
    var jiaonv = HeroCreator.jiaonv.create()
    var xiaoye = HeroCreator.xiaoye.create()


    var maomi = HeroCreator.maomi.create()
    var shengqi = HeroCreator.shengqi.create()
    var guangqiu = HeroCreator.guangqiu.create()
    var muqiu = HeroCreator.muqiu.create()

    override fun initHeroes() {
        heros = arrayListOf(tianshi, shengqi, maomi, yanmo, dianfa, sishen, jiaonv, guangqiu, xiaoye, muqiu)
        upHeros = arrayListOf(tianshi, xiaoye, yanmo, dianfa, sishen, jiaonv)
        qiu = muqiu
        qiuTime = 2000L

        super.initHeroes()

    }
}