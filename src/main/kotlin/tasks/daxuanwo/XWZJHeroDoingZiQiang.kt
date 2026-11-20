package tasks.daxuanwo

import data.HeroCreator
import kotlinx.coroutines.delay
import java.awt.event.KeyEvent

class XWZJHeroDoingZiQiang : BaseSimpleXWHeroDoing() {
    val nvwang = HeroCreator.nvwang.create()
    val zhanjiang = HeroCreator.zhanjiang.create()
    val xuemo = HeroCreator.xuemo.create()
    val feiting = HeroCreator.feiting.create()
    val gugu = HeroCreator.gugu.create()

    val hunqiu = HeroCreator.hunqiu.create()
    val huanqiu = HeroCreator.huanqiu.create()

    val tieqi = HeroCreator.tieqi.create()
    val guangqiu = HeroCreator.guangqiu.create()
    val xiaoye = HeroCreator.xiaoye.create()


    override fun initHeroes() {
        midHeros69 = listOf(tieqi,xuemo)
        super.initHeroes()
        auto59 = true
        heros = arrayListOf(nvwang, zhanjiang, xuemo, feiting, gugu, hunqiu, huanqiu, tieqi, guangqiu, xiaoye)
        addGuanDeal(0) {
            over {
                fulls(zhanjiang, gugu, feiting, hunqiu, xuemo, xiaoye, tieqi)
            }
            chooseHero {
                if (!zhanjiang.isInCar()) {
                    return@chooseHero upAny(zhanjiang, feiting)
                } else {
                    if (!feiting.isInCar()) {
                        return@chooseHero upAny(feiting, nvwang, zhanjiang)
                    } else if (!nvwang.isInCar()) {
                        return@chooseHero upAny(nvwang, zhanjiang, feiting)
                    } else if (!xuemo.isInCar() || !tieqi.isInCar()) {
                        return@chooseHero upAny(tieqi, xuemo, nvwang, zhanjiang, feiting)
                    } else return@chooseHero upAny(
                        xuemo, zhanjiang, nvwang, feiting,
                        gugu, xiaoye, tieqi
                    )
                }
            }
        }

        changeZhuangbei(31, { yandou })

        gudingShuaQiuTask("hunqiu",39,2500)

        add49WithQiu(feiting,hunqiu,2500)

//        add50(listOf(zhanjiang,feiting,nvwang,hunqiu,gugu,xuemo,xiaoye), listOf(xiaoye,xuemo))
//        add69(auto = true)
        addGuanDealWithHerosFull(51, listOf(feiting))
        changeZhuangbei(61) { longxin }
        add69()
        curGuanDeal = guanDealList.get(0)
    }

    override suspend fun onKeyDown(code: Int): Boolean {

        return super.onKeyDown(code)
    }
}