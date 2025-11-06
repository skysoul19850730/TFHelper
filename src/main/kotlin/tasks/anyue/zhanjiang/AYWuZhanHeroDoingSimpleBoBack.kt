package tasks.anyue.zhanjiang

import MainData
import data.HeroBean
import data.HeroCreator
import kotlinx.coroutines.delay
import java.awt.event.KeyEvent

class AYWuZhanHeroDoingSimpleBoBack : BaseSimpleAnYueHeroDoing() {

    val shuiling = HeroCreator.shuiling.create()
    val tieqi = HeroCreator.tieqi.create()
    val zhanjiang = HeroCreator.zhanjiang.create()
    val gugu = HeroCreator.gugu.create()
    val dianfa = HeroCreator.dianfa.create()
    val kuangjiang = HeroCreator.kuangjiang.create()
    val sishen = HeroCreator.sishen.create()
    val huanqiu = HeroCreator.huanqiu.create()
    val dijing = HeroCreator.dijing.create()
    val shexian = HeroCreator.shexian.create()

    override fun initHeroes() {
        heros = arrayListOf(zhanjiang, tieqi, shuiling, gugu, sishen, shexian, dianfa, dijing, kuangjiang, huanqiu)
        heros39Up4 = arrayListOf(kuangjiang, shuiling, gugu, dianfa)

        guanDealList.add(GuanDeal(0, isOver = {
            zhanjiang.isGold()
        }, chooseHero = {
            upAny(zhanjiang)
        }))
//        guanDealList.add(GuanDeal(19, isOver = { false }, chooseHero = {
//            deal19(this)
//        }, onGuanDealStart = {
//            start19Oberserver(true)
//        }))
        guanDealList.add(GuanDeal(9, isOver = {
            fulls(zhanjiang, tieqi, dijing, gugu, shuiling, dianfa, shexian) && yandou
        }, chooseHero = {
            if (!tieqi.isInCar()) {
                this.indexOf(tieqi)
            } else {
                upAny(zhanjiang, gugu, shexian, dijing, tieqi, shuiling, dianfa, zhuangbei = { yandou })
            }
        }))


        guanDealList.add(GuanDeal(21, isOver = {
            fulls(zhanjiang, sishen, tieqi, shuiling, gugu, dianfa, shexian) && yandou
        }, chooseHero = {
            upAny(zhanjiang, sishen, tieqi, shuiling, gugu, dianfa, shexian, zhuangbei = { yandou })
        }, onGuanDealStart = {
            carDoing.downHero(dijing)
        }))

        guanDealList.add(GuanDeal(31, isOver = { kuangjiang.isFull() }, chooseHero = {
            upAny(kuangjiang)
        }, onGuanDealStart = { carDoing.downHero(sishen) }))

        guanDealList.add(
            GuanDeal(39, isOver = { false },
                chooseHero = {
                    deal39(this)
                })
        )

        guanDealList.add(GuanDeal(40, isOver = {
            fulls(zhanjiang, sishen, tieqi, shuiling, gugu, dianfa, shexian)
        }, chooseHero = {
            upAny(zhanjiang, sishen, tieqi, shuiling, gugu, dianfa, shexian)
        }, onGuanDealStart = {
            carDoing.downHero(kuangjiang)
        }))
        guanDealList.add(GuanDeal(51, isOver = {
            fulls(zhanjiang, tieqi, shuiling, gugu, dianfa, shexian) && kuangjiang.isInCar() && qiangxi
        }, chooseHero = {
            upAny(zhanjiang, kuangjiang, tieqi, shuiling, gugu, dianfa, shexian, zhuangbei = { qiangxi })
        }, onGuanDealStart = {
            carDoing.downHero(sishen)
        }))
        guanDealList.add(GuanDeal(71, isOver = {
            fulls(zhanjiang, sishen, tieqi, shuiling, gugu, dianfa, shexian) && longxin
        }, chooseHero = {
            upAny(zhanjiang, sishen, tieqi, shuiling, gugu, dianfa, shexian, zhuangbei = { longxin })
        }, onGuanDealStart = {
            carDoing.downHero(kuangjiang)
        }))

        guanDealList.add(GuanDeal(101, isOver = {
            shengjian
        }, chooseHero = {
            zhuangbei {
                shengjian
            }
        }))

        guanDealList.add(GuanDeal(111, isOver = { kuangjiang.isInCar() && longxin }, chooseHero = {
            upAny(kuangjiang, zhuangbei = { longxin })
        }, onGuanDealStart = {
            carDoing.downHero(shexian)
            carDoing.downHero(sishen)
        }).apply { des = "下射线，下死神，满狂将，龙心，初五前打死" })


        guanDealList.add(GuanDeal(120, isOver = {
            fulls(sishen, shexian)
        }, chooseHero = {
            upAny(sishen, shexian)
        }, onGuanDealStart = {
            carDoing.downHero(kuangjiang)
        }
        ))

        guanDealList.add(GuanDeal(129, isOver = { currentGuan() > 129 }, chooseHero = { g129Index(this) })
            .apply { des = "按0下射线，再按0上射线" })


        guanDealList.add(
            GuanDeal(
                130,
                isOver = { shexian.isFull() },
                chooseHero = {
                    upAny(shexian)
                },
                onGuanDealStart = {
                    g129State = -1
                })
        )

        guanDealList.add(GuanDeal(139, isOver = { false }, chooseHero = {
            if (g139State == 0) {
                if (this.indexOf(kuangjiang) > -1) {
                    while (g139State == 0) {
                        delay(100)
                    }
                    carDoing.downHero(gugu)
                    this.indexOf(kuangjiang)
                } else -1
            } else {
                var index = this.indexOf(gugu)
                if (index > -1) {
                    g139State = 0
                    index
                } else -1
            }

        }).apply {
            des =
                "按0切一轮咕咕狂将，执行顺序：下咕咕，上狂将，下狂将，再上咕咕，备选好狂将。为一轮。最好等预备好狂将后再按0开始一轮"
        })

        guanDealList.add(GuanDeal(140, isOver = {
            if (g149Type == 0) {
                fulls(gugu, kuangjiang) && qiangxi
            } else
                false
        }, chooseHero = {
            if (currentGuan() > 149) {
                -1
            } else {
                while (g149Type == -1) {
                    delay(100)
                }
                if (g149Type == 0) {
                    carDoing.downHero(sishen)
                    upAny(kuangjiang, gugu, zhuangbei = { qiangxi })
                } else if (g149Type == 1) {
                    if (g139State == -1) {
                        if (gugu.isFull()) {
                            g139State = 0
                            -1
                        } else {
                            carDoing.downHero(kuangjiang)
                            upAny(gugu)
                        }
                    } else {
                        if (g139State == 0) {
                            if (this.indexOf(kuangjiang) > -1) {
                                while (g139State == 0) {
                                    delay(100)
                                }
                                carDoing.downHero(gugu)
                                this.indexOf(kuangjiang)
                            } else -1
                        } else {
                            var index = this.indexOf(gugu)
                            if (index > -1) {
                                g139State = 0
                                index
                            } else -1
                        }
                    }
                } else {
                    carDoing.downHero(kuangjiang)
                    upAny(gugu)
                }
            }

        }).apply {
            des =
                "如果是土灵石柱形态，按1，会下死神满狂将幻强袭。.如果是火灵形态，按2，之后等149时按0来切一轮咕咕狂将，同139，如果是水灵月亮形态，按3，暂时只是满上咕咕，打不过"
        })

        guanDealList.add(GuanDeal(150, isOver = { gugu.isFull() && longxin }, chooseHero = {
            upAny(gugu, sishen, zhuangbei = { longxin })
        }, onGuanDealStart = {
            g139State = -1
            carDoing.downHero(kuangjiang)
        }))


        guanDealList.add(GuanDeal(159, isOver = { false }, chooseHero = {
            if (g139State == 0) {
                if (this.indexOf(kuangjiang) > -1) {
                    while (g139State == 0) {
                        delay(100)
                    }
                    carDoing.downHero(gugu)
                    this.indexOf(kuangjiang)
                }else -1
            } else {
                var index = this.indexOf(gugu)
                if (index > -1) {
                    g139State = 0
                    index
                }else -1
            }
        }, onGuanDealStart = {
            g139State = 0
        }))


        curGuanDeal = guanDealList.get(0)
    }

