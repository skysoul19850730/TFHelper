package tasks.huodong.qiuxiang3

import data.HeroCreator

class QiuXiang3Day2:BaseQiuxiang3() {
    override fun initHeroes() {
        heros = arrayListOf(
            HeroCreator.dianfa.create(),
            HeroCreator.gugu.create(),
            HeroCreator.xiongmao.create(),
            HeroCreator.feiji.create(),
            HeroCreator.longwang.create(),
            HeroCreator.yanmo.create(),

            HeroCreator.nvwang.create(),
            HeroCreator.muqiu.create(),
            HeroCreator.guangqiu.create(),
            HeroCreator.youling.create()
         )

        firstUpHeros.addAll(
            heros.take(6)
        )

        super.initHeroes()
    }
}