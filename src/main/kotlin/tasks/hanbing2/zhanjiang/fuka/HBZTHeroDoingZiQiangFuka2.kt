package tasks.hanbing2.zhanjiang.fuka

import data.HeroBean
import data.HeroCreator
import getImage
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import tasks.XueLiang
import tasks.hanbing2.BaseSimpleHBHeroDoing
import ui.zhandou.hanbing.HanBingModel
import utils.HBUtil

class HBZTHeroDoingZiQiangFuka2 : BaseSimpleHBHeroDoing() {

    val isRenwu = false

    val xiaoye = HeroCreator.xiaoye.create()
    val dianfa = HeroCreator.dianfa.create().apply {
        isMohua = false
    }
    val niutou = HeroCreator.niutou.create().apply {
        isMohua = false
    }
    val lvgong = HeroCreator.lvgong.create()
    val yuren = HeroCreator.yuren.create()
    val tianshi = HeroCreator.tianshi.create()
    val bingnv = HeroCreator.bingnv.create()

    val shexian = HeroCreator.shexian.create()
    val bingqiu = HeroCreator.bingqiu.create()
    val guangqiu = if (!isRenwu) HeroCreator.guangqiu.create() else
        HeroBean(HanBingModel.renwuKa.value)


    override fun initHeroes() {
        super.initHeroes()
        heros = arrayListOf(tianshi, bingnv, xiaoye, lvgong, bingqiu, yuren, niutou, dianfa, shexian, guangqiu)

        guanDealList.add(GuanDeal(0, isOver = {
            bingnv.isInCar()
        }, chooseHero = {
            val index = indexOf(bingnv)
            if (index > -1) {
                while (curGuan < 9) {
                    delay(500)
                }
                index
            } else -1
        }))

        addGuanDeal(10) {
            over {
                fulls(shexian, xiaoye, niutou, dianfa)
            }
            chooseHero {
                upAny(shexian, xiaoye, niutou, dianfa)
            }
            onStart {
                carDoing.downHero(bingnv)
            }
        }
        addGuanDeal(29) {
            over {
                fulls(yuren, lvgong, shexian, xiaoye, niutou, dianfa)
            }
            chooseHero {
                upAny(yuren, lvgong, shexian, xiaoye, niutou, dianfa)
            }
        }
        addGuanDeal(98) {
            over {
                fulls(tianshi)
            }
            chooseHero {
                upAny(tianshi)
            }
           
        }
        addGuanDeal(100) {
            over {
                fulls(bingnv)
            }
            chooseHero {
                upAny(bingnv)
            }
            onStart {
                carDoing.downHero(tianshi)
            }
        }

        addGuanDeal(108) {
            over {
                curGuan > 109
            }
            chooseHero {
                var index = upBase()
                if (index > -1) index else {
                    if(isRenwu){
                        -1
                    }else
                    upAny(guangqiu)
                }
            }
            onStart {
                carDoing.downHero(bingnv)
            }
        }
        guanDealList.add(GuanDeal(110, isOver = {
            fullBase()
        }, chooseHero = {
            if (needReCheckStar) {
                carDoing.reCheckStars()
                needReCheckStar = false
            }
            upBase()
        }, onGuanDealStart = {
            needReCheckStar = true
        }))

        guanDealList.add(
            GuanDeal(129, onlyDoSomething = {
                startChuanZhangOberserver2()
            })
        )

        guanDealList.add(GuanDeal(130, isOver = {
            fullBase()
        }, chooseHero = {
            upBase()
        }, onGuanDealStart = {
            stopChuanZhangOberserver()
        }))

        guanDealList.add(GuanDeal(148, isOver = {
            fulls(dianfa, lvgong, shexian, xiaoye, niutou, tianshi, yuren)
        }, chooseHero = {
            upAny(dianfa, lvgong, shexian, xiaoye, niutou, tianshi, yuren)
        }, onGuanDealStart = { startLeishenOberserver() }))

        gudingShuaQiuTask("bingqiu", 149, 2500, overGuan = 150, dealTime = 3000,
            sholudPasue = {
                if (g149 == 1) {//
                    delay(3000)//保证的是 冰完打3秒
                    g149 = 0
                }
            },
            customOverJudge = {
                curGuan > 149 || blueOverCount >= 2
            }
        )

        guanDealList.add(GuanDeal(150, onlyDoSomething = { leishenOberser = false }))

        guanDealList.add(
            GuanDeal(189, isOver = { curGuan > 189 },
                chooseHero = {
                    delay(500)
                    val ind = upBase()
                    if (ind < 0 && !isRenwu) {
                        upAny(guangqiu)
                    } else ind
//                checkHeroStarAndFull(this) { currentGuan() > 189 }
                },
                onGuanDealStart = {//废话时间
                    delay(10000)
                }
            )
        )

//        change2Tianshi3(
//            190
//        )

        addGuanDeal(190){
            over {
                yuren.currentLevel == 3 && tianshi.currentLevel == 3
            }
            chooseHero {
                if(tianshi.currentLevel<3 && yuren.currentLevel<3){
                    upAny(tianshi,yuren)
                }else if(tianshi.currentLevel<3){
                    indexOf(tianshi)
                }else if(yuren.currentLevel<3){
                    indexOf(yuren)
                }else {
                    -1
                }
            }
            onStart { 
                carDoing.downHero(tianshi)
                carDoing.downHero(yuren)
            }
        }


        guanDealList.add(GuanDeal(
            199,
            isOver = {
                curGuan > 199
            },
            chooseHero = {
                deal199Super2(this)
            },
            onGuanDealStart = {
                step199Super = 1
            },
        ).apply {
            des =
                "白球撞上后 按3 进入监听点名，期间可以按数字键盘进行下卡，点名结束后，可以按3重新进入白球卡阶段（下萨满补到3星）"
        })

        addGuan210(arrayListOf(yuren, niutou, dianfa,xiaoye,tianshi,lvgong))
        curGuanDeal = guanDealList.first()
    }

