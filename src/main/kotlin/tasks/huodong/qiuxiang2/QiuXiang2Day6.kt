package tasks.huodong.qiuxiang2

import data.HeroCreator
import java.awt.event.KeyEvent

class QiuXiang2Day6 : BaseQiuxiang2() {
    val dianfa = HeroCreator.dianfa.create()
    val hugong = HeroCreator.hugong.create()
    val houyi = HeroCreator.houyi.create()
    val shenv = HeroCreator.shenv.create()
    val xiaopao = HeroCreator.haiyao.create()
    val haiyao = HeroCreator.haiyao.create()
    val gugu = HeroCreator.gugu.create()
    val nvyao = HeroCreator.nvyao.create()
    val moqiu = HeroCreator.moqiu.create()
    val dapao = HeroCreator.dapao.create()


    var moPause = false
    override fun initHeroes() {
        heros = arrayListOf(dianfa, houyi, haiyao, dapao, moqiu, gugu, shenv, xiaopao, hugong, nvyao)

        addGuanDealWithHerosFull(0, listOf(dianfa, haiyao, houyi, gugu, nvyao, hugong, dapao))

        addGuanDealWithHerosFull(50, listOf(dianfa, haiyao, houyi, gugu, nvyao, hugong, dapao))

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