package tasks.huodong.sanguo

import data.HeroBean
import data.HeroCreator

class SGHero3:BaseSanguoHeroDoing() {

    val dianfa = HeroCreator.dianfa.create()
    val hugong = HeroCreator.longwang.create()
    val shenv = HeroCreator.gugu.create()
    val nvyao = HeroCreator.yanmo.create()
    val tuling = HeroCreator.sishen.create()
    val feiji = HeroCreator.xiaoye.create()
    val guangqiu = HeroCreator.guangqiu.create()
    val shexian = HeroCreator.dapao.create()


    val bingqiu = HeroCreator.huanqiu.create()
    val houyi = HeroCreator.zhongkui.create()

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