package tasks.huodong.qiuxiang2

import data.HeroCreator

class QiuXiang2Day2 : BaseQiuxiang2() {
    val dianfa = HeroCreator.dianfa.create()
    val gugu = HeroCreator.gugu.create()
    val hugong = HeroCreator.hugong.create()
    val houyi = HeroCreator.houyi.create()
    val shenv = HeroCreator.shenv.create()
    val niutou = HeroCreator.niutou.create()
    val ganglie = HeroCreator.ganglie.create()
    val moqiu = HeroCreator.moqiu.create()
    val xiaopao = HeroCreator.zhanjiang.create()
    val shexian = HeroCreator.shexian.create()




    override fun initHeroes() {
        heros = arrayListOf(dianfa, hugong, niutou, shexian, xiaopao, ganglie, houyi, shenv, gugu, moqiu)

        addGuanDealWithHerosFull(0,listOf(
            hugong,dianfa,houyi,niutou,gugu,ganglie,shexian
        ))


        curGuanDeal = guanDealList.get(0)
    }


}