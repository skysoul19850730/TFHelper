package tasks.hanbing2.huoling

import data.HeroBean
import data.HeroCreator
import kotlinx.coroutines.delay
import tasks.XueLiang
import tasks.hanbing2.BaseSimpleHBHeroDoing

class HBBoHuolingHeroDoing : BaseSimpleHBHeroDoing() {

    val xiongmao = HeroCreator.xiongmao.create()
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
                    delay(300)
                    val ind = upBase()
                    if (ind < 0) {
                        upAny(guangqiu)
                    } else ind
                })
        )

        changeZhuangbei(110) { qiangxi }

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

        guanDealList.add(
            GuanDeal(189, isOver = { currentGuan() > 189 },
                chooseHero = {
                    delay(300)
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
        if (carDoing.hasOpenSpace() || carDoing.hasNotFull()) {
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


    private fun currentGuan(): Int {
        return guankaTask?.currentGuanIndex ?: 0
    }

    fun fullBase(): Boolean {
        return fulls(xiongmao, shuiling, huoling, gugong, nvyao, binggong, baoku)
    }

    fun List<HeroBean?>.upBase(zhuangbei: (() -> Boolean)? = null): Int {
        return upAny(xiongmao, shuiling, huoling, gugong, nvyao, binggong, baoku, zhuangbei = zhuangbei)
    }
}