package tasks.huodong.dasheng

import data.HeroBean

class DaShengHeroDoingGongjian:BaseDaShengHeroDoing() {

    val gugong = HeroBean("gugong", 100)
    val hugong = HeroBean("hugong", 90)
    val shenv = HeroBean("shenv", 80)
    val aishen = HeroBean("aishen", 70)
    val houyi = HeroBean("houyi", 60, compareRate = 0.95)
    val huofa = HeroBean("huofa", 50, compareRate = 0.95)
    val xiaochou = HeroBean("xiaochou", 50)
    val xiaoye = HeroBean("xiaoye", 40)

    //    val baoku = HeroBean("shexian", 30, needCar = true, isGongCheng = true, compareRate = 0.9)
    val muqiu = HeroBean("muqiu", 20, needCar = false, compareRate = 0.95)
    val guangqiu = HeroBean("guangqiu", 0, needCar = false)


    override fun initHeroes() {
        heros = arrayListOf<HeroBean>().apply {
            add(gugong)
            add(hugong)
            add(shenv)
            add(aishen)
            add(houyi)
            add(huofa)
            add(xiaochou)
            add(xiaoye)
            add(muqiu)
            add(guangqiu)
        }

        upHeros = arrayListOf(hugong, shenv, aishen, huofa, xiaochou, xiaoye, muqiu, guangqiu)
    }
}