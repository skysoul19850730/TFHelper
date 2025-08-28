package tasks.hanbing2.zhanjiang.fuka

import data.HeroBean
import data.HeroCreator
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import log
import tasks.XueLiang
import tasks.hanbing2.BaseSimpleHBHeroDoing
import ui.zhandou.hanbing.HanBingModel
import java.awt.event.KeyEvent

class HB5ZGGHeroDoingZiQiangFuka : BaseSimpleHBHeroDoing() {

    val isRenwu = true

    //xiongmao,xiaoye,saman,shexian,lvgong,zhanjiang,nvyao,niutou,kuangjiang,guangqiu
    val zhanjiang = HeroCreator.zhangjiangy.create()
    val xiongmao = HeroCreator.xiongmao2.create()
    val xiaoye = HeroCreator.xiaoye.create()
    val saman = HeroCreator.saman2.create()
    val lvgong = HeroCreator.lvgong.create()
    val nvyao = HeroCreator.nvyao.create()
    val niutou = HeroCreator.niutou2.create()
    val kuangjiang = HeroCreator.kuangjiang.create()
    val shexian = HeroCreator.shexian.create()

    val guangqiu = HeroCreator.yuren.create()
//        HeroBean(if (isRenwu) HanBingModel.renwuKa.value else "guangqiu", 40, needCar = false, compareRate = 0.95)


    override fun initHeroes() {
        super.initHeroes()
        heros = arrayListOf(zhanjiang, xiongmao, xiaoye, saman, lvgong, nvyao, niutou, kuangjiang, shexian, guangqiu)
        heroBean149 = kuangjiang

        guanDealList.add(GuanDeal(0, isOver = {
            fulls(xiongmao, saman, xiaoye, shexian, lvgong) && kuangjiang.isInCar() && zhanjiang.isInCar()
        }, chooseHero = {
            if (zhanjiang.isInCar() && kuangjiang.isInCar()) {
                upAny(xiongmao, saman, shexian, lvgong, xiaoye)
            } else if (zhanjiang.isInCar()) {
                upAny(xiongmao, saman, shexian, lvgong, xiaoye, kuangjiang)
            } else if (kuangjiang.isInCar()) {
                upAny(xiongmao, saman, shexian, lvgong, xiaoye, zhanjiang)
            } else upAny(xiongmao, saman, shexian, lvgong, xiaoye, kuangjiang, zhanjiang)
        }))

        guanDealList.add(GuanDeal(
            41,
            isOver = {
                fulls(kuangjiang)
            },
            chooseHero = {
                upAny(kuangjiang)
            }
        ))

        guanDealList.add(GuanDeal(99, isOver = {
            zhanjiang.isFull()
        }, chooseHero = {
            upAny(zhanjiang)
        }))


        guanDealList.add(
            GuanDeal(109, isOver = { false },
                chooseHero = {
                    delay(300)
                    val ind = upAny(xiongmao, saman, shexian, lvgong, xiaoye, kuangjiang, zhanjiang)
                    if (ind < 0 && !isRenwu) {
                        upAny(guangqiu)
                    } else ind
                })
        )

        guanDealList.add(GuanDeal(111, isOver = {
            fulls(xiongmao, saman, shexian, lvgong, xiaoye, kuangjiang, zhanjiang)
        }, chooseHero = {
            if (needReCheckStar) {
                carDoing.reCheckStars()
                needReCheckStar = false
            }
            upAny(xiongmao, saman, shexian, lvgong, xiaoye, kuangjiang, zhanjiang)
        }, onGuanDealStart = {
            needReCheckStar = true
        }))

        guanDealList.add(
            GuanDeal(129, onlyDoSomething = {
                startChuanZhangOberserver2()
            })
        )
        guanDealList.add(
            GuanDeal(130, onlyDoSomething = {
                stopChuanZhangOberserver()
            })
        )

        guanDealList.add(GuanDeal(149, isOver = {
            currentGuan() > 149
        }, chooseHero = {
            deal149(this)
        }, onGuanDealStart = {
            startLeishenOberserver()
        }, onGuanDealEnd = {
            leishenOberser = false
        }))

        guanDealList.add(GuanDeal(150, isOver = {
            fulls(kuangjiang)
        }, chooseHero = {
            upAny(kuangjiang)
        }))

        guanDealList.add(
            GuanDeal(
                169,
                isOver = { nvyao.isInCar() },
                chooseHero = {
                    carDoing.downHero(kuangjiang)
                    upAny(nvyao)
                },
            )
        )
        guanDealList.add(GuanDeal(
            171,
            isOver = {fulls(niutou)},
            chooseHero = {
                carDoing.downHero(nvyao)
                upAny(niutou)
            }
        ))


        guanDealList.add(
            GuanDeal(189, isOver = { currentGuan() > 189 },
                chooseHero = {
                    delay(300)
                    val ind = upAny(xiongmao, zhanjiang, saman, lvgong, niutou, xiaoye,shexian)
                    if (ind < 0 && !isRenwu) {
                        upAny(guangqiu)
                    } else ind
//                checkHeroStarAndFull(this) { currentGuan() > 189 }
                })
        )

        guanDealList.add(GuanDeal(
            199,
            isOver = {
                currentGuan()>199
            },
            chooseHero = {
                deal199Super(this)
            },
        ).apply {
            des =
                "白球撞上后 按3 进入监听点名，期间可以按数字键盘进行下卡，点名结束后，可以按3重新进入白球卡阶段（下萨满补到3星）"
        })

        curGuanDeal = guanDealList.first()
    }

    override fun deal199Step0(): GuanDeal {
        return GuanDeal(0, isOver = {
            true
        })
    }

    override fun deal199Step3(): GuanDeal {
        return GuanDeal(0, isOver = {
            fulls(xiongmao,zhanjiang,guangqiu,shexian,saman,lvgong,kuangjiang)
        }, chooseHero = {
            carDoing.downHero(xiaoye)
            carDoing.downHero(niutou)
            upAny(guangqiu)
        })
    }

    override fun deal199Step1(): GuanDeal {
        return GuanDeal(0, isOver = {
            !zhanjiang.isInCar()
        }, chooseHero = {
            carDoing.downHero(zhanjiang)
            -1
        })
    }
    override fun deal199Step2(): GuanDeal {
        return GuanDeal(0, isOver = {
                                    fulls(zhanjiang, xiongmao, saman, niutou, lvgong, niutou,shexian)
        }, chooseHero = {
            upAny(zhanjiang, xiongmao, xiaoye, saman, lvgong, niutou,shexian)
        })
    }



    private fun currentGuan(): Int {
        return guankaTask?.currentGuanIndex ?: 0
    }


    private suspend fun shuaMu(heros: List<HeroBean?>, over: () -> Boolean): Int {
        var index = heros.indexOf(niutou)
        if (index > -1 && !isRenwu) {
            while (!over.invoke() && (!XueLiang.isMLess(0.9f))) {
                delay(50)
            }
            return index
        }
        return -1
    }




    override fun onChuanzhangClick(position: Int) {
    }

    var zs99Clicked = 0
    override suspend fun onKeyDown(code: Int): Boolean {
        if (guankaTask?.currentGuanIndex == 98 || guankaTask?.currentGuanIndex == 99) {
            if (code == KeyEvent.VK_NUMPAD3) {
                zs99Clicked = 1
                return true
            }
        }

        return super.onKeyDown(code)
    }

}