package tasks.anyue.zhanjiang

import data.HeroBean
import data.HeroCreator
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import tasks.XueLiang
import tasks.anyue.base.BaseAnYueHeroDoing

class AYWuZhanHeroDoingZQ2 : BaseAnYueHeroDoing() {

    val zhanjiang = HeroCreator.zhanjiang.create()
    val tieqi = HeroCreator.tieqi.create()
    val gugu = HeroCreator.gugu.create()
    val guangqiu = HeroCreator.guangqiu.create()
    val jiaonv = HeroCreator.jiaonv.create()
    val feiting = HeroCreator.feiting.create()

    val tuling = HeroCreator.tuling.create()
    val dijing = HeroCreator.dijing.create()
    val tianshi = HeroCreator.tianshi.create()

    val bingqiu = HeroCreator.bingqiu.create()


    override fun initHeroes() {
        heros = arrayListOf(zhanjiang, tieqi, tuling, tianshi, gugu, feiting, jiaonv, dijing, bingqiu, guangqiu)

        addGuanDeal(0) {
            over {
                fulls(zhanjiang, gugu, jiaonv, dijing, feiting)
            }
            chooseHero {
                if (zhanjiang.isInCar()) {
                    upAny(zhanjiang, gugu, jiaonv, dijing, feiting)
                } else {
                    upAny(zhanjiang)
                }
            }
        }

        addGuanDealWithHerosFull(27, listOf(zhanjiang, gugu, jiaonv, dijing, feiting, tieqi))

        addGuanDealWithHerosFull(38, listOf(tianshi))

        add39()

        addGuanDealWithHerosFull(40, listOf(tuling), listOf(tianshi))

        add49(false)


        add69(listOf(bingqiu))
        add79()
        add89()
        add99()


        addGuanDealWithHerosFull(100, listOf(tianshi), listOf(dijing))

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
            .apply { des = "按0下射线，再按0上射线，改为脚本方上下俩卡，另一方下一个就不用再操作上下卡了（可以刷魔）" })


        guanDealList.add(
            GuanDeal(
                130,
                isOver = { fulls(feiting,jiaonv) },
                chooseHero = {
                    upAny(feiting,jiaonv)
                })
        )

//        add139()
        add139(false)

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
        //目前的卡组下了娇女，副卡要刷魔，而且副卡没有射线，会卡魔，等后面卡组更厉害了，再考虑脚本下俩，否则现在比之前还难刷
//        when (g129State) {
//            1 -> {
//                delay(500)
//                carDoing.downHero(feiting)
//                carDoing.downHero(jiaonv)
//                var index = heros.upAny(jiaonv,feiting)
//                if (index > -1) {
//                    while (g129State == 1) {
//                        delay(100)
//                        if (curGuan > 129) return -1
//                    }
//                    return index
//                }
//                return -1
//            }
//            0 ->{
//                if(jiaonv.isInCar() && feiting.isInCar()){
//                    while (g129State == 0) {
//                        delay(100)
//                        if (curGuan > 129) return -1
//                    }
//                    //这里等state 变回1后，直接递归本方法，去执行 =1 的分支，即下卡
//                    return g129Index(heros)
//                }else{
//                    //这里也上另一个，这样如果运气差，等另一个满了，也就上去了
//                    if(jiaonv.isInCar()){
//                        return heros.upAny(feiting,jiaonv)
//                    }else{
//                        return heros.upAny(jiaonv,feiting)
//                    }
//                }
//            }
//
//        }
        return -1
    }
}