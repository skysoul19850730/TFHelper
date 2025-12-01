package tasks.anyue.zhanjiang

import data.HeroBean
import data.HeroCreator
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import tasks.XueLiang
import tasks.anyue.base.BaseAnYueHeroDoing

class AYWuZhanHeroDoingSimpleBack4 : BaseAnYueHeroDoing() {

    val zhanjiang = HeroCreator.zhanjiang.create()
    val tieqi = HeroCreator.tieqi.create()
    val gugu = HeroCreator.gugu.create()
    val sishen = HeroCreator.sishen.create()
    val dijing = HeroCreator.dijing.create()
    val yuren = HeroCreator.yuren.create()
    val bingqiu = HeroCreator.bingqiu.create()
    val shexian = HeroCreator.shexian.create()

    val guangqiu = HeroCreator.guangqiu.create()

    val tuling = HeroCreator.tuling.create()


    override fun initHeroes() {
        heros = arrayListOf(zhanjiang, tieqi, tuling, sishen, gugu, shexian, guangqiu, dijing, bingqiu, yuren)

        guanDealList.add(GuanDeal(0, isOver = {
            zhanjiang.currentLevel == 3
        }, chooseHero = {
            if (!zhanjiang.isInCar()) {
                upAny(zhanjiang)
            } else if (!tieqi.isInCar()) {
                upAny(tieqi)
            } else {
                upAny(zhanjiang, tieqi, gugu, sishen, shexian)
            }
        }))

        addGuanDeal(17) {
            over {
                fulls(zhanjiang, tieqi, gugu, sishen, yuren, dijing, shexian)
            }
            chooseHero {
                upAny(zhanjiang, tieqi, gugu, sishen, yuren, dijing, shexian)
            }
        }

        guanDealList.add(GuanDeal(27, isOver = {
            fulls(zhanjiang, tuling, tieqi, yuren, sishen, gugu, shexian)
        }, chooseHero = {
            upAny(zhanjiang, gugu, tieqi, tuling, sishen, yuren, shexian)
        }, onGuanDealStart = {
            carDoing.downHero(dijing)
        }))

        add39()

        //add49 后面要识别49球了
//        addGuanDeal(49){
//            over { curGuan>49 }
//            chooseHero{
//                while(curGuan==49){
//                    delay(1000)
//                }
//                -1
//            }
//            onStart {
//                App.startAutoSave()
//            }
//            onEnd {
//                App.stopAutoSave()
//            }
//        }

        add69(listOf(bingqiu))
        add79()
        add89()
        add99()

        guanDealList.add(GuanDeal(111, onlyDoSomething = {
            carDoing.downHero(shexian)
        }).apply { des = "下射线" })


        guanDealList.add(GuanDeal(120, isOver = {
            fulls(shexian)
        }, chooseHero = {
            upAny(shexian)
        }
        ))

        guanDealList.add(GuanDeal(129, isOver = { curGuan > 129 }, chooseHero = { g129Index(this) }, onGuanDealStart = {
            g129State = g129FirstState
            GlobalScope.launch {
                check129Xue()
            }
        })
            .apply { des = "按0下射线，再按0上射线" })


        guanDealList.add(
            GuanDeal(
                130,
                isOver = { shexian.isFull() },
                chooseHero = {
                    upAny(shexian)
                })
        )

        curGuanDeal = guanDealList.get(0)
    }

    val g129FirstState:Int //右车 1 (先下射线）  左车0（先不下）
        get() = if(chePosition==1) 1 else 0

    var g129State = 0//0等待,1 下宝库 备宝库，2上宝库 //回到了初始态，等1再下宝库循环


    var g129XueCount = 1//0,1 下射线，2，3上射线

    suspend fun check129Xue() {

        while (curGuan <= 129) {
            XueLiang.observerXueDown(xueRate = 0.6f, over = { curGuan > 129 })
            g129XueCount++
            if (g129XueCount == 2) {
                delay(1500)
                g129XueCount = 0
                g129State = if (g129State == 0) 1 else 0
            }
            delay(2000)
        }

    }

    suspend fun g129Index(heros: List<HeroBean?>): Int {


        if (curGuan > 129) return -1

        while (g129State == 0) {
            delay(100)
            if (curGuan > 129) return -1
        }
        when (g129State) {
            1 -> {
                delay(500)
                carDoing.downHero(shexian)
                var index = heros.indexOf(shexian)
                if (index > -1) {
                    while (g129State == 1) {
                        delay(100)
                        if (curGuan > 129) return -1
                    }
                    return index
                }
                return -1
            }

        }
        return -1
    }
}