package tasks.huodong.shuihu

import data.HeroBean

/**
 * 只能普通
 */
class ShuihuHeroDoing1 : BaseShuihuHeroDoing() {

    val gugong = HeroBean("gugong")
    val hugong = HeroBean("hugong")
    val shenv = HeroBean("shenv")
    val gugu = HeroBean("gugu")
    val houyi = HeroBean("houyi")
    val leishen = HeroBean("leishen")
    val bingnv = HeroBean("bingnv")
    val xiaoye = HeroBean("xiaoye")

    val shexian = HeroBean("shexian", isGongCheng = true)

    val muqiu = HeroBean("muqiu", 0, needCar = false)


    override fun initHeroes() {
        heros = arrayListOf<HeroBean>().apply {
            add(gugong)
            add(hugong)
            add(shenv)
            add(gugu)
            add(houyi)
            add(xiaoye)
            add(leishen)
            add(shexian)
            add(bingnv)
            add(muqiu)
        }

        upHeros = arrayListOf(gugong, hugong, gugu, leishen, shexian, bingnv, xiaoye)
        mQiu = muqiu

    }
}