package tasks.huodong.shuihu3

import data.HeroBean
import data.HeroCreator
import tasks.SimpleHeZuoHeroDoing

class Day12:BaseDay() {
    val houzi3 = HeroCreator.tieqi.create()
    val longwang = HeroCreator.yuren.create()
    val yanmo = HeroCreator.kuangjiang.create()
    val huofa = HeroCreator.shitou.create()
    val nvyao = HeroCreator.gugu.create()
    val guangqiu = HeroCreator.guangqiu.create()
    val maomi = HeroCreator.maomi.create()
    val shexian = HeroCreator.shexian.create()
    val moqiu = HeroCreator.moqiu.create()

    val aishen = HeroCreator.muqiu.create()

    override var downUpHero: HeroBean = yanmo

    override var upHeros: ArrayList<HeroBean> = arrayListOf(houzi3,longwang,yanmo,huofa,nvyao,maomi,shexian)

    override fun initHeroes() {
        heros = arrayListOf(houzi3,longwang,yanmo,huofa,nvyao,guangqiu,maomi, shexian,moqiu,aishen)
        super.initHeroes()
    }
}