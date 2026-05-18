package tasks.huodong.qiuxiang2

import data.HeroCreator

class QiuXiang2Day3 : BaseQiuxiang2() {
    val dianfa = HeroCreator.dianfa.create()
    val huoling = HeroCreator.huoling.create()
    val gugu = HeroCreator.gugu.create()
    val xiongmao = HeroCreator.xiongmao.create()
    val gugong = HeroCreator.gugong.create()
    val ganglie = HeroCreator.ganglie.create()
    val sishen = HeroCreator.sishen.create()
    val huanqiu = HeroCreator.huanqiu.create()
    val muqiu = HeroCreator.muqiu.create()
    val wawa = HeroCreator.wawa.create()




    override fun initHeroes() {
        heros = arrayListOf(dianfa, gugu, ganglie, wawa, muqiu, sishen, xiongmao, gugong, huoling, huanqiu)


        addGuanDealWithHerosFull(0, listOf(huoling,dianfa,gugu,xiongmao,gugong,ganglie,wawa)
        , zhuangbei = {qiangxi})

        addGuanDealWithHerosFull(50, listOf(huoling,dianfa,gugu,xiongmao,gugong,ganglie,wawa)
            , zhuangbei = {qiangxi})

        addGuanDealWithHerosFull(140, listOf(sishen), listOf(ganglie))

        curGuanDeal = guanDealList.get(0)
    }


}