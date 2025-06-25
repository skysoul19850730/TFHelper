package tasks.huodong.shuihu3

import data.HeroBean
import data.HeroCreator
import tasks.SimpleHeZuoHeroDoing

class DayHuoFei:BaseDay() {
    val houzi3 = HeroCreator.dianfa.create()
    val longwang = HeroCreator.huoling.create()
    val yanmo = HeroCreator.gugu.create()
    val huofa = HeroCreator.feiji.create()
    val nvyao = HeroCreator.yanmo.create()
    val guangqiu = HeroCreator.guangqiu.create()
    val maomi = HeroCreator.saman2.create()
    val shexian = HeroCreator.dapao.create()
    val moqiu = HeroCreator.xiaoye.create()

    val aishen = HeroCreator.shenv.create()

    override var downUpHero: HeroBean = shexian

    override var upHeros: ArrayList<HeroBean> = arrayListOf(houzi3,longwang,yanmo,huofa,nvyao,moqiu,shexian)

    override fun initHeroes() {
        heros = arrayListOf(houzi3,longwang,yanmo,huofa,nvyao,guangqiu,maomi, shexian,moqiu,aishen)
        super.initHeroes()
    }
}