package tasks.hanbing2.zhanjiang

import data.HeroBean
import data.HeroCreator
import kotlinx.coroutines.delay
import org.apache.commons.compress.harmony.pack200.PackingUtils.log
import tasks.XueLiang
import tasks.hanbing2.BaseSimpleHBHeroDoing
import ui.zhandou.hanbing.HanBingModel
import java.awt.event.KeyEvent

class HBZTHeroDoingZiQiang2 : BaseSimpleHBHeroDoing() {
    val isRenwu = false


    val zhanjiang = HeroCreator.zhanjiang.create()
    val tieqi = HeroCreator.tieqi.create()
    val gugu = HeroCreator.gugu.create()
    val wangjiang = HeroCreator.wangjiang.create()
    val jiaonv = HeroCreator.jiaonv.create()
    val dianfa = HeroCreator.dianfa.create()
    val feiting = HeroCreator.feiting.create()
    val tianshi = HeroCreator.tianshi.create()

    val huanqiu = HeroCreator.huanqiu.create()

    val guangqiu =
        HeroBean(if (isRenwu) HanBingModel.renwuKa.value else "guangqiu", 40, needCar = false, compareRate = 0.95)


    override fun initHeroes() {
        super.initHeroes()
        heros = arrayListOf(
            zhanjiang,
            tieqi,
            gugu,
            wangjiang,
            jiaonv,
            dianfa,
            feiting,
            tianshi,
            huanqiu,
            guangqiu
        )
        heroDoNotDown129 = zhanjiang

        addGuanDeal(0) {
            over {
                zhanjiang.currentLevel == 3
            }
            chooseHero {
                if (zhanjiang.isInCar()) {
                    upAny(zhanjiang, gugu, jiaonv, feiting)
                } else {
                    upAny(zhanjiang)
                }
            }
        }

        addGuanDeal(19) {
            over {
                fulls(zhanjiang, gugu, jiaonv, tieqi, wangjiang, feiting) && longxin
            }
            chooseHero {
                upAny(zhanjiang, gugu, jiaonv, tieqi, wangjiang, feiting, zhuangbei = { longxin })
            }
        }

        addGuanDeal(29) {
            over {
                fulls(dianfa) && longxin
            }
            chooseHero {
                upAny(dianfa, zhuangbei = { longxin })
            }
        }
        addGuanDeal(98) {
            over {
                fulls(tianshi)
            }
            chooseHero {
                upAny(tianshi)
            }
            onStart {
                carDoing.downHero(wangjiang)
            }
        }
        addGuanDeal(100) {
            over {
                fulls(wangjiang) && yandou
            }
            chooseHero {
                upAny(wangjiang, zhuangbei = { yandou })
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
                if (index > -1) index else upAny(guangqiu)
            }
            onStart {
                carDoing.downHero(wangjiang)
            }
        }

        guanDealList.add(GuanDeal(110, isOver = {
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
                startChuanZhangOberserver2()
            })
        )
        guanDealList.add(GuanDeal(130, isOver = {
            fulls(zhanjiang, gugu, tieqi, jiaonv, dianfa, wangjiang, feiting) && yandou
        }, chooseHero = {
            upAny(zhanjiang, gugu, tieqi, jiaonv, dianfa, wangjiang, feiting, zhuangbei = { yandou })
        }, onGuanDealStart = {
            stopChuanZhangOberserver()
            carDoing.downHero(tianshi)
        }))

        addGuanDeal(140) {
            over {
                fullBase()
            }
            chooseHero {
                upBase()
            }
            onStart {
                carDoing.downHero(wangjiang)
            }
        }

        changeZhuangbei(150) { qiangxi }
        changeZhuangbei(170) { longxin }


        guanDealList.add(
            GuanDeal(189, isOver = { curGuan > 189 },
                chooseHero = {
                    delay(500)
                    val ind = upAny(tieqi, zhanjiang, dianfa, jiaonv, tianshi, gugu, feiting)
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

        change2Tianshi3(
            190
        )

        guanDealList.add(GuanDeal(
            199,
            isOver = {
                curGuan >= 200
            },
            chooseHero = {
                deal199(this)
            },
            onGuanDealEnd = {
                step199 = 3
            },
        ).apply {
            des =
                "白球撞上后 按3 进入监听点名，期间可以按数字键盘进行下卡，点名结束后，可以按3重新进入白球卡阶段（下萨满补到3星）"
        })


        addGuan210(arrayListOf(tieqi, zhanjiang, gugu, jiaonv, tianshi, dianfa))
        curGuanDeal = guanDealList.first()
    }


    private var step199 = 1  // 1打白球阶段， 2点名阶段，
    private var count199 = 0

    private suspend fun deal199(heros: List<HeroBean?>): Int {
        while (step199 == 0) {
            delay(200)
        }
        if (step199 == 1) {
            if (tieqi.isFull()) {
                carDoing.downHero(tieqi)
            }
            if (tianshi.isFull()) {
                carDoing.downHero(tianshi)
            }

            if (tieqi.currentLevel < 3 || tianshi.currentLevel < 3) {
                if (tieqi.currentLevel < 3 && tianshi.currentLevel < 3) {
                    return heros.upAny(tieqi, tianshi)
                } else {

                    val level3Lowers = arrayListOf(tieqi, tianshi).filter {
                        it.currentLevel < 3
                    }
                    return heros.upAny(*level3Lowers.toTypedArray(), useGuang = false)

                }

            } else {
                //等点名
                XueLiang.observerXueDown()//掉血 等于 白球撞上了
                log("白球撞上了，进入step2")
                step199 = 2
                delay(300)//怕不同步，延迟300，满上萨满

                return heros.upAny(tieqi, tianshi)
            }
        } else if (step199 == 2) {

            var dianmingIndex = carDoing.getHB199Selected()

            var other = otherCarDoing.getHB199Selected()

            if (position199 > -1 || dianmingIndex > -1) {
                carDoing.downPosition(position199)
                carDoing.downPosition(dianmingIndex)
                count199++
                position199 = -1
            }
            if (position199 > -1 || dianmingIndex > -1 || other > -1) {
                count199++
//                GlobalScope.launch {
//                    delay(2000)
//                    step199 = 1
//                }
            }


            if (carDoing.hasAllOpenSpace() || carDoing.hasNotFull()) {
                return heros.upAny(zhanjiang, dianfa, jiaonv, tieqi, tianshi, gugu, feiting)
            } else {
                while (step199 == 2 && curGuan == 199) {
                    var dianmingIndex = carDoing.getHB199Selected()
                    other = otherCarDoing.getHB199Selected()
                    if (position199 > -1 || dianmingIndex > -1 || other > -1) {
                        count199++
//                        GlobalScope.launch {
//                            delay(2000)
//                            step199 = 1
//                        }
                    }

                    if (position199 > -1 || dianmingIndex > -1) {
                        carDoing.downPosition(position199)
                        carDoing.downPosition(dianmingIndex)
                        position199 = -1
                        return heros.upAny(zhanjiang, dianfa, jiaonv, tieqi, tianshi, gugu, feiting)
                    }
                    delay(100)
                }
            }

        }


        return -1
    }


    fun fullBase(): Boolean {
        return fulls(zhanjiang, tieqi, gugu, jiaonv, dianfa, tianshi, feiting)
    }

    fun List<HeroBean?>.upBase(zhuangbei: (() -> Boolean)? = null): Int {
        return upAny(zhanjiang, tieqi, gugu, jiaonv, dianfa, tianshi, feiting, zhuangbei = zhuangbei)
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

    override suspend fun onKeyDown(code: Int): Boolean {
        if (code == KeyEvent.VK_NUMPAD3) {//9强制改变waitting，防止waiting有逻辑错误不上卡
            if (guankaTask?.currentGuanIndex == 199 || guankaTask?.currentGuanIndex == 198) {

                step199 = if (step199 == 1) 2 else 1
                log("快捷键修改step199 为：${step199}")
                return true
            }
        }

        return super.onKeyDown(code)
    }
}