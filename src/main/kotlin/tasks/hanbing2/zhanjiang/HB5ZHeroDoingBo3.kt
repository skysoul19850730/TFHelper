package tasks.hanbing2.zhanjiang

import data.HeroBean
import data.HeroCreator
import kotlinx.coroutines.delay
import org.apache.commons.compress.harmony.pack200.PackingUtils
import tasks.XueLiang
import tasks.Zhuangbei
import tasks.hanbing2.BaseSimpleHBHeroDoing
import java.awt.event.KeyEvent

class HB5ZHeroDoingBo3 : BaseSimpleHBHeroDoing() {


    val niutou = HeroCreator.niutou.create()
    val tieqi = HeroCreator.tieqi.create()
    val tianshi = HeroCreator.tianshi.create()
    val zhanjiang = HeroCreator.zhanjiang.create()
    val dianfa = HeroCreator.dianfa.create()
    val jiaonv = HeroCreator.jiaonv.create()

    val feiting = HeroCreator.feiting.create()

    val wangjiang = HeroCreator.wangjiang.create()
    val huanqiu = HeroCreator.huanqiu.create()
    val guangqiu = HeroCreator.guangqiu.create()

    override fun initHeroes() {
        super.initHeroes()
        heros = arrayListOf(niutou, tieqi, tianshi, zhanjiang, dianfa, wangjiang, huanqiu, jiaonv, feiting, guangqiu)

        guanDealList.add(GuanDeal(0, isOver = {
            fulls(zhanjiang, niutou, jiaonv, feiting)
        }, chooseHero = {
            if (zhanjiang.isInCar()) {
                upAny(zhanjiang, niutou, jiaonv, feiting)
            } else upAny(zhanjiang)
        }))

        addGuanDeal(25) {
            over { fulls(zhanjiang, niutou, jiaonv, feiting, tieqi, dianfa, wangjiang) && longxin }
            chooseHero {
                upAny(zhanjiang, niutou, jiaonv, feiting, tieqi, dianfa, wangjiang, zhuangbei = {longxin})
            }
        }

        guanDealList.add(GuanDeal(95, isOver = {
            fullBase() && yandou
        }, chooseHero = {
            carDoing.downHero(wangjiang)
            upBase(zhuangbei = { yandou })
        }))


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
            fullBase() && yandou
        }, chooseHero = {
            upBase { yandou }
        }, onGuanDealStart = { stopChuanZhangOberserver() }))

        addGuanDeal(133){
            over {
                wangjiang.isInCar()
            }
            chooseHero {
                carDoing.downHero(tianshi)
                upAny(wangjiang)
            }
        }

        addGuanDeal(140){
            over {
                fullBase()
            }
            chooseHero{
                carDoing.downHero(wangjiang)
                upBase()
            }
        }

        changeZhuangbei(150){qiangxi}

        addGuanDeal(159) {
            over {
                curGuan > 159
            }

            chooseHero {
                while (needZhuangbei == Zhuangbei.curZhuangBei && curGuan < 160) {
                    delay(100)
                }
                if (curGuan < 160) {
                    zhuangbei { yandou }
                } else -1
            }

            onStart {
                startXiongMaoOberser()
            }
            onEnd {
                xiongmaoOberserver = false
            }
        }

        changeZhuangbei(160) { qiangxi }

        changeZhuangbei(181) { longxin }


        guanDealList.add(
            GuanDeal(189, isOver = { curGuan > 189 },
                chooseHero = {
                    delay(500)
                    val ind = upBase()
                    if (ind < 0) {
                        upAny(guangqiu)
                    } else ind
                },
                onGuanDealStart = {//废话时间
                    delay(10000)
                }
            )
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

        addGuanDeal(195){
            over {
                tianshi.currentLevel == 3
            }
            chooseHero {
                if(tianshi.isFull()){
                    carDoing.downHero(tianshi)
                }
                upAny(tianshi)
            }
        }

        guanDealList.add(GuanDeal(
            199,
            isOver = {
                curGuan > 200
            },
            chooseHero = {
                deal199(this)
            },
        ).apply {
            des =
                "白球撞上后 按3 进入监听点名，期间可以按数字键盘进行下卡，点名结束后，可以按3重新进入白球卡阶段（下萨满补到3星）,最后一次按3进行点名，点名完会上满卡，打崩坏阶段，" +
                        "如果不小心按3进了打白球的话，就再按3进点名补满卡即可，更新后，这里只需要白球后点一次3进点名就可以了，不会出第二个白球了，直接进入崩坏"
        })

        addGuan210(arrayListOf( jiaonv, tieqi, zhanjiang,niutou, tianshi, dianfa))

        curGuanDeal = guanDealList.first()
    }

    var needZhuangbei = Zhuangbei.QIANGXI
    override fun onXiongMaoQiuGot(qiu: String) {
        super.onXiongMaoQiuGot(qiu)
        if (qiu == "fs") {
            needZhuangbei = Zhuangbei.YANDOU

            waiting = false
        }
    }

    private var step199 = 1  // 1打白球阶段， 2点名阶段，
    private var count199 = 0

    private suspend fun deal199(heros: List<HeroBean?>): Int {
        while (step199 == 0 && curGuan < 200) {
            delay(200)
        }

        if(step199==1){
            //等点名
            XueLiang.observerXueDown()//掉血 等于 白球撞上了
            PackingUtils.log("白球撞上了，进入step2")
            step199 = 2
            delay(300)//怕不同步，延迟300，满上萨满

            return heros.upAny( tianshi)
        }


        var dianmingIndex = carDoing.getHB199Selected()
        if (position199 > -1 || dianmingIndex > -1) {
            carDoing.downPosition(position199)
            carDoing.downPosition(dianmingIndex)
            count199++
            position199 = -1
        }
        if (carDoing.hasAllOpenSpace() || carDoing.hasNotFull()) {
            return heros.upBase()
        } else {
            while (step199 == 2 && curGuan < 200) {
                var dianmingIndex = carDoing.getHB199Selected()
                if (position199 > -1 || dianmingIndex > -1) {
                    carDoing.downPosition(position199)
                    carDoing.downPosition(dianmingIndex)
                    position199 = -1
                    count199++
                    return heros.upBase()
                }
                delay(100)
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


    fun fullBase(): Boolean {
        return fulls(niutou, tieqi, tianshi, jiaonv, dianfa, zhanjiang, feiting)
    }

    fun List<HeroBean?>.upBase(zhuangbei: (() -> Boolean)? = null): Int {
        return upAny(niutou, tieqi, tianshi, dianfa, jiaonv, zhanjiang, feiting, zhuangbei = zhuangbei)
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