package tasks.hanbing2.zhanjiang

import data.HeroBean
import data.HeroCreator
import kotlinx.coroutines.delay
import tasks.XueLiang
import tasks.hanbing2.BaseSimpleHBHeroDoing
import java.awt.event.KeyEvent

class HB5ZHeroDoingBo : BaseSimpleHBHeroDoing() {

    val isRenwu = false

    val zhanjiang = HeroCreator.zhanjiang2.create()
    val tieqi = HeroCreator.tieqi.create()
    val saman = HeroCreator.saman2.create()
    val wangjiang = HeroCreator.wangjiang2.create()
    val sishen = HeroCreator.sishen2.create()
    val kuangjiang = HeroCreator.kuangjiang.create()
    val moqiu = HeroCreator.moqiu.create()
    val dianfa = HeroCreator.dianfa.create()
    val baoku = HeroCreator.baoku.create()
    val guangqiu = HeroCreator.guangqiu.create()

    override fun initHeroes() {
        super.initHeroes()
        heros = arrayListOf(zhanjiang, tieqi, saman, wangjiang, sishen, kuangjiang, moqiu, dianfa, baoku, guangqiu)
        heroDoNotDown129 = zhanjiang

        guanDealList.add(GuanDeal(0, isOver = {
            fullBase()
        }, chooseHero = {
            if (zhanjiang.isGold()) {
                upBase()
            } else {
                upAny(zhanjiang)
            }
        }))

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
                startChuanZhangOberserver()
            })
        )
        guanDealList.add(GuanDeal(130, isOver = {
            fullBase()
        }, chooseHero = {
            upBase()
        }, onGuanDealStart = { stopChuanZhangOberserver() }))

        guanDealList.add(GuanDeal(135, isOver = {
            wangjiang.isInCar()
        }, chooseHero = {
            carDoing.downHero(sishen)
            upAny(wangjiang)
        }
        ))

        guanDealList.add(GuanDeal(140, isOver = {
            sishen.isFull()
        }, chooseHero = {
            carDoing.downHero(wangjiang)
            upAny(sishen)
        }))


        guanDealList.add(GuanDeal(170, isOver = {
            wangjiang.isInCar()
        }, chooseHero = {
            carDoing.downHero(sishen)
            upAny(wangjiang)
        }
        ))
        guanDealList.add(GuanDeal(180, isOver = {
            sishen.isFull()
        }, chooseHero = {
            carDoing.downHero(wangjiang)
            upAny(sishen)
        }))

        guanDealList.add(
            GuanDeal(189, isOver = { currentGuan() > 189 },
                chooseHero = {
//                delay(300)
//                val ind =upBase()
//                if(ind<0){
//                    upAny(guangqiu)
//                }else ind
                    checkHeroStarAndFull(this) { currentGuan() > 189 }
                })
        )

        guanDealList.add(GuanDeal(191, isOver = {
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


        guanDealList.add(GuanDeal(199, isOver = {
            false
        },
            chooseHero = {
                deal199(this)
            },
        ).apply {
            des="白球撞上后 按3 进入监听点名，期间可以按数字键盘进行下卡，点名结束后，可以按3重新进入白球卡阶段（下萨满补到3星）"
        })


        curGuanDeal = guanDealList.first()
    }

    private var step199 = 1  // 1打白球阶段， 2点名阶段，
    private var count199=0

    private suspend fun deal199(heros: List<HeroBean?>): Int {
        while (step199 == 0) {
            delay(200)
        }
        if (step199 == 1) {
            carDoing.downHero(kuangjiang)
            if (!wangjiang.isFull()) {
                return heros.upAny(wangjiang)
            } else {
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
                    while(step199 == 1){
                        delay(100)
                    }

                    return heros.upAny(saman)
                }
            }
        } else if (step199 == 2) {

            var dianmingIndex = carDoing.getHB199Selected()
            if (position199 > -1 || dianmingIndex > -1) {
                carDoing.downPosition(position199)
                carDoing.downPosition(dianmingIndex)
                count199++
                position199 = -1
            }
            if (carDoing.hasOpenSpace() || carDoing.hasNotFull()) {
                return heros.upAny(zhanjiang, dianfa, sishen, tieqi, wangjiang, saman, baoku)
            } else {
                while (step199 == 2) {
                    var dianmingIndex = carDoing.getHB199Selected()
                    if (position199 > -1 || dianmingIndex > -1) {
                        carDoing.downPosition(position199)
                        carDoing.downPosition(dianmingIndex)
                        position199 = -1
                        count199++
                        return heros.upAny(zhanjiang, dianfa, sishen, tieqi, wangjiang, saman, baoku)
                    }
                    delay(100)
                }
            }

        }


        return -1
    }

    override suspend fun onKeyDown(code: Int): Boolean {
        if (code == KeyEvent.VK_NUMPAD3) {//9强制改变waitting，防止waiting有逻辑错误不上卡
            if (guankaTask?.currentGuanIndex == 199 ||guankaTask?.currentGuanIndex == 198 ) {

                step199 = if(step199 == 1) 2 else 1

                return true
            }
        }


        return super.onKeyDown(code)
    }


    private fun currentGuan(): Int {
        return guankaTask?.currentGuanIndex ?: 0
    }


    fun fullBase(): Boolean {
        return fulls(zhanjiang, tieqi, saman, dianfa, sishen, kuangjiang, baoku)
    }

    fun List<HeroBean?>.upBase(zhuangbei: (() -> Boolean)? = null): Int {
        return upAny(zhanjiang, tieqi, saman, sishen, dianfa, kuangjiang, baoku, zhuangbei = zhuangbei)
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