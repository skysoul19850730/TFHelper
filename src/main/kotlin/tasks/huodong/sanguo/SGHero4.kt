package tasks.huodong.sanguo

import data.HeroBean
import data.HeroCreator

class SGHero4:BaseSanguoHeroDoing() {

    val dianfa = HeroCreator.tieqi.create()
    val hugong = HeroCreator.yuren.create()
    val shenv = HeroCreator.kuangjiang.create()
    val nvyao = HeroCreator.shitou.create()
    val tuling = HeroCreator.tuling.create()
    val feiji = HeroCreator.gugu.create()
    val guangqiu = HeroCreator.guangqiu.create()
    val shexian = HeroCreator.shexian.create()


    val bingqiu = HeroCreator.moqiu.create()
    val houyi = HeroCreator.bingqiu.create()

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
//        mQiu = bingqiu

    }
}