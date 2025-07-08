package tasks.hanbing2.zhanjiang

import data.HeroBean
import data.HeroCreator
import kotlinx.coroutines.delay
import tasks.XueLiang
import tasks.hanbing2.BaseSimpleHBHeroDoing
import ui.zhandou.hanbing.HanBingModel
import java.awt.event.KeyEvent

class HB5ZHeroDoingZiQiang : BaseSimpleHBHeroDoing() {

    val isRenwu = false

    val zhanjiang = HeroCreator.zhanjiang2.create()
    val tieqi = HeroCreator.tieqi.create()
    val saman = HeroCreator.saman2.create()
    val wangjiang = HeroCreator.wangjiang2.create()
    val sishen = HeroCreator.sishen2.create()
    val yuren = HeroCreator.yuren.create()
    val muqiu = HeroBean(if (isRenwu) HanBingModel.renwuKa.value else "muqiu", 40, needCar = false, compareRate = 0.95)
    val bingqi = HeroCreator.bingqi.create()
    val huanqiu = HeroCreator.huanqiu.create()
    val guangqiu = HeroCreator.guangqiu.create()

    override fun initHeroes() {
        super.initHeroes()
        heros = arrayListOf(zhanjiang, tieqi, saman, wangjiang, sishen, yuren, muqiu, bingqi, huanqiu, guangqiu)
        heroDoNotDown129 = zhanjiang

        guanDealList.add(GuanDeal(0, isOver = {
            fullBase() && shengjian
        }, chooseHero = {
            if (zhanjiang.isGold()) {
                upBase(zhuangbei = { shengjian })
            } else {
                upAny(zhanjiang)
            }
        }))

        guanDealList.add(
            GuanDeal(91, isOver = { bingqi.isFull() },
                chooseHero = { upAny(bingqi) }, onGuanDealStart = { carDoing.downHero(wangjiang) })
        )

        guanDealList.add(
            GuanDeal(100,
                isOver = { wangjiang.isFull() && yandou },
                chooseHero = { upAny(wangjiang, zhuangbei = { yandou }) },
                onGuanDealStart = { carDoing.downHero(bingqi) })
        )

        guanDealList.add(
            GuanDeal(109, isOver = { false },
                chooseHero = {
                    delay(300)
                    val ind = upBase()
                    if (ind < 0) {
                        upAny(guangqiu)
                    } else ind
                })
        )

        guanDealList.add(GuanDeal(111, isOver = {
            fullBase() && qiangxi
        }, chooseHero = {
            if (needReCheckStar) {
                carDoing.reCheckStars()
                needReCheckStar = false
            }
            upBase(zhuangbei = { qiangxi })
        }, onGuanDealStart = {
            needReCheckStar = true
        }))

        guanDealList.add(
            GuanDeal(129, onlyDoSomething = {
                startChuanZhangOberserver()
            })
        )
        guanDealList.add(GuanDeal(130, isOver = {
            fullBase() && yandou
        }, chooseHero = {
            upBase { yandou }
        }, onGuanDealStart = { stopChuanZhangOberserver() }))
        guanDealList.add(GuanDeal(150, isOver = {
            bingqi.isFull() && longxin
        }, chooseHero = {
            upAny(bingqi, zhuangbei = { longxin })
        }, onGuanDealStart = {
            carDoing.downHero(wangjiang)
        }))
        guanDealList.add(
            GuanDeal(159, isOver = { false },
                chooseHero = {
                    shuaMu(this) {
                        (guankaTask?.currentGuanIndex ?: 0) > 159
                    }
                })
        )
        guanDealList.add(
            GuanDeal(
                160,
                isOver = { fullBase() },
                chooseHero = { upBase() },
                onGuanDealStart = { carDoing.downHero(bingqi) })
        )

        guanDealList.add(GuanDeal(170, isOver = {
            fullBase() && qiangxi
        }, chooseHero = {
            upBase(zhuangbei = { qiangxi })
        }))

        guanDealList.add(
            GuanDeal(179, isOver = { (guankaTask?.currentGuanIndex ?: 0) > 179 },
                chooseHero = {
                    shuaMu(this) {
                        (guankaTask?.currentGuanIndex ?: 0) > 179
                    }
                })
        )

        guanDealList.add(
            GuanDeal(189, isOver = { currentGuan() > 189 },
                chooseHero = {
                    delay(300)
                    val ind = upBase()
                    if (ind < 0) {
                        upAny(guangqiu)
                    } else ind
//                checkHeroStarAndFull(this) { currentGuan() > 189 }
                })
        )

        guanDealList.add(GuanDeal(191, isOver = {
            fullBase() && longxin
        }, chooseHero = {
            if (needReCheckStar) {
                carDoing.reCheckStars()
                needReCheckStar = false
            }
            upBase(zhuangbei = { longxin })
        }, onGuanDealStart = {
            needReCheckStar = true
        }))


        guanDealList.add(GuanDeal(
            199,
            isOver = {
                false
            },
            chooseHero = {
                deal199(this)
            },
        ).apply {
            des =
                "白球撞上后 按3 进入监听点名，期间可以按数字键盘进行下卡，点名结束后，可以按3重新进入白球卡阶段（下萨满补到3星）"
        })

        curGuanDeal = guanDealList.first()
    }

