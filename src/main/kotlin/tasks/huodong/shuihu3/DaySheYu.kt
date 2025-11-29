package tasks.huodong.shuihu3

import data.HeroBean
import data.HeroCreator

class DaySheYu:BaseDay() {
    val houzi3 = HeroCreator.dianfa.create()
    val longwang = HeroCreator.yuren.create()
    val yanmo = HeroCreator.hugong.create()
    val huofa = HeroCreator.houyi.create()
    val nvyao = HeroCreator.nvyao.create()
    val guangqiu = HeroCreator.guangqiu.create()
    val maomi = HeroCreator.maomi.create()
    val shexian = HeroCreator.shexian.create()
    val moqiu = HeroCreator.moqiu.create()

    val aishen = HeroCreator.shenv.create()

    override var downUpHero: HeroBean = huofa

    override var upHeros: ArrayList<HeroBean> = arrayListOf(houzi3,longwang,yanmo,huofa,nvyao,maomi,shexian)

    override fun initHeroes() {
        heros = arrayListOf(houzi3,longwang,yanmo,huofa,nvyao,guangqiu,maomi, shexian,moqiu,aishen)
        super.initHeroes()
    }
}