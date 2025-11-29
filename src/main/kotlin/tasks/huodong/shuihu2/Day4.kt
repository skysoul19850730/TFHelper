package tasks.huodong.shuihu2

import data.HeroBean
import data.HeroCreator
import kotlinx.coroutines.delay
import tasks.SimpleHeZuoHeroDoing

class Day4 : SimpleHeZuoHeroDoing() {
    val dianfa = HeroCreator.dianfa.create()
    val huoling = HeroCreator.huoling.create()
    val tuling = HeroCreator.tuling.create()
    val nvyao = HeroCreator.nvyao.create()
    val xiaoye = HeroCreator.xiaoye.create()
    val shenv = HeroCreator.shenv.create()
    val ganglie = HeroCreator.ganglie.create()
    val moqiu = HeroCreator.moqiu.create()
    val dapao = HeroCreator.dapao.create()
    val haiyao = HeroCreator.haiyao.create()

    override fun initHeroes() {
        heros = arrayListOf(dianfa, huoling, tuling, nvyao, xiaoye, shenv, ganglie, moqiu, dapao, haiyao)

        guanDealList.add(GuanDeal(0, isOver = {
            fulls(dianfa, huoling, nvyao, tuling, xiaoye, haiyao, dapao)
        }, chooseHero = {
            if (carDoing.openCount() < 2) {
                upAny(dianfa, huoling, tuling, xiaoye, dapao)
            } else {
                if (carDoing.openCount() < 4) {
                    var hs = carDoing.carps.filter {
                        it.mHeroBean != null
                    }.map {
                        it.mHeroBean!!
                    }
                    upAny(haiyao, nvyao, dapao, *hs.toTypedArray())
                } else {
                    upAny(dianfa, huoling, tuling, xiaoye, nvyao, haiyao, dapao)
                }
            }
        }))

        guanDealList.add(GuanDeal(29, isOver = { false }, chooseHero = {
            checkMoMu(this, true) { -1 }
        }))

        guanDealList.add(GuanDeal(49, onlyDoSomething = {
            carDoing.downHero(haiyao)
            carDoing.downHero(nvyao)
        }))
        guanDealList.add(GuanDeal(50, isOver = {
            false
        }, chooseHero = {
            checkMoMu(this, fulls(haiyao, nvyao)) { upAny(nvyao, haiyao) }
        }))

        guanDealList.add(
            GuanDeal(
                100,
                isOver = { false },
                chooseHero = {
                    checkMoMu(this, ganglie.isFull()) {
                        upAny(ganglie)
                    }
                },
                onGuanDealStart = {
                    carDoing.downHero(haiyao)
                })
        )
        guanDealList.add(
            GuanDeal(
                110,
                isOver = { false },
                chooseHero = {
                    checkMoMu(this, haiyao.isFull()) {
                        upAny(haiyao)
                    }
                },
                onGuanDealStart = {
                    carDoing.downHero(ganglie)
                })
        )

        guanDealList.add(
            GuanDeal(
                120,
                isOver = { false },
                chooseHero = {
                    checkMoMu(this, ganglie.isFull()) {
                        upAny(ganglie)
                    }
                },
                onGuanDealStart = {
                    carDoing.downHero(haiyao)
                })
        )
        guanDealList.add(
            GuanDeal(
                130,
                isOver = { false },
                chooseHero = {
                    checkMoMu(this, haiyao.isFull()) {
                        upAny(haiyao)
                    }
                },
                onGuanDealStart = {
                    carDoing.downHero(ganglie)
                })
        )
        curGuanDeal = guanDealList.get(0)
    }

    var lastMo = 0L
    private suspend fun checkMoMu(heros: List<HeroBean?>, isOver: Boolean, other: () -> Int): Int {
        val moIndex = heros.indexOf(moqiu)
        var index = doCheckMo(heros)
        if (index > -1) {
            return index
        } else {
            if (isOver) {
                var index = -1
                if (moIndex > -1) {//如果都满了，并且这会有木有魔，不用刷新，继续定时看状态是否使用就行
                    while (index < 0) {
                        delay(100)
                        index = doCheckMo(heros)
                    }
                    return index
                }else{
                    return -1
                }

            } else {
                return other.invoke()
            }
        }

    }

    private suspend fun doCheckMo(heros: List<HeroBean?>): Int {
        val moIndex = heros.indexOf(moqiu)
        if (moIndex > -1 && System.currentTimeMillis() - lastMo > 4500) {
            lastMo = System.currentTimeMillis()
            return moIndex
        }  else {
            return -1
        }
    }

}