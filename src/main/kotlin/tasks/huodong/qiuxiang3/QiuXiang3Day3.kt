package tasks.huodong.qiuxiang3

import data.HeroCreator

class QiuXiang3Day3:BaseQiuxiang3() {
    override fun initHeroes() {
        heros = arrayListOf(
            HeroCreator.shuiling.create(),
            HeroCreator.gugu.create(),
            HeroCreator.huoling.create(),
            HeroCreator.saman.create(),
            HeroCreator.huofa.create(),

            HeroCreator.feiji.create(),

            HeroCreator.guangqiu.create(),
            HeroCreator.moqiu.create(),
            HeroCreator.tuling.create(),
            HeroCreator.muqiu.create()
         )

        firstUpHeros.addAll(
            heros.take(5)
        )
            upDownHeros.add(heros.get(5))

        super.initHeroes()
    }
}