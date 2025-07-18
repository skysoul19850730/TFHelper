package tasks.hanbing2.huoling

import data.HeroBean
import data.HeroCreator
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import log
import tasks.XueLiang
import tasks.hanbing2.BaseSimpleHBHeroDoing

class HBBoHuolingHeroDoing : BaseSimpleHBHeroDoing() {

    val xiongmao = HeroCreator.xiongmao2.create()
    val shuiling = HeroCreator.shuiling.create()
    val huoling = HeroCreator.huoling.create()
    val gugong = HeroCreator.gugong.create()
    val binggong = HeroCreator.binggong.create()
    val nvyao = HeroCreator.nvyao.create()

    val baoku = HeroCreator.baoku.create()

    val guangqiu = HeroCreator.guangqiu.create()
    val huanqiu = HeroCreator.huanqiu.create()
    val muqiu = HeroCreator.muqiu.create()

    override fun initHeroes() {
        super.initHeroes()
        heros = arrayListOf(xiongmao, shuiling, huoling, gugong, binggong, nvyao, baoku, guangqiu, huanqiu, muqiu)
        guanDealList.add(GuanDeal(
            startGuan = 0,
            isOver = {
                fullBase() && qiangxi
            },
            chooseHero = {
                upBase(zhuangbei = { qiangxi })
            }
        ))

        changeZhuangbei(100, zhuangbei = { yandou })

        guanDealList.add(
            GuanDeal(109, isOver = { false },
                chooseHero = {
                    delay(500)
                    val ind = upBase()
                    if (ind < 0) {
                        upAny(guangqiu)
                    } else ind
                })
        )


        guanDealList.add(GuanDeal(111, isOver = {
            fullBase() && qiangxi
        }, chooseHero = {
            if(needReCheckStar) {
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

        changeZhuangbei(150) { qiangxi }
        changeZhuangbei(170) { yandou }
        changeZhuangbei(180) { qiangxi }
        guanDealList.add(
            GuanDeal(189, isOver = { currentGuan() > 189 },
                chooseHero = {
                    delay(500)
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
            upBase() { longxin }
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

    private suspend fun deal199(heros: List<HeroBean?>): Int {

        var dianmingIndex = carDoing.getHB199Selected()
        if (position199 > -1 || dianmingIndex > -1) {
            carDoing.downPosition(position199)
            carDoing.downPosition(dianmingIndex)
            position199 = -1
        }
        if (carDoing.hasAllOpenSpace() || carDoing.hasNotFull()) {
            return heros.upBase()
        } else {
            while (true) {
                var dianmingIndex = carDoing.getHB199Selected()
                if (position199 > -1 || dianmingIndex > -1) {
                    carDoing.downPosition(position199)
                    carDoing.downPosition(dianmingIndex)
                    position199 = -1
                    return heros.upBase()
                }
                delay(100)
            }
        }

        return -1
    }
private var step199 = 1  // 1打白球阶段， 2点名阶段，
    private var count199 = 0

    private var baiqiuCount = 0
//    private suspend fun deal199(heros: List<HeroBean?>): Int {
//        while (step199 == 0) {
//            delay(200)
//        }
//        if (step199 == 1) {
//            if (huoling.isInCar()) {
//                carDoing.downHero(huoling)
//            }
//
//                //等点名
//                XueLiang.observerXueDown()//掉血 等于 白球撞上了
//                log("白球撞上了，进入step2")
//
//                baiqiuCount++
//                if(baiqiuCount<3) {
//                    GlobalScope.launch {
//                        delay(3200)
//                        step199 = 1
//                    }
//                }
//                step199 = 2
//                delay(300)//怕不同步，延迟300，满上萨满
//
//
//                //按3 会改成2，不监听掉血了，怕处于涨血状态判断不准。白球撞完按3
////                while (step199 == 1) {
////                    delay(100)
////                }
//
//
//                return heros.upAny(huoling)
//        } else if (step199 == 2) {
//
//            var dianmingIndex = carDoing.getHB199Selected()
//
//            var other = otherCarDoing.getHB199Selected()
//
//            if (position199 > -1 || dianmingIndex > -1) {
//                carDoing.downPosition(position199)
//                carDoing.downPosition(dianmingIndex)
//                count199++
//                position199 = -1
//            }
//            if (position199 > -1 || dianmingIndex > -1 || other>-1) {
//                count199++
////                GlobalScope.launch {
////                    delay(2000)
////                    step199 = 1
////                }
//            }
//
//
//            if (carDoing.hasAllOpenSpace() || carDoing.hasNotFull() ) {
//                return heros.upBase()
//            } else {
//                while (step199 == 2) {
//                    var dianmingIndex = carDoing.getHB199Selected()
//                    other = otherCarDoing.getHB199Selected()
//                    if (position199 > -1 || dianmingIndex > -1 || other>-1) {
//                        count199++
////                        GlobalScope.launch {
////                            delay(2000)
////                            step199 = 1
////                        }
//                    }
//
//                    if (position199 > -1 || dianmingIndex > -1) {
//                        carDoing.downPosition(position199)
//                        carDoing.downPosition(dianmingIndex)
//                        position199 = -1
//                        return heros.upBase()
//                    }
//                    delay(100)
//                }
//            }
//
//        }
//
//
//        return -1
//    }

    private fun currentGuan(): Int {
        return guankaTask?.currentGuanIndex ?: 0
    }

    fun fullBase(): Boolean {
        return fulls( binggong,xiongmao, shuiling, huoling, gugong, nvyao, baoku)
    }

    fun List<HeroBean?>.upBase(zhuangbei: (() -> Boolean)? = null): Int {
        return upAny( binggong,xiongmao, shuiling, huoling, gugong, nvyao, baoku, zhuangbei = zhuangbei)
    }
}