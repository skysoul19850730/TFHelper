package tasks.huodong.dasheng

import data.HeroBean

class DaShengHeroDoingBaNv:BaseDaShengHeroDoing() {

    val nvwang = HeroBean("nvwang", 100)
    val bawang = HeroBean("bawang", 90)
    val shahuang = HeroBean("shahuang", 80)
    val niutou2 = HeroBean("niutou2", 70)
    val saman = HeroBean("saman2", 60, compareRate = 0.95)
    val gongjiang = HeroBean("gongjiang", 50)
    val shexian = HeroBean("shexian", 40, isGongCheng = true)

    //    val baoku = HeroBean("shexian", 30, needCar = true, isGongCheng = true, compareRate = 0.9)
    val muqiu = HeroBean("muqiu", 20, needCar = false, compareRate = 0.95)
    val moqiu = HeroBean("moqiu", 20, needCar = false, compareRate = 0.95)
    val guangqiu = HeroBean("guangqiu", 0, needCar = false)


    override fun initHeroes() {
        heros = arrayListOf<HeroBean>().apply {
            add(nvwang)
            add(bawang)
            add(shahuang)
            add(niutou2)
            add(saman)
            add(moqiu)
            add(gongjiang)
            add(shexian)
            add(muqiu)
            add(guangqiu)
        }

        upHeros = arrayListOf(bawang,saman, shahuang, niutou2, nvwang, gongjiang, shexian, muqiu, guangqiu)
    }
}