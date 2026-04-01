package tasks.huodong.qiuxiang2

import data.HeroCreator
import java.awt.event.KeyEvent

class QiuXiang2Day7 : BaseQiuxiang2() {
    val wuyi = HeroCreator.zhanjiang.create()
    val tianshi = HeroCreator.tianshi.create()
    val sishen = HeroCreator.sishen.create()
    val jiaonv = HeroCreator.jiaonv.create()
    val shengqi = HeroCreator.shengqi.create()
    val dijing = HeroCreator.dijing.create()
    val ganglie = HeroCreator.ganglie.create()
    val xiaoye = HeroCreator.xiaoye.create()
    val huanqiu = HeroCreator.huanqiu.create()
    val dapao = HeroCreator.dapao.create()


    var moPause = false
    override fun initHeroes() {
        heros = arrayListOf(wuyi, sishen, dijing, dapao, huanqiu, ganglie, jiaonv, shengqi, tianshi, xiaoye)

        addGuanDealWithHerosFull(0, listOf(tianshi, jiaonv, sishen, ganglie, xiaoye, shengqi, dapao))

        addGuanDealWithHerosFull(50, listOf(tianshi, jiaonv, sishen, ganglie, xiaoye, shengqi, dapao))

        changeZhuangbei(60, {qiangxi})


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