    private fun currentGuan(): Int {
        return guankaTask?.currentGuanIndex ?: 0
    }

    var g149Type = -1;  //0 石柱，切强袭，死神换狂将； 1 火灵boss，狂将切咕咕，切回 满咕咕尽量；2是月亮boss，暂时打不过


    var g139State = 0

    var g129State = 0//0等待,1 下宝库 备宝库，2上宝库 //回到了初始态，等1再下宝库循环


    suspend fun g129Index(heros: List<HeroBean?>): Int {
        if (currentGuan() > 129) return -1
        while (g129State == 0) {
            delay(100)
            if (currentGuan() > 129) return -1
        }
        when (g129State) {
            1 -> {
                carDoing.downHero(shexian)
                var index = heros.indexOf(shexian)
                if (index > -1) {
                    while (g129State == 1) {
                        delay(100)
                        if (currentGuan() > 129) return -1
                    }
                    return index
                }
                return -1
            }

        }
        return -1
    }

    override suspend fun doAfterHeroBeforeWaiting(heroBean: HeroBean) {

        if (heroBean == kuangjiang && (guankaTask?.currentGuanIndex == 139 || guankaTask?.currentGuanIndex == 138
                    || guankaTask?.currentGuanIndex == 149 || guankaTask?.currentGuanIndex == 148
                    || guankaTask?.currentGuanIndex == 159 || guankaTask?.currentGuanIndex == 158
                    )
        ) {
            delay(500)
            carDoing.downHero(kuangjiang)
        }

        super.doAfterHeroBeforeWaiting(heroBean)
    }

