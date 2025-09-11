package tasks.hanbing2.zhanjiang

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

class HB5ZGGHeroDoingZiQiang : BaseSimpleHBHeroDoing() {

    val isRenwu = false

    val zhanjiang = HeroCreator.zhanjiang2.create()
    val tieqi = HeroCreator.tieqi.create()
    val gugu = HeroCreator.gugu.create()
    val wangjiang = HeroCreator.wangjiang2.create()
    val sishen = HeroCreator.sishen2.create()
    val yuren = HeroCreator.yuren.create()
    val dianfa = HeroCreator.dianfa.create()
    val haiyao = HeroCreator.haiyaoy.create()
    val huanqiu = HeroCreator.huanqiu.create()

    val guangqiu = HeroBean(if(isRenwu) HanBingModel.renwuKa.value else "guangqiu", 40, needCar = false, compareRate = 0.95)


    override fun initHeroes() {
        super.initHeroes()
        heros = arrayListOf(zhanjiang, tieqi, gugu, wangjiang, sishen, yuren, dianfa, haiyao, huanqiu, guangqiu)
        heroDoNotDown129 = zhanjiang

        guanDealList.add(GuanDeal(0, isOver = {
           fulls(zhanjiang,haiyao,gugu,tieqi,dianfa)
        }, chooseHero = {
            if (zhanjiang.isGold()) {
               upAny(zhanjiang,haiyao,gugu,tieqi,dianfa)
            } else {
                upAny(zhanjiang)
            }
        }))

        guanDealList.add(GuanDeal(19, isOver = {
            fullBase() && longxin
        }, chooseHero = {
           carDoing.downHero(haiyao)
            upBase(zhuangbei = { longxin })
        }))

        guanDealList.add(GuanDeal(99, isOver = {
            false
        }, chooseHero = {
            while(zs99Clicked==0 && currentGuan()<100){
                delay(200)
            }
            if(zs99Clicked==1){
                carDoing.downHero(yuren)
                carDoing.downHero(tieqi)
                delay(10000)
                zs99Clicked=2
            }
            if(zs99Clicked==2){
                upAny(tieqi,yuren)
            }else -1

        }))

        guanDealList.add(GuanDeal(100, isOver = {
            fullBase()&&yandou
        }, chooseHero = {
            carDoing.downHero(haiyao)
            upBase(zhuangbei = { yandou })
        }))

        guanDealList.add(
            GuanDeal(109, isOver = { false },
                chooseHero = {
                    delay(300)
                    val ind =upBase()
                    if(ind<0 && !isRenwu){
                        upAny(guangqiu)
                    }else ind
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
                startChuanZhangOberserver2()
            })
        )
        guanDealList.add(GuanDeal(130, isOver = {
            fullBase() && yandou
        }, chooseHero = {
            upBase { yandou }
        }, onGuanDealStart = {stopChuanZhangOberserver()}))

        guanDealList.add(GuanDeal(135, isOver = {
            wangjiang.isInCar()
        }, chooseHero = {
            carDoing.downHero(yuren)
            upAny(wangjiang)
        }))

        guanDealList.add(GuanDeal(140, isOver = {
            fulls(haiyao)
        }, chooseHero = {
            carDoing.downHero(wangjiang)
            upAny(haiyao)
        }))

        guanDealList.add(GuanDeal(150, isOver = {
            fullBase()&&qiangxi
        }, chooseHero = {
            carDoing.downHero(haiyao)
            carDoing.downHero(wangjiang)
            upBase(zhuangbei = { qiangxi })
        }))

        guanDealList.add(GuanDeal(
            170,
            isOver = {wangjiang.isFull() && longxin},
            chooseHero = {
                carDoing.downHero(yuren)
                upAny(wangjiang, zhuangbei = {longxin})
            },
        ))


        guanDealList.add(
            GuanDeal(189, isOver = {currentGuan() > 189},
            chooseHero = {
                delay(300)
                val ind =upAny(tieqi,zhanjiang,wangjiang,sishen,dianfa,gugu)
                if(ind<0 && !isRenwu){
                    upAny(guangqiu)
                }else ind
//                checkHeroStarAndFull(this) { currentGuan() > 189 }
            })
        )

        guanDealList.add(GuanDeal(191, isOver = {
            fullBase()
        }, chooseHero = {
            if(needReCheckStar) {
                carDoing.reCheckStars()
                needReCheckStar = false
            }
            carDoing.downHero(wangjiang)
            upBase()
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

    private var baiqiuCount = 0
    private suspend fun deal199(heros: List<HeroBean?>): Int {
        while (step199 == 0) {
            delay(200)
        }
        if (step199 == 1) {
            if (tieqi.isFull()) {
                carDoing.downHero(tieqi)
            }

            if (tieqi.currentLevel < 3) {
                return heros.upAny(tieqi)
            } else {
                //等点名
                    XueLiang.observerXueDown()//掉血 等于 白球撞上了
                log("白球撞上了，进入step2")

                baiqiuCount++
//                if(baiqiuCount<3) {
//                    GlobalScope.launch {
//                        delay(32000)
//                        step199 = 1
//                    }
//                }
                    step199 = 2
                    delay(300)//怕不同步，延迟300，满上萨满


                //按3 会改成2，不监听掉血了，怕处于涨血状态判断不准。白球撞完按3
//                while (step199 == 1) {
//                    delay(100)
//                }


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


            if (carDoing.hasOpenSpace() || carDoing.hasNotFull() ) {
                return heros.upAny(zhanjiang, yuren, sishen, tieqi, dianfa, gugu)
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
                        return heros.upAny(zhanjiang, yuren, sishen, tieqi, dianfa, gugu)
                    }
                    delay(100)
                }
            }

        }


        return -1
    }

    private fun currentGuan():Int{
        return guankaTask?.currentGuanIndex?:0
    }



    private suspend fun shuaMu(heros:List<HeroBean?>,over:()->Boolean):Int{
        var index = heros.indexOf(dianfa)
        if (index > -1 && !isRenwu) {
            while (!over.invoke() && (!XueLiang.isMLess(0.9f))) {
                delay(50)
            }
            return index
        }
        return  -1
    }

    fun fullBase(): Boolean {
        return fulls(zhanjiang, tieqi, gugu, dianfa, sishen, yuren)
    }

    fun List<HeroBean?>.upBase(zhuangbei: (() -> Boolean)? = null): Int {
        return upAny(zhanjiang, tieqi, gugu, sishen, dianfa, yuren, zhuangbei = zhuangbei)
    }

    private suspend fun checkHeroStarAndFull(heros: List<HeroBean?>,over: () -> Boolean): Int {
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

    var zs99Clicked = 0
    override suspend fun onKeyDown(code: Int): Boolean {
        if(guankaTask?.currentGuanIndex ==98 || guankaTask?.currentGuanIndex == 99){
            if(code == KeyEvent.VK_NUMPAD3){
                zs99Clicked = 1
                return true
            }
        }

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