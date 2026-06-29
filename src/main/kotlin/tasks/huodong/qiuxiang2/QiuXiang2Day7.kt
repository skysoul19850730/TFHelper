package tasks.huodong.qiuxiang2

import data.HeroCreator
import java.awt.event.KeyEvent

class QiuXiang2Day7 : BaseQiuxiang2() {
    val houzi = HeroCreator.houzi.create()
    val longwang = HeroCreator.longwang.create()
    val sishen = HeroCreator.sishen.create()
    val xiaoye = HeroCreator.xiaoye.create()
    val gugu = HeroCreator.gugu.create()
    val ganglie = HeroCreator.ganglie.create()
    val zhongkui = HeroCreator.zhongkui.create()
    val bingqiu = HeroCreator.bingqiu.create()
    val moqiu = HeroCreator.moqiu.create()
    val dapao = HeroCreator.dapao.create()


    var moPause = false
    override fun initHeroes() {
        heros = arrayListOf(houzi, sishen, bingqiu, dapao, moqiu, ganglie, gugu, zhongkui, longwang, xiaoye)

        addGuanDealWithHerosFull(0,listOf(houzi,sishen,dapao,ganglie,gugu,xiaoye,longwang))

        addGuanDealWithHerosFull(90,listOf(zhongkui),listOf(ganglie))

        add99()

        addGuanDealWithHerosFull(100,listOf(ganglie) ,listOf(zhongkui))

        addGuanDealWithHerosFull(140,listOf(zhongkui),listOf(ganglie))
        addGuanDealWithHerosFull(150,listOf(ganglie),listOf(zhongkui))

        curGuanDeal = guanDealList.get(0)
    }


}