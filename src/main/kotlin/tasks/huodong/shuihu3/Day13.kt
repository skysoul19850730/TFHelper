package tasks.huodong.shuihu3

import data.HeroBean
import data.HeroCreator

class Day13:BaseDay() {
    val houzi3 = HeroCreator.nvwang.create()
    val longwang = HeroCreator.yuren.create()
    val yanmo = HeroCreator.feiji.create()
    val huofa = HeroCreator.saman.create()
    val nvyao = HeroCreator.shahuang.create()
    val guangqiu = HeroCreator.guangqiu.create()
    val maomi = HeroCreator.maomi.create()
    val shexian = HeroCreator.shexian.create()
    val moqiu = HeroCreator.moqiu.create()

    val aishen = HeroCreator.muqiu.create()

    override var downUpHero: HeroBean = houzi3

    override var upHeros: ArrayList<HeroBean> = arrayListOf(houzi3,longwang,yanmo,huofa,nvyao,maomi,shexian)

    override fun initHeroes() {
        heros = arrayListOf(houzi3,longwang,yanmo,huofa,nvyao,guangqiu,maomi, shexian,moqiu,aishen)
        super.initHeroes()
    }
}