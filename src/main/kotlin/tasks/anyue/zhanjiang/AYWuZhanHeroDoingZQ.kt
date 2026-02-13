package tasks.anyue.zhanjiang

import data.HeroBean
import data.HeroCreator
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import tasks.XueLiang
import tasks.anyue.base.BaseAnYueHeroDoing
import java.awt.event.KeyEvent

class AYWuZhanHeroDoingZQ : BaseAnYueHeroDoing() {

    val zhanjiang = HeroCreator.zhanjiang.create()
    val tieqi = HeroCreator.tieqi.create()
    val gugu = HeroCreator.gugu.create()
    val yuren = HeroCreator.yuren.create()
    val xiaoye = HeroCreator.xiaoye.create()
    val feiting = HeroCreator.feiting.create()

    val guangqiu = HeroCreator.guangqiu.create()
    val dijing = HeroCreator.dijing.create()
    val tianshi = HeroCreator.tianshi.create()

    val bingqiu = HeroCreator.bingqiu.create()


    override fun initHeroes() {
        heros = arrayListOf(zhanjiang, tieqi, guangqiu, tianshi, gugu, feiting, xiaoye, dijing, bingqiu, yuren)

        addGuanDeal(0) {
            over {
                fulls(zhanjiang, gugu, xiaoye, dijing, feiting)
            }
            chooseHero {
                if (zhanjiang.isInCar()) {
                    upAny(zhanjiang, gugu, xiaoye, dijing, feiting)
                } else {
                    upAny(zhanjiang)
                }
            }
        }

        addGuanDealWithHerosFull(27, listOf(zhanjiang, gugu, xiaoye, dijing, feiting, tieqi, yuren))

        addGuanDealWithHerosFull(38, listOf(tianshi), listOf(dijing))

        add39()


        downHero(40,tianshi)
        addGuanDealWithHerosFull(48, listOf(tianshi))



        add49(false)
        downHero(50,tianshi)
        addGuanDealWithHerosFull(59, listOf(tianshi))
        downHero(60,tianshi)
        addGuanDealWithHerosFull(68, listOf(tianshi))


        add69(listOf(bingqiu))

        downHero(70,tianshi)
        add79()
        add89()
        add99()


        addGuanDealWithHerosFull(100, listOf(tianshi))

        add109()

        guanDealList.add(GuanDeal(111, onlyDoSomething = {
            carDoing.downHero(feiting)
        }).apply { des = "下射线" })


        guanDealList.add(GuanDeal(120, isOver = {
            fulls(feiting)
        }, chooseHero = {
            upAny(feiting)
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
                isOver = { feiting.isFull() },
                chooseHero = {
                    upAny(feiting)
                })
        )

        add139()

        curGuanDeal = guanDealList.get(0)
    }

    val g129FirstState: Int //右车 1 (先下射线）  左车0（先不下）
        get() = if (chePosition == 1) 1 else 0

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
                carDoing.downHero(feiting)
                var index = heros.indexOf(feiting)
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