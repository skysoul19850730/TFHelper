package tasks.hanbing2.zhanjiang

import data.HeroBean
import data.HeroCreator
import kotlinx.coroutines.delay
import org.apache.commons.compress.harmony.pack200.PackingUtils.log
import tasks.XueLiang
import tasks.hanbing2.BaseSimpleHBHeroDoing
import ui.zhandou.hanbing.HanBingModel
import java.awt.event.KeyEvent

class HBZTHeroDoingZiQiang : BaseSimpleHBHeroDoing() {
    val isRenwu = false


    val zhanjiang = HeroCreator.zhanjiangb.create()
    val tieqi = HeroCreator.tieqi.create()
    val gugu = HeroCreator.gugu.create()
    val wangjiang = HeroCreator.wangjiang2.create()
    val sishen = HeroCreator.sishen2.create()
    val yuren = HeroCreator.yuren.create()
    val baoku = HeroCreator.baoku.create()
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
            sishen,
            yuren,
            baoku,
            tianshi,
            huanqiu,
            guangqiu
        )
        heroDoNotDown129 = zhanjiang

        addGuanDeal(0) {
            over {
               zhanjiang.currentLevel==3
            }
            chooseHero {
                upAny(zhanjiang, gugu, sishen, baoku)
            }
        }

        addGuanDeal(19) {
            over {
                fulls(zhanjiang, gugu, sishen, tieqi, wangjiang, baoku)&&longxin
            }
            chooseHero {
                upAny(zhanjiang, gugu, sishen, tieqi, wangjiang, baoku, zhuangbei = {longxin})
            }
        }

        addGuanDeal(29) {
            over {
                fulls(yuren)&&longxin
            }
            chooseHero {
                upAny(yuren, zhuangbei = {longxin})
            }
        }
        var start = 40
        for (i in start..180) {
            var other: GuanDeal? = when (i) {

                start -> GuanDeal(0, onGuanDealStart = { carDoing.downHero(wangjiang) })

                100 -> GuanDeal(0, isOver = {
                    fullBaseWithoutTianshi() && yandou
                }, chooseHero = {
                    upBaseWithoutTianshi(zhuangbei = { yandou })
                })

                108 -> GuanDeal(0, isOver = {
                    curGuan>109
                }, chooseHero = {
                    upBase()
                })

                110 -> GuanDeal(0, isOver = {
                    fullBaseWithoutTianshi() && qiangxi
                }, chooseHero = {
                    upBaseWithoutTianshi(zhuangbei = { qiangxi })
                })

                128 -> GuanDeal(0, isOver = {
                    fulls(zhanjiang,gugu,tianshi,sishen,yuren,tianshi,baoku)
                }, chooseHero = {
                    upAny(zhanjiang,gugu,tianshi,sishen,yuren,tianshi,baoku,zhuangbei = { qiangxi })
                }, onGuanDealStart = {
//                    carDoing.downHero(baoku)
                    startChuanZhangOberserver2()
                })

                130 -> GuanDeal(0, isOver = {
                    fullBaseWithoutTianshi() && yandou
                }, chooseHero = {
                    upBaseWithoutTianshi(zhuangbei = { yandou })
                }, onGuanDealStart = { stopChuanZhangOberserver() })

                140 -> GuanDeal(0, isOver = {
                    fullBaseWithoutTianshi() && yandou
                }, chooseHero = {
                    upBaseWithoutTianshi(zhuangbei = { yandou })
                }, onGuanDealStart = {
                    carDoing.downHero(wangjiang)
                }
                )

                150 -> GuanDeal(0, isOver = {
                    qiangxi
                }, chooseHero = {
                    zhuangbei { qiangxi }
                })

                170 -> GuanDeal(0, isOver = {
                    longxin
                }, chooseHero = {
                    zhuangbei { longxin }
                })

                else -> null
            }
            if (i % 10 == 0) {

                change2Tianshi3(i, other)
                if (i == 130) {
                    guanDealList.add(GuanDeal(135, isOver = {
                        wangjiang.isFull()
                    }, chooseHero = {
                        carDoing.downHero(sishen)
                        upAny(wangjiang, useGuang = false)
                    }))
                }
            } else if (i % 10 == 8) {
                change2Tianshi4(i, other)
            }
        }


        guanDealList.add(
            GuanDeal(189, isOver = { curGuan > 189 },
                chooseHero = {
                    delay(500)
                    val ind = upAny(tieqi, zhanjiang, yuren, sishen, tianshi, gugu, baoku)
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
                curGuan>=200
            },
            chooseHero = {
                deal199(this)
            }, onGuanDealEnd = {
                  step199=3
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
            if (tieqi.isFull()) {
                carDoing.downHero(tieqi)
            }
            if(tianshi.isFull()){
                carDoing.downHero(tianshi)
            }

            if (tieqi.currentLevel < 3 || tianshi.currentLevel<3) {
                if(tieqi.currentLevel<3 && tianshi.currentLevel<3){
                    return heros.upAny(tieqi,tianshi)
                }else if(tianshi.currentLevel<3){
                    return heros.indexOf(tianshi)
                }else return heros.indexOf(tieqi)

            } else {
                //等点名
                XueLiang.observerXueDown()//掉血 等于 白球撞上了
                log("白球撞上了，进入step2")
                step199 = 2
                delay(300)//怕不同步，延迟300，满上萨满

                return heros.upAny(tieqi)
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
            if (position199 > -1 || dianmingIndex > -1 || other>-1) {
                count199++
//                GlobalScope.launch {
//                    delay(2000)
//                    step199 = 1
//                }
            }


            if (carDoing.hasAllOpenSpace() || carDoing.hasNotFull() ) {
                return heros.upAny(zhanjiang, yuren, sishen, tieqi, tianshi, gugu,baoku)
            } else {
                while (step199 == 2) {
                    var dianmingIndex = carDoing.getHB199Selected()
                    other = otherCarDoing.getHB199Selected()
                    if (position199 > -1 || dianmingIndex > -1 || other>-1) {
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
                        return heros.upAny(zhanjiang, yuren, sishen, tieqi, tianshi, gugu,baoku)
                    }
                    delay(100)
                }
            }

        }


        return -1
    }


    fun fullBase(): Boolean {
        return fulls(zhanjiang, tieqi, gugu, sishen, yuren, tianshi, baoku)
    }

    fun List<HeroBean?>.upBase(zhuangbei: (() -> Boolean)? = null): Int {
        return upAny(zhanjiang, tieqi, gugu, sishen, yuren, tianshi, baoku, zhuangbei = zhuangbei)
    }

    fun fullBaseWithoutTianshi(): Boolean {
        return fulls(zhanjiang, tieqi, gugu, sishen, yuren, baoku)
    }

    fun List<HeroBean?>.upBaseWithoutTianshi(zhuangbei: (() -> Boolean)? = null): Int {
        return upAny(zhanjiang, tieqi, gugu, sishen, yuren, baoku, zhuangbei = zhuangbei)
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
                    selfOver=true
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