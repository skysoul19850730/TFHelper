package tasks.huodong.qiuxiang2

import data.HeroCreator
import java.awt.event.KeyEvent

class QiuXiang2Day5 : BaseQiuxiang2() {
    val houzi = HeroCreator.houzi.create()
    val longwang = HeroCreator.longwang.create()
    val sishen = HeroCreator.sishen.create()
    val xiaoye = HeroCreator.xiaoye.create()
    val zhongkui = HeroCreator.zhongkui.create()
    val yanmo = HeroCreator.yanmo.create()
    val gugu = HeroCreator.gugu.create()
    val ganglie = HeroCreator.ganglie.create()
    val moqiu = HeroCreator.moqiu.create()
    val dapao = HeroCreator.dapao.create()


    var moPause = false
    override fun initHeroes() {
        heros = arrayListOf(houzi, sishen, yanmo, dapao, moqiu, gugu, xiaoye, zhongkui, longwang, ganglie)

        addGuanDealWithHerosFull(0, listOf(houzi, xiaoye, sishen, gugu, ganglie, longwang, dapao))

        addGuanDealWithHerosFull(50, listOf(houzi, xiaoye, sishen, gugu, ganglie, longwang, dapao))

        gudingShuaQiuTask("moqiu", 58, 5500, sholudPasue = {
            moPause
        })


        curGuanDeal = guanDealList.get(0)
    }

    override suspend fun onKeyDown(code: Int): Boolean {
        val sr = super.onKeyDown(code)

        if(code == KeyEvent.VK_NUMPAD0 && !sr){
            moPause = !moPause
        }

        return sr
    }

}