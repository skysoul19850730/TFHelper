package tasks.hanbing2.renwu

import data.HeroBean
import data.HeroCreator
import kotlinx.coroutines.delay
import org.apache.commons.compress.harmony.pack200.PackingUtils.log
import tasks.XueLiang
import tasks.hanbing2.BaseSimpleHBHeroDoing
import java.awt.event.KeyEvent

class HB5LRHeroDoingRW : BaseSimpleHBHeroDoing() {
    val isRenwu = false


    val houyi = HeroCreator.houyi.create()
    val dianfa = HeroCreator.dianfa.create()
    val shenv = HeroCreator.shenv.create()
    val hugong = HeroCreator.hugong.create()
    val tianshi = HeroCreator.tianshi.create()

    val gugu = HeroCreator.gugu.create()
    val sishen = HeroCreator.sishen.create()

    val gugong = HeroCreator.gugong.create()

    val niutou2 = HeroCreator.niutou.create()

    val haiyao = HeroCreator.haiyao.create()

    override fun initHeroes() {
        super.initHeroes()
        heros = arrayListOf(
            houyi,
            dianfa,
            gugu,
            niutou2,
            sishen,
            hugong,
            haiyao,
            tianshi,
            shenv,
            gugong
        )

        addGuanDeal(0) {
            over {
               fullBaseWithoutTianshi()
            }
            chooseHero {
               upBaseWithoutTianshi()
            }
        }

        var start = 27
        for (i in start..180) {
            var other: GuanDeal? = when (i) {

                108 -> GuanDeal(0, isOver = {
                    curGuan>109
                }, chooseHero = {
                    upBase()
                })

                110 -> GuanDeal(0, isOver = {
                    fullBaseWithoutTianshi()
                }, chooseHero = {
                    upBaseWithoutTianshi()
                })

                128 -> GuanDeal(0, isOver = {
                    fullBase()
                }, chooseHero = {
                   upBase()
                }, onGuanDealStart = {
//                    carDoing.downHero(baoku)
                    startChuanZhangOberserver2()
                })


                else -> null
            }
            if (i % 10 == 0) {

                change2Tianshi3(i, other)
            } else if (i % 10 == 8) {
                change2Tianshi4(i, other)
            }
        }


        guanDealList.add(
            GuanDeal(189, isOver = { curGuan > 189 },
                chooseHero = {
                    delay(500)
                    upBase()
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
//            if (dianfa.isFull()) {
//                carDoing.downHero(dianfa)
//            }
//            if(tianshi.isFull()){
//                carDoing.downHero(tianshi)
//            }

            if (dianfa.currentLevel < 3 || tianshi.currentLevel<3) {
                if(dianfa.currentLevel<3 && tianshi.currentLevel<3){
                    return heros.upAny(dianfa,tianshi)
                }else if(tianshi.currentLevel<3){
                    return heros.indexOf(tianshi)
                }else return heros.indexOf(dianfa)

            } else {
                //等点名
                XueLiang.observerXueDown()//掉血 等于 白球撞上了
                log("白球撞上了，进入step2")
                step199 = 2
                delay(300)//怕不同步，延迟300，满上萨满

                return heros.upAny(dianfa)
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
                return heros.upBase()
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
                        return heros.upBase()
                    }
                    delay(100)
                }
            }

        }


        return -1
    }


    fun fullBase(): Boolean {
        return fulls(houyi, dianfa, gugu, sishen, niutou2, tianshi)
    }

    fun List<HeroBean?>.upBase(): Int {
        return upAny(houyi, dianfa, gugu, sishen, niutou2, tianshi)
    }

    fun fullBaseWithoutTianshi(): Boolean {
        return fulls(houyi, dianfa, gugu, sishen, niutou2,)
    }

    fun List<HeroBean?>.upBaseWithoutTianshi(): Int {
        return upAny(houyi, dianfa, gugu, sishen, niutou2,)
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
                        if (get(otherIndex) == gugong) {
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
        var startTime = 0L
        addGuanDeal(guan) {
            over {
                tianshi.isFull() && otherGuanDeal?.isOver?.invoke() ?: true
            }
            chooseHero {
                val index = indexOf(tianshi)
                if (index > -1 && !tianshi.isFull() && !selfOver) {
                    delay(5000-(System.currentTimeMillis()-startTime))
                    selfOver=true
                    upAny(tianshi)
                } else {
                    otherGuanDeal?.chooseHero?.invoke(this) ?: upAny(tianshi)
                }
            }
            onStart {
                startTime = System.currentTimeMillis()
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