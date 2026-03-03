package tasks.huodong.huangfeihong

import data.HeroCreator

class HFHHero1 : BaseHFH() {

    var shuiling = HeroCreator.shuiling.create()
    var gugu = HeroCreator.gugu.create()
    var huoling = HeroCreator.huoling.create()
    var lvgong = HeroCreator.lvgong.create()
    var xiaoye = HeroCreator.xiaoye.create()
    var fengling = HeroCreator.fengling.create()
    var shexian = HeroCreator.shexian.create()

    var shenv = HeroCreator.shenv.create()
    var guangqiu = HeroCreator.guangqiu.create()
    var moqiu = HeroCreator.moqiu.create()

    override fun initHeroes() {
        heros = arrayListOf(shuiling, gugu, huoling, shenv, lvgong, xiaoye, fengling, guangqiu, moqiu, shexian)
        upHeros = arrayListOf(shuiling, gugu, huoling, lvgong, xiaoye, fengling, shexian)
        qiu = moqiu
        qiuTime = 5000L

        super.initHeroes()

    }
}