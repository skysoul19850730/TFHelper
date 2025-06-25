package tasks.huodong.shuihu2

import data.HeroBean
import data.HeroCreator
import kotlinx.coroutines.delay
import tasks.SimpleHeZuoHeroDoing

class Day3 : SimpleHeZuoHeroDoing() {
    val shan = HeroCreator.shan.create()
    val mengyan = HeroCreator.mengyan.create()
    val gugu = HeroCreator.gugu.create()
    val kuiqian = HeroCreator.kuiqian.create()
    val xiaoye = HeroCreator.xiaoye.create()
    val dasheng = HeroCreator.dasheng.create()
    val haiyao = HeroCreator.haiyao.create()
    val huanqiu = HeroCreator.huanqiu.create()
    val bingqiu = HeroCreator.bingqiu.create()
    val shexian = HeroCreator.shexian.create()

    override fun initHeroes() {
        heros = arrayListOf(shan, mengyan, gugu, kuiqian, xiaoye, dasheng, haiyao, huanqiu, bingqiu, shexian)

        guanDealList.add(GuanDeal(0, isOver = {
            fulls(shan, mengyan, kuiqian, gugu, xiaoye, haiyao ,shexian) && longxin
        }, chooseHero = {
            if (carDoing.openCount() < 2) {
                upAny(kuiqian, mengyan, gugu,xiaoye, zhuangbei = { longxin })
            } else {
                if (carDoing.openCount() < 4) {
                    var hs = carDoing.carps.filter {
                        it.mHeroBean != null
                    }.map {
                        it.mHeroBean!!
                    }
                    upAny(shan, haiyao, *hs.toTypedArray(), zhuangbei = { longxin })
                } else {
                    upAny(shan, mengyan, gugu, xiaoye, kuiqian,haiyao,shexian, zhuangbei = { longxin })
                }
            }
        }))

        guanDealList.add(GuanDeal(30, isOver = {false}, chooseHero = {
            doBing()
        }))

        guanDealList.add(GuanDeal(47, onlyDoSomething = {
            carDoing.downHero(shan)
            carDoing.downHero(haiyao)
        }))
        guanDealList.add(GuanDeal(50, isOver = {
            fulls(haiyao, shan)
        }, chooseHero = {
            upAny(shan, haiyao)
        }))

        guanDealList.add(GuanDeal(100,isOver = {qiangxi}, chooseHero = {zhuangbei { qiangxi }}))
        guanDealList.add(GuanDeal(110,isOver = {yandou}, chooseHero = {zhuangbei { yandou }}))
        guanDealList.add(GuanDeal(120,isOver = {qiangxi}, chooseHero = {zhuangbei { qiangxi }}))
        guanDealList.add(GuanDeal(130,isOver = {yandou}, chooseHero = {zhuangbei { yandou }}))
        guanDealList.add(GuanDeal(140,isOver = {longxin}, chooseHero = {zhuangbei { longxin }}))
        curGuanDeal = guanDealList.get(0)
    }

    var lastBing = 0L

    private suspend fun List<HeroBean?>.doBing():Int{
        if(indexOf(bingqiu)>-1){
            while(System.currentTimeMillis() - lastBing<2700){
                delay(100)
            }
            lastBing = System.currentTimeMillis()
            return upAny(bingqiu)
        }
        return -1
    }
}