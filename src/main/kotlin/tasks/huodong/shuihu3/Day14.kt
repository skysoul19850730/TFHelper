package tasks.huodong.shuihu3

import data.HeroBean
import data.HeroCreator

class Day14:BaseDay() {
    val houzi3 = HeroCreator.dianfa.create()
    val longwang = HeroCreator.feiji.create()
    val nvyao = HeroCreator.daoke.create()
    val huofa = HeroCreator.gugu.create()
    val yanmo = HeroCreator.yanmo.create()
    val guangqiu = HeroCreator.guangqiu.create()
    val maomi = HeroCreator.maomi.create()
    val shexian = HeroCreator.shexian.create()
    val moqiu = HeroCreator.moqiu.create()

    val aishen = HeroCreator.huofa.create()

    override var downUpHero: HeroBean = yanmo

    override var upHeros: ArrayList<HeroBean> = arrayListOf(houzi3,longwang,yanmo,huofa,nvyao,maomi,shexian)

    override fun initHeroes() {
        heros = arrayListOf(houzi3,longwang,yanmo,huofa,nvyao,guangqiu,maomi, shexian,moqiu,aishen)
        super.initHeroes()
    }
}