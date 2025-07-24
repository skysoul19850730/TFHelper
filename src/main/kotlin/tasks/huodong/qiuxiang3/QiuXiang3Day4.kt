package tasks.huodong.qiuxiang3

import data.HeroCreator

class QiuXiang3Day4:BaseQiuxiang3() {
    override fun initHeroes() {
        heros = arrayListOf(
            HeroCreator.gugong.create(),
            HeroCreator.houyi.create(),
            HeroCreator.feiji.create(),
            HeroCreator.nvyao.create(),
            HeroCreator.yuren.create(),

            HeroCreator.yanmo.create(),

            HeroCreator.guangqiu.create(),
            HeroCreator.moqiu.create(),
            HeroCreator.lvgong.create(),
            HeroCreator.hugong.create()
         )

        firstUpHeros.addAll(
            heros.take(6)
        )
//            upDownHeros.add(heros.get(5))

        super.initHeroes()
    }
}