    override suspend fun onKeyDown(code: Int): Boolean {

        if (guankaTask?.currentGuanIndex == 129 || guankaTask?.currentGuanIndex == 128
        ) {//按0 下射线，备射线，再按0，上射线
            when (code) {
                KeyEvent.VK_NUMPAD0 -> {
                    g129State = if (g129State == 0) 1 else 0
                }
            }

            return true
        }

        if (guankaTask?.currentGuanIndex == 139 || guankaTask?.currentGuanIndex == 138
            || guankaTask?.currentGuanIndex == 159 || guankaTask?.currentGuanIndex == 158
        ) {
            if (code == KeyEvent.VK_NUMPAD0) {
                g139State = 1
                return true
            }
        }
        var curGuan = guankaTask?.currentGuanIndex ?: 0
        if (curGuan in 141..149
        ) {
            if (code == KeyEvent.VK_NUMPAD0) {
                g139State = 1
                return true
            } else if (code == KeyEvent.VK_NUMPAD1) {

                g149Type = 0
                return true
            } else if (code == KeyEvent.VK_NUMPAD2) {
                g139State = -1
                g149Type = 1
                return true
            } else if (code == KeyEvent.VK_NUMPAD3) {
                g149Type = 2
                return true
            }
        }


        if (code == KeyEvent.VK_PLUS) {
            MainData.kuangjiangUpTime.value += 100
        } else if (code == KeyEvent.VK_MINUS) {
            MainData.kuangjiangUpTime.value -= 100
        }


        if (super.onKeyDown(code)) {
            return true
        }
        return doOnKeyDown(code)
    }

    suspend fun doOnKeyDown(code: Int): Boolean {


        return true
    }
}