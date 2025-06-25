package tasks.huodong.shuihu2

import data.HeroCreator
import tasks.SimpleHeZuoHeroDoing

class Day2 : SimpleHeZuoHeroDoing() {
    val gugong = HeroCreator.gugong.create()
    val hugong = HeroCreator.hugong.create()
    val nvyao = HeroCreator.nvyao.create()
    val xiaoye = HeroCreator.xiaoye.create()
    val shenv = HeroCreator.shenv.create()
    val yuren = HeroCreator.yuren.create()
    val haiyao = HeroCreator.haiyao.create()
    val miyun = HeroCreator.miyun.create()
    val fenghuang = HeroCreator.fenghuang.create()
    val huanqiu = HeroCreator.huanqiu.create()

    override fun initHeroes() {
        heros = arrayListOf(gugong, hugong, nvyao, xiaoye, shenv, yuren, haiyao, miyun, fenghuang, huanqiu)

        guanDealList.add(GuanDeal(0, isOver = {
            fulls(gugong, hugong, xiaoye, nvyao, miyun, fenghuang) && longxin
        }, chooseHero = {
            if (carDoing.openCount() < 2) {
                upAny(xiaoye, nvyao, miyun, fenghuang, zhuangbei = { longxin })
            } else {
                if (carDoing.openCount() < 4) {
                    var hs = carDoing.carps.filter {
                        it.mHeroBean != null
                    }.map {
                        it.mHeroBean!!
                    }
                    upAny(gugong, hugong, *hs.toTypedArray(), zhuangbei = { longxin })
                } else {
                    upAny(gugong, hugong, nvyao, miyun, fenghuang, xiaoye, zhuangbei = { longxin })
                }
            }
        }))

        guanDealList.add(GuanDeal(48, onlyDoSomething = {
            carDoing.downHero(gugong)
            carDoing.downHero(hugong)
        }))
        guanDealList.add(GuanDeal(50, isOver = {
            fulls(hugong, gugong)
        }, chooseHero = {
            upAny(gugong, hugong)
        }))

        guanDealList.add(
            GuanDeal(
                100,
                isOver = { hugong.isFull() && qiangxi },
                chooseHero = { upAny(hugong, zhuangbei = { qiangxi }) },
                onGuanDealStart = {
                    carDoing.downHero(haiyao)
                })
        )
        guanDealList.add(
            GuanDeal(
                110,
                isOver = { haiyao.isFull() && yandou },
                chooseHero = { upAny(haiyao, zhuangbei = { yandou }) },
                onGuanDealStart = {
                    carDoing.downHero(hugong)
                })
        )

        guanDealList.add(
            GuanDeal(
                120,
                isOver = { hugong.isFull() && qiangxi },
                chooseHero = { upAny(hugong, zhuangbei = { qiangxi }) },
                onGuanDealStart = {
                    carDoing.downHero(haiyao)
                })
        )
        guanDealList.add(
            GuanDeal(
                130,
                isOver = { haiyao.isFull() && yandou },
                chooseHero = { upAny(haiyao, zhuangbei = { yandou }) },
                onGuanDealStart = {
                    carDoing.downHero(hugong)
                })
        )
        guanDealList.add(
            GuanDeal(
                140,
                isOver = { hugong.isFull() && qiangxi },
                chooseHero = { upAny(hugong, zhuangbei = { qiangxi }) },
                onGuanDealStart = {
                    carDoing.downHero(haiyao)
                })
        )
        curGuanDeal = guanDealList.get(0)
    }
}