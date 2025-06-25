package tasks.huodong.shuihu

import data.HeroBean

class ShuihuHeroDoing3 : BaseShuihuHeroDoing() {

    val dianfa = HeroBean("dianfa")
    val huoling = HeroBean("huoling")
    val gugu = HeroBean("gugu")
    val fenghuang = HeroBean("fenghuang")
    val nvyao = HeroBean("nvyao")
    val xiaoye = HeroBean("xiaoye")


    val efei = HeroBean("daoke")
    val niutou2 = HeroBean("shenv")

    val guangqiu = HeroBean("guangqiu",  needCar = false)

    val hunqiu = HeroBean("muqiu", 0, needCar = false)


    override fun initHeroes() {
        heros = arrayListOf<HeroBean>().apply {
            add(dianfa)
            add(huoling)
            add(gugu)
            add(fenghuang)
            add(nvyao)
            add(niutou2)
            add(xiaoye)
            add(guangqiu)
            add(efei)
            add(hunqiu)
        }

        upHeros = arrayListOf(dianfa, huoling, fenghuang, xiaoye, nvyao, gugu,guangqiu)
        mQiu = hunqiu

    }
}