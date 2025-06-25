package tasks.huodong.shuihu

import data.HeroBean

class ShuihuHeroDoingfeiji : BaseShuihuHeroDoing() {

    val gugong = HeroBean("dianfa")
    val yuren = HeroBean("feiji")
    val lvgong = HeroBean("gugu")
    val houyi = HeroBean("yanmo")
    val wangjiang2 = HeroBean("huofa")
    val efei = HeroBean("bingqi")
    val niutou2 = HeroBean("baoku")

    val shengqi = HeroBean("houzi3")

    val guangqiu = HeroBean("guangqiu",  needCar = false)
    val hunqiu = HeroBean("hunqiu", 0, needCar = false)


    override fun initHeroes() {
        heros = arrayListOf<HeroBean>().apply {
            add(gugong)
            add(yuren)
            add(shengqi)
            add(lvgong)
            add(houyi)
            add(niutou2)
            add(wangjiang2)
            add(guangqiu)
            add(efei)
            add(hunqiu)
        }

        upHeros = arrayListOf(gugong, yuren, lvgong,houyi, wangjiang2, efei, niutou2,guangqiu)
        mQiu = hunqiu

    }
}