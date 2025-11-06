package tasks.anyue.zhanjiang

import data.HeroBean
import data.HeroCreator
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import tasks.XueLiang
import java.awt.event.KeyEvent

class AYWuZhanHeroDoingSimpleBack3 : BaseSimpleAnYueHeroDoing() {

    val zhanjiang = HeroCreator.zhanjiang.create()
    val tieqi = HeroCreator.tieqi.create()
    val gugu = HeroCreator.gugu.create()
    val sishen = HeroCreator.sishen.create()
    val dijing = HeroCreator.dijing.create()
    val yuren = HeroCreator.yuren.create()
    val tianshi = HeroCreator.tianshi.create()
    val shexian = HeroCreator.shexian.create()

    val guangqiu = HeroCreator.guangqiu.create()

    val tuling = HeroCreator.tuling.create()


    override fun onHeroPointByG19(hero: HeroBean): Boolean {//点战将无敌抗，点两次认输。。。
        return hero != zhanjiang
    }

    override fun initHeroes() {
        heros = arrayListOf(zhanjiang, tieqi, tuling, sishen, gugu, shexian, guangqiu, dijing, tianshi, yuren)
        heros39Up4 = arrayListOf(yuren, tianshi, sishen, gugu)

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
            fulls(zhanjiang, tianshi, tieqi, yuren, sishen, gugu, shexian)
        }, chooseHero = {
            upAny(zhanjiang, gugu, tieqi, tianshi, sishen, yuren, shexian)
        }, onGuanDealStart = {
            carDoing.downHero(dijing)
        }))


        var start = 30
        for (i in start..99) {
            var other: GuanDeal? = when (i) {
                38 -> GuanDeal(39, isOver = { curGuan > 39 },
                    chooseHero = {
                        deal39(this)
                    })

                40 -> GuanDeal(40, isOver = {
                    fulls(zhanjiang, tieqi, yuren, sishen, gugu, shexian)
                }, chooseHero = {
                    upAny(zhanjiang, gugu, tieqi, sishen, yuren, shexian, useGuang = false)
                })

                else -> null
            }

            if (i % 10 == 0) {
                change2Tianshi3(i, other)
            } else if (i % 10 == 8) {
                change2Tianshi4(i, other)
            }
        }


        guanDealList.add(GuanDeal(
            startGuan = 100,
            isOver = {
                fulls(tuling)
            },
            chooseHero = {
                upAny(tuling)
            }, onGuanDealStart = {
                carDoing.downHero(tianshi)
            }
        ))

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

    var qiu69 = false


    private fun change2Tianshi3(guan: Int, otherGuanDeal: GuanDeal? = null) {

        addGuanDeal(guan) {
            over {
                tianshi.currentLevel == 3 && otherGuanDeal?.isOver?.invoke() ?: true
            }
            chooseHero {

                val index = indexOf(tianshi)
                if (index > -1 && tianshi.currentLevel < 3) {
                    upAny(tianshi)
                } else {
                    var otherIndex = otherGuanDeal?.chooseHero?.invoke(this) ?: -1
                    if (otherIndex > -1) {
                        if (get(otherIndex) == guangqiu) {
                            -1
                        } else otherIndex
                    } else otherIndex

                }
            }
            onStart {
                if (tianshi.currentLevel > 3) {
                    carDoing.downHero(tianshi)
                }
                otherGuanDeal?.onGuanDealStart?.invoke()
            }
            onEnd {
                otherGuanDeal?.onGuanDealEnd?.invoke()
            }
        }
    }

    private fun change2Tianshi4(guan: Int, otherGuanDeal: GuanDeal? = null) {

        var selfOver = false
        addGuanDeal(guan) {
            over {
                tianshi.isFull() && otherGuanDeal?.isOver?.invoke() ?: true
            }
            chooseHero {
                val index = indexOf(tianshi)
                if (index > -1 && !tianshi.isFull() && !selfOver) {
                    delay(5000)
                    selfOver = true
                    upAny(tianshi)
                } else {
                    otherGuanDeal?.chooseHero?.invoke(this) ?: upAny(tianshi)
                }
            }
            onStart {
                otherGuanDeal?.onGuanDealStart?.invoke()
            }
            onEnd {
                otherGuanDeal?.onGuanDealEnd?.invoke()
            }
        }
    }


    override suspend fun onHuanQiuPost() {
        if (guankaTask?.currentGuanIndex == 69 || guankaTask?.currentGuanIndex == 68) {
            return
        }
        super.onHuanQiuPost()
    }

    override suspend fun onKeyDown(code: Int): Boolean {

        if (guankaTask?.currentGuanIndex == 69 || guankaTask?.currentGuanIndex == 68
//            || guankaTask?.currentGuanIndex == 49 || guankaTask?.currentGuanIndex == 48
        ) {
            if (code == KeyEvent.VK_NUMPAD0) {
                qiu69 = !qiu69
                return true
            }
        }



        return super.onKeyDown(code)
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