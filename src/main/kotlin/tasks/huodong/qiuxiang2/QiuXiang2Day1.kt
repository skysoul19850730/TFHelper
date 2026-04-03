package tasks.huodong.qiuxiang2

import data.HeroCreator
import java.awt.event.KeyEvent

class QiuXiang2Day1 : BaseQiuxiang2() {
    val shan = HeroCreator.shan.create()
    val mengyan = HeroCreator.mengyan.create()
    val gugu = HeroCreator.gugu.create()
    val sishen = HeroCreator.sishen.create()
    val kui = HeroCreator.kui.create()
    val dasheng = HeroCreator.dasheng.create()

    val haiyao = HeroCreator.haiyao.create()

    val nvyao = HeroCreator.nvyao.create()
    val bingqiu = HeroCreator.bingqiu.create()
    val shexian = HeroCreator.shexian.create()


    override fun initHeroes() {
        heros = arrayListOf(shan, mengyan, dasheng, gugu, sishen, kui, nvyao, bingqiu, haiyao, shexian)
        upHeros = arrayListOf(shan, mengyan, kui, gugu,sishen,haiyao,shexian)
        pBingQiu = bingqiu

        guanDealList.add(GuanDeal(
            0,
            isOver = {
                fulls(shan, mengyan, kui, gugu,sishen,haiyao,shexian)
            },
            chooseHero = {
                upAny(shan, mengyan, kui, gugu,sishen,haiyao,shexian)
            }
        ))

//        add49()

        addGuanDealWithHerosFull(50, listOf(shan, mengyan, kui, gugu,sishen,haiyao,shexian))

        gudingShuaQiuTask("bingqiu",80,2200, customOverJudge = {
            !bing88
        }, onGuanDealStart = {bing88 = true})

        add99()

        gudingShuaQiuTask("bingqiu",120,2200, customOverJudge = {
            !bing88
        }, onGuanDealStart = {bing88 = true})

        autoHuanAfter149()

        curGuanDeal = guanDealList.get(0)
    }

    var bing88 = true


    override suspend fun onKeyDown(code: Int): Boolean {
        if(code == KeyEvent.VK_NUMPAD0){
            bing88 = false
        }
        return super.onKeyDown(code)
    }
}