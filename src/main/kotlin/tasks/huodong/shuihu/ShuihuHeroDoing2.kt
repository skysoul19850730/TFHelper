package tasks.huodong.shuihu

import data.HeroBean

class ShuihuHeroDoing2 : BaseShuihuHeroDoing() {

    val gugong = HeroBean("gugong")
    val yuren = HeroBean("yuren")
    val shengqi = HeroBean("shengqi")
    val lvgong = HeroBean("lvgong")
    val houyi = HeroBean("houyi")
    val wangjiang2 = HeroBean("wangjiang2")
    val efei = HeroBean("efei")
    val niutou2 = HeroBean("niutou2")

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

        upHeros = arrayListOf(gugong, yuren, lvgong, wangjiang2, efei, niutou2,guangqiu)
        mQiu = hunqiu

    }
}