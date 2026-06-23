package tasks.huodong.qiuxiang2

import data.HeroCreator
import java.awt.event.KeyEvent

class QiuXiang2Day1 : BaseQiuxiang2() {
    val dianfa = HeroCreator.dianfa.create()
    val tianshi = HeroCreator.tianshi.create()
    val gugu = HeroCreator.gugu.create()
    val nvyao = HeroCreator.nvyao.create()
    val bingqiu = HeroCreator.bingqiu.create()
    val leishen = HeroCreator.leishen.create()

    val yuren = HeroCreator.yuren.create()

    val moqiu = HeroCreator.moqiu.create()
    val gongjiang = HeroCreator.gongjiang.create()
    val shexian = HeroCreator.shexian.create()


    override fun initHeroes() {
        heros = arrayListOf(dianfa, tianshi, moqiu, gugu, leishen, yuren, nvyao, bingqiu, gongjiang, shexian)

        addGuanDealWithHerosFull(0,listOf(
            tianshi,dianfa,leishen,nvyao,gugu,yuren,shexian
        ))

//        addGuanDealWithHerosFull(0,listOf(
//            tianshi,dianfa,leishen,nvyao,gugu,yuren,shexian
//        ))
//        autoHuanAfter149()

        curGuanDeal = guanDealList.get(0)
    }

}