    fun List<HeroBean?>.upBase(): Int {
        return upAny(lvgong, shexian, xiaoye, niutou, tianshi, yuren, dianfa)
    }

    fun fullBase(): Boolean {
        return fulls(lvgong, shexian, xiaoye, niutou, tianshi, yuren, dianfa)
    }

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


    suspend fun deal199Super2(heros: List<HeroBean?>): Int {
        if (step199Super == 1) {//准备冰球，变白扔冰
            val index = heros.indexOf(bingqiu)
            if (index > -1 && !bai) {

                while (!bai) {
                    delay(100)
                    var img = getImage(App.rectWindow)
                    bai = HBUtil.is199Bai(img)
                }

                //变白后
                GlobalScope.launch {
                    XueLiang.observerXueDown(0.1f, over = { !running || curGuan > 199 })
                    step199Super = 2
                }

            } else if (index > -1) {
                delay(1000)
            }
            return if (step199Super == 1) {
                index
            } else -1

        } else if (step199Super == 2) {

            var dianmingIndex = carDoing.getHB199Selected()

            if (position199 > -1 || dianmingIndex > -1) {
                carDoing.downPosition(position199)
                carDoing.downPosition(dianmingIndex)
                position199 = -1
            }

            if (carDoing.hasAllOpenSpace() || carDoing.hasNotFull()) {
                return heros.upBase()
            } else {
                while (step199Super == 2 && curGuan == 199) {
                    var dianmingIndex = carDoing.getHB199Selected()
                    if (position199 > -1 || dianmingIndex > -1) {
                        carDoing.downPosition(position199)
                        carDoing.downPosition(dianmingIndex)
                        position199 = -1
                        return heros.upBase()
                    }
                    delay(100)
                }
                return -1
            }
        } else {
            return -1
        }
    }

    var g149 = 0//  红球变1
    var buleCount = 0
    var lastBing = 0L
    var blueOverCount = 0

    override fun onLeiShenRedBallShow() {
        super.onLeiShenRedBallShow()
        if (blueOverCount < buleCount) {
            blueOverCount = buleCount
        }
        g149 = 1
    }

    override fun onLeiShenBlueBallShow() {
        super.onLeiShenBlueBallShow()
        if (blueOverCount < buleCount) {
            blueOverCount = buleCount
        }
        buleCount++
    }


    private fun currentGuan(): Int {
        return guankaTask?.currentGuanIndex ?: 0
    }


    override fun onChuanzhangClick(position: Int) {
    }

    override suspend fun onKeyDown(code: Int): Boolean {

        return super.onKeyDown(code)
    }

}