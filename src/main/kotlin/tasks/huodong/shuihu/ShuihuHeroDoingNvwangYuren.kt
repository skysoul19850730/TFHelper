package tasks.huodong.shuihu

import data.HeroBean

class ShuihuHeroDoingNvwangYuren : BaseShuihuHeroDoing() {

    val gugong = HeroBean("nvwang")
    val yuren = HeroBean("yuren")
    val lvgong = HeroBean("feiji")
    val houyi = HeroBean("niutou2")
    val wangjiang2 = HeroBean("fuke")
    val efei = HeroBean("shahuang")
    val niutou2 = HeroBean("baoku", needCar = true, isGongCheng = true)

    val shengqi = HeroBean("daoke")

    val guangqiu = HeroBean("guangqiu",  needCar = false)
    val hunqiu = HeroBean("muqiu", 0, needCar = false)


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