    private var step199 = 1  // 1打白球阶段， 2点名阶段，
    private var count199 = 0

    private suspend fun deal199(heros: List<HeroBean?>): Int {
        while (step199 == 0) {
            delay(200)
        }
        if (step199 == 1) {
            if (saman.isFull()) {
                carDoing.downHero(saman)
            }

            if (saman.currentLevel < 3) {
                return heros.upAny(saman)
            } else {
                //等点名
//                    XueLiang.observerXueDown()//掉血 等于 白球撞上了
//                    step199 = 2
//                    delay(300)//怕不同步，延迟300，满上萨满

                //按3 会改成2，不监听掉血了，怕处于涨血状态判断不准。白球撞完按3
                while (step199 == 1) {
                    delay(100)
                }

                return heros.upAny(saman)
            }
        } else if (step199 == 2) {

            var dianmingIndex = carDoing.getHB199Selected()
            if (position199 > -1 || dianmingIndex > -1) {
                carDoing.downPosition(position199)
                carDoing.downPosition(dianmingIndex)
                count199++
                position199 = -1
            }
            if (carDoing.hasOpenSpace() || carDoing.hasNotFull()  || XueLiang.getXueLiang()<0.95) {
                return heros.upAny(zhanjiang, yuren, sishen, tieqi, wangjiang, saman,muqiu)
            } else {
                while (step199 == 2) {
                    var dianmingIndex = carDoing.getHB199Selected()
                    if (position199 > -1 || dianmingIndex > -1) {
                        carDoing.downPosition(position199)
                        carDoing.downPosition(dianmingIndex)
                        position199 = -1
                        count199++
                        return heros.upAny(zhanjiang, yuren, sishen, tieqi, wangjiang, saman)
                    }
                    delay(100)
                }
            }

        }


        return -1
    }

    override suspend fun onKeyDown(code: Int): Boolean {
        if (code == KeyEvent.VK_NUMPAD3) {//9强制改变waitting，防止waiting有逻辑错误不上卡
            if (guankaTask?.currentGuanIndex == 199 || guankaTask?.currentGuanIndex == 198) {

                step199 = if (step199 == 1) 2 else 1

                return true
            }
        }


        return super.onKeyDown(code)
    }

    private fun currentGuan(): Int {
        return guankaTask?.currentGuanIndex ?: 0
    }


    private suspend fun shuaMu(heros: List<HeroBean?>, over: () -> Boolean): Int {
        var index = heros.indexOf(muqiu)
        if (index > -1 && !isRenwu) {
            while (!over.invoke() && (!XueLiang.isMLess(0.9f))) {
                delay(50)
            }
            return index
        }
        return -1
    }

    fun fullBase(): Boolean {
        return fulls(zhanjiang, tieqi, saman, wangjiang, sishen, yuren)
    }

    fun List<HeroBean?>.upBase(zhuangbei: (() -> Boolean)? = null): Int {
        return upAny(zhanjiang, tieqi, saman, sishen, wangjiang, yuren, zhuangbei = zhuangbei)
    }

    private suspend fun checkHeroStarAndFull(heros: List<HeroBean?>, over: () -> Boolean): Int {
        if (carDoing.hasOpenSpace() || carDoing.hasNotFull()) {
            return heros.upBase()
        }
        while (!over.invoke()) {
            delay(500)
            carDoing.reCheckStars()

            if (carDoing.hasOpenSpace() || carDoing.hasNotFull()) {
                return heros.upBase()
            }
        }
        return -1
    }


    override fun onChuanzhangClick(position: Int) {
    }

}