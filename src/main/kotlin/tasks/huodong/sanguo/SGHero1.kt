package tasks.huodong.sanguo

import data.HeroBean
import data.HeroCreator

class SGHero1:BaseSanguoHeroDoing() {

    val dianfa = HeroCreator.dianfa.create()
    val hugong = HeroCreator.hugong.create()
    val shenv = HeroCreator.shenv.create()
    val houyi = HeroCreator.houyi.create()
    val nvyao = HeroCreator.nvyao.create()
    val tuling = HeroCreator.tuling.create()
    val guangqiu = HeroCreator.guangqiu.create()
    val bingqiu = HeroCreator.bingqiu.create()
    val feiji = HeroCreator.feiji.create()
    val shexian = HeroCreator.shexian.create()

    override fun initHeroes() {

        heros = arrayListOf<HeroBean>().apply {
            add(dianfa)
            add(hugong)
            add(shenv)
            add(houyi)
            add(nvyao)
            add(tuling)
            add(guangqiu)
            add(bingqiu)
            add(feiji)
            add(shexian)
        }
        upHeros = arrayListOf(dianfa,hugong,shenv,nvyao,tuling,feiji,shexian,guangqiu)
        mQiu = bingqiu

    }
}