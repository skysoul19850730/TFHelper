package tasks.hanbing2.zhanjiang

import data.HeroBean
import data.HeroCreator
import kotlinx.coroutines.delay
import tasks.hanbing2.BaseSimpleHBHeroDoing
import java.awt.event.KeyEvent

class HB5ZHeroDoingBo2 : BaseSimpleHBHeroDoing() {


    val niutou2 = HeroCreator.niutou2.create()
    val tieqi = HeroCreator.tieqi.create()
    val tianshi = HeroCreator.tianshi.create()
    val xiaoye = HeroCreator.xiaoye.create()
    val sishen = HeroCreator.sishen2.create()
    val wangjiang2 = HeroCreator.wangjiang2.create()
    val huanqiu = HeroCreator.huanqiu.create()
    val muqiu = HeroCreator.muqiu.create()
    val baoku = HeroCreator.dapao.create()
    val guangqiu = HeroCreator.guangqiu.create()

    override fun initHeroes() {
        super.initHeroes()
        heros = arrayListOf(niutou2, tieqi, tianshi, xiaoye, sishen, wangjiang2, huanqiu, muqiu, baoku, guangqiu)

        guanDealList.add(GuanDeal(0, isOver = {
            fullBase() && longxin
        }, chooseHero = {
            upBase(zhuangbei = { longxin })
        }))


        guanDealList.add(GuanDeal(100, isOver = {
            fullBase() && yandou
        }, chooseHero = {
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
                startChuanZhangOberserver()
            })
        )
        guanDealList.add(GuanDeal(130, isOver = {
            fullBase() && yandou
        }, chooseHero = {
            upBase { yandou }
        }, onGuanDealStart = { stopChuanZhangOberserver() }))

        changeZhuangbei(150) { qiangxi }
        changeZhuangbei(181) { longxin }


        guanDealList.add(
            GuanDeal(189, isOver = { currentGuan() > 189 },
                chooseHero = {
                    delay(500)
                    val ind = upBase()
                    if (ind < 0) {
                        upAny(guangqiu)
                    } else ind
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
                currentGuan() > 200
            },
            chooseHero = {
                deal199(this)
            },
        ).apply {
            des =
                "白球撞上后 按3 进入监听点名，期间可以按数字键盘进行下卡，点名结束后，可以按3重新进入白球卡阶段（下萨满补到3星）,最后一次按3进行点名，点名完会上满卡，打崩坏阶段，" +
                        "如果不小心按3进了打白球的话，就再按3进点名补满卡即可，更新后，这里只需要白球后点一次3进点名就可以了，不会出第二个白球了，直接进入崩坏"
        })

        guanDealList.add(GuanDeal(
            startGuan = 209,
            isOver = {
                false
            },
            chooseHero = {
                deal210(this)
            },
        ).apply
        {
            des = "哪里被标记为黑洞就点哪里"
        })

        curGuanDeal = guanDealList.first()
    }

    private var step199 = 2  // 1打白球阶段， 2点名阶段，
    private var count199 = 0

    private suspend fun deal199(heros: List<HeroBean?>): Int {
        while (step199 == 0 && currentGuan() < 200) {
            delay(200)
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
            while (step199 == 2 && currentGuan() < 200) {
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

    var click210Pos = -1
    var lastClick210Pos = -1
    var upList210 = arrayListOf(niutou2, sishen, tieqi, xiaoye, tianshi, wangjiang2)

    private suspend fun deal210(heros: List<HeroBean?>): Int {

        if (!upList210.all {
                it.isFull()
            }) {
            return heros.upAny(*upList210.toTypedArray())
        }

        while (click210Pos == -1 || lastClick210Pos == click210Pos) {
            delay(200)
        }
        val carPos = carDoing.carps.get(click210Pos)
        val hero = carPos.mHeroBean
        if (hero != null) {
            carDoing.resetHero(hero)
            carPos.isUnEnable = true

            val lastH = upList210.last()
            carDoing.downHero(lastH)
            upList210.remove(lastH)
            lastClick210Pos = click210Pos
            return heros.upAny(*upList210.toTypedArray())
        } else {
            lastClick210Pos = click210Pos
        }
        return -1

    }


    var zs99Clicked = 0
    override suspend fun onKeyDown(code: Int): Boolean {
        if (guankaTask?.currentGuanIndex == 98 || guankaTask?.currentGuanIndex == 99) {
            if (code == KeyEvent.VK_NUMPAD3) {
                zs99Clicked = 1
                return true
            }
        }
        if (code == KeyEvent.VK_NUMPAD3) {//9强制改变waitting，防止waiting有逻辑错误不上卡
            if (guankaTask?.currentGuanIndex == 199 || guankaTask?.currentGuanIndex == 198) {

                step199 = if (step199 == 1) 2 else 1

                return true
            }
        }

        if (guankaTask?.currentGuanIndex == 209 || guankaTask?.currentGuanIndex == 208) {
            click210Pos = when (code) {
                KeyEvent.VK_NUMPAD2 -> 0
                KeyEvent.VK_NUMPAD1 -> 1
                KeyEvent.VK_NUMPAD5 -> 2
                KeyEvent.VK_NUMPAD4 -> 3
                KeyEvent.VK_NUMPAD8 -> 4
                KeyEvent.VK_NUMPAD7 -> 5
                KeyEvent.VK_NUMPAD0 -> 6
                else -> -1
            }

            if (click210Pos > -1) {
                return true
            }
        }


        return super.onKeyDown(code)
    }


    private fun currentGuan(): Int {
        return guankaTask?.currentGuanIndex ?: 0
    }


    fun fullBase(): Boolean {
        return fulls(niutou2, tieqi, tianshi, wangjiang2, sishen, xiaoye, baoku)
    }

    fun List<HeroBean?>.upBase(zhuangbei: (() -> Boolean)? = null): Int {
        return upAny(niutou2, tieqi, tianshi, sishen, wangjiang2, xiaoye, baoku, zhuangbei = zhuangbei)
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