package tasks.huodong.qiuxiang2

import data.HeroCreator
import java.awt.event.KeyEvent

class QiuXiang2Day6 : BaseQiuxiang2() {
    val zhanjiang = HeroCreator.zhanjiang.create()
    val tieqi = HeroCreator.tieqi.create()
    val xiongmao = HeroCreator.xiongmao.create()
    val fuke = HeroCreator.fuke.create()
    val shengqi = HeroCreator.shengqi.create()
    val yuren = HeroCreator.yuren.create()
    val ganglie = HeroCreator.ganglie.create()
    val shexian = HeroCreator.shexian.create()
    val moqiu = HeroCreator.moqiu.create()
    val xiaoye = HeroCreator.xiaoye.create()


    var moPause = false
    override fun initHeroes() {
        heros = arrayListOf(zhanjiang, xiongmao, yuren, xiaoye, moqiu, ganglie, fuke, shengqi, tieqi, shexian)

        addGuanDealWithHerosFull(0, listOf(zhanjiang, yuren, shengqi, ganglie, shexian, tieqi, xiaoye))

        add99()

        curGuanDeal = guanDealList.get(0)
    }

}