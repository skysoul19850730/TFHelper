package tasks.huodong.qiuxiang2

import data.HeroCreator
import java.awt.event.KeyEvent

class QiuXiang2Day5 : BaseQiuxiang2() {
    val dianfa = HeroCreator.dianfa.create()
    val huoling = HeroCreator.huoling.create()
    val sishen = HeroCreator.sishen.create()
    val niutou = HeroCreator.niutou.create()
    val daoke = HeroCreator.daoke.create()
    val haiyao = HeroCreator.haiyao.create()
    val gugu = HeroCreator.gugu.create()
    val ganglie = HeroCreator.ganglie.create()
    val nvyao = HeroCreator.nvyao.create()
    val bingqiu = HeroCreator.bingqiu.create()


    override fun initHeroes() {
        heros = arrayListOf(dianfa, sishen, haiyao, bingqiu, nvyao, gugu, niutou, daoke, huoling, ganglie)


        addGuanDealWithHerosFull(0, listOf(huoling,niutou,gugu,daoke,ganglie,dianfa))

        add99()


        curGuanDeal = guanDealList.get(0)
    }


}