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

class HBZTHeroDoingZiQiangFuka : BaseSimpleHBHeroDoing() {

    val isRenwu = true

    //bingnv,xiaoye,shengqi,shexian,bingqiu,zhanjiang,nvyao,niutou,kuangjiang,guangqiu
    val dianfa = HeroCreator.dianfa.create().apply {
        isMohua = false
    }
    val bingnv = HeroCreator.bingnv.create()
    val nvyao = HeroCreator.nvyao.create()
    val xiaoye = HeroCreator.xiaoye.create()
    val niutou = HeroCreator.niutou.create().apply {
        isMohua = false
    }
    val shengqi = HeroCreator.shengqi.create()
    val kuangjiang = HeroCreator.kuangjiang.create()
    val shexian = HeroCreator.shexian.create()


    val bingqiu = HeroCreator.bingqiu.create()
    val guangqiu = if (!isRenwu) HeroCreator.guangqiu.create() else
        HeroBean(HanBingModel.renwuKa.value)


    override fun initHeroes() {
        super.initHeroes()
        heros = arrayListOf(dianfa, bingnv, xiaoye, shengqi, bingqiu, nvyao, niutou, kuangjiang, shexian, guangqiu)

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
                fulls(bingnv, shengqi, shexian, xiaoye, niutou, dianfa, nvyao)
            }
            chooseHero {
                upAny(bingnv, shengqi, shexian, xiaoye, niutou, dianfa, nvyao)
            }
        }


        var start = 40
        for (i in start..190) {
            var other: GuanDeal? = when (i) {
                108 -> GuanDeal(0, isOver = {
                    curGuan > 109
                }, chooseHero = {
                    delay(1000)
                    upAny(bingnv, shengqi, shexian, xiaoye, niutou, dianfa, nvyao)
                })

                128 -> GuanDeal(0, isOver = {
                    fulls(bingnv, shengqi, shexian, xiaoye, niutou, dianfa, nvyao)
                }, chooseHero = {
                    upAny(bingnv, shengqi, shexian, xiaoye, niutou, dianfa, nvyao)
                }, onGuanDealStart = {
//                    carDoing.downHero(baoku)
                    startChuanZhangOberserver2()
                })

                130 -> GuanDeal(0, isOver = {
                    fulls(bingnv, kuangjiang, shexian, xiaoye, niutou, dianfa, nvyao)
                }, chooseHero = {
                    upAny(bingnv, kuangjiang, shexian, xiaoye, niutou, dianfa, nvyao)
                }, onGuanDealStart = { stopChuanZhangOberserver() })

                148 ->  GuanDeal(0, isOver = {
                    fulls(bingnv, shengqi, shexian, xiaoye, niutou, dianfa, nvyao)
                }, chooseHero = {
                    upAny(bingnv, shengqi, shexian, xiaoye, niutou, dianfa, nvyao)
                }, onGuanDealStart = { startLeishenOberserver() })


                150 -> GuanDeal(150, onlyDoSomething = { leishenOberser = false })

                188 -> GuanDeal(0, isOver = {
                    curGuan > 189
                }, chooseHero = {
                    delay(1000)
                    upAny(bingnv, shengqi, shexian, xiaoye, niutou, dianfa, nvyao)
                })

                else -> null
            }


            if (i == 149) {
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
            }


            if (i % 10 == 0) {
                change2Tianshi3(i, other)
            } else if (i % 10 == 8) {
                change2Tianshi4(i, other)
            }
        }


        guanDealList.add(GuanDeal(
            199,
            isOver = {
                currentGuan() > 199
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

        change2Tianshi3(200)

        curGuanDeal = guanDealList.first()
    }


    private fun change2Tianshi3(guan: Int, otherGuanDeal: GuanDeal? = null) {

        addGuanDeal(guan) {
            over {
                kuangjiang.isFull() && otherGuanDeal?.isOver?.invoke() ?: true
            }
            chooseHero {

                val index = upAny(kuangjiang)
                if (index > -1) {
                    index
                } else {
                    otherGuanDeal?.chooseHero?.invoke(this) ?: -1
                }
            }
            onStart {
                carDoing.downHero(shengqi)
                otherGuanDeal?.onGuanDealStart?.invoke()
            }
            onEnd {
                otherGuanDeal?.onGuanDealEnd?.invoke()
            }
        }
    }

    private fun change2Tianshi4(guan: Int, otherGuanDeal: GuanDeal? = null) {

        var selfOver = false
        var startTime = 0L
        addGuanDeal(guan) {
            over {
                shengqi.isInCar() && otherGuanDeal?.isOver?.invoke() ?: true
            }
            chooseHero {
                val index = upAny(shengqi)
                if (index > -1 && !shengqi.isInCar() && !selfOver) {
                    delay(3000 - (System.currentTimeMillis() - startTime))
                    selfOver = true
                    upAny(shengqi)
                } else {
                    otherGuanDeal?.chooseHero?.invoke(this) ?: upAny(shengqi)
                }
            }
            onStart {
                carDoing.downHero(kuangjiang)
                startTime = System.currentTimeMillis()
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
            carDoing.downHero(kuangjiang)

            var dianmingIndex = carDoing.getHB199Selected()

            if (position199 > -1 || dianmingIndex > -1) {
                carDoing.downPosition(position199)
                carDoing.downPosition(dianmingIndex)
                position199 = -1
            }

            if (carDoing.hasAllOpenSpace() || carDoing.hasNotFull()) {
                return heros.upAny(bingnv, shengqi, shexian, xiaoye, niutou, dianfa, nvyao)
            } else {
                while (step199Super == 2 && curGuan == 199) {
                    var dianmingIndex = carDoing.getHB199Selected()
                    if (position199 > -1 || dianmingIndex > -1) {
                        carDoing.downPosition(position199)
                        carDoing.downPosition(dianmingIndex)
                        position199 = -1
                        return heros.upAny(bingnv, shengqi, shexian, xiaoye, niutou, dianfa, nvyao)
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