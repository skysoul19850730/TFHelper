package tasks.huodong.qiuxiang2

import data.HeroCreator

class QiuXiang2Day4 : BaseQiuxiang2() {
    val sishen = HeroCreator.sishen.create()
    val zhanjiang = HeroCreator.zhanjiang.create()
    val saman = HeroCreator.saman.create()
    val niutou = HeroCreator.niutou.create()
    val ganglie = HeroCreator.ganglie.create()
    val xiaoye = HeroCreator.xiaoye.create()
    val fuke = HeroCreator.fuke.create()
    val haiyao = HeroCreator.haiyao.create()
    val bingqiu = HeroCreator.bingqiu.create()
    val nvwang = HeroCreator.nvwang.create()




    override fun initHeroes() {

        addGuanDealWithHerosFull(0,listOf(
            zhanjiang,niutou,sishen,xiaoye,ganglie,haiyao
        ))

        add99()

        curGuanDeal = guanDealList.get(0)
    }


}