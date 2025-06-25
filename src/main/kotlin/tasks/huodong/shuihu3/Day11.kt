package tasks.huodong.shuihu3

import data.HeroBean
import data.HeroCreator
import tasks.SimpleHeZuoHeroDoing

class Day11:BaseDay() {
    val houzi3 = HeroCreator.dianfa.create()
    val longwang = HeroCreator.longwang.create()
    val yanmo = HeroCreator.yanmo.create()
    val huofa = HeroCreator.gugu.create()
    val nvyao = HeroCreator.zhongkui.create()
    val guangqiu = HeroCreator.guangqiu.create()
    val maomi = HeroCreator.xiaoye.create()
    val shexian = HeroCreator.dapao.create()
    val moqiu = HeroCreator.moqiu.create()

    val aishen = HeroCreator.youling.create()

    override var downUpHero: HeroBean = shexian

    override var upHeros: ArrayList<HeroBean> = arrayListOf(houzi3,longwang,yanmo,huofa,nvyao,maomi,shexian)

    override fun initHeroes() {
        heros = arrayListOf(houzi3,longwang,yanmo,huofa,nvyao,guangqiu,maomi, shexian,moqiu,aishen)
        super.initHeroes()
    }
}