package tasks.daxuanwo

import data.HeroCreator
import kotlinx.coroutines.delay
import java.awt.event.KeyEvent

class XWZJHeroDoingZiQiang : BaseSimpleXWHeroDoing() {
    val yuren = HeroCreator.yuren.create()
    val zhanjiang = HeroCreator.zhanjiang.create()
    val xuemo = HeroCreator.xuemo.create()
    val feiting = HeroCreator.feiting.create()
    val gugu = HeroCreator.gugu.create()

    val tianshi = HeroCreator.tianshi.create()
    val huanqiu = HeroCreator.huanqiu.create()

    val tieqi = HeroCreator.tieqi.create()
    val guangqiu = HeroCreator.guangqiu.create()
    val xiaoye = HeroCreator.xiaoye.create()


    override fun initHeroes() {
        midHeros69 = listOf(yuren,tieqi)
        super.initHeroes()
        auto59 = true
        heros = arrayListOf(yuren, zhanjiang, xuemo, feiting, gugu, tianshi, huanqiu, tieqi, guangqiu, xiaoye)
        addGuanDeal(0) {
            over {
                fulls(zhanjiang, gugu, feiting, tianshi, xuemo, xiaoye, tieqi)
            }
            chooseHero {
                if (!zhanjiang.isInCar()) {
                    return@chooseHero upAny(zhanjiang, feiting)
                } else {
                    if (!xuemo.isInCar()) {
                        return@chooseHero upAny(xuemo, zhanjiang, feiting)
                    } else if (!tianshi.isInCar() || !tieqi.isInCar()) {
                        return@chooseHero upAny(tianshi, tieqi, xuemo, zhanjiang, feiting)
                    } else return@chooseHero upAny(
                        xuemo, tianshi, feiting,
                        zhanjiang, gugu, xiaoye, tieqi
                    )
                }
            }
        }

        changeZhuangbei(31,{yandou})

        addGuanDealWithHerosFull(46, listOf(yuren), listOf(tianshi))
        add49(feiting)

//        add50(listOf(zhanjiang,feiting,yuren,tianshi,gugu,xuemo,xiaoye), listOf(xiaoye,xuemo))
//        add69(auto = true)
        addGuanDealWithHerosFull(51, listOf(tianshi), listOf(yuren))
        changeZhuangbei(61){longxin}
        addGuanDealWithHerosFull(67, listOf(yuren), listOf(tianshi))

        add69()
        curGuanDeal = guanDealList.get(0)
    }

    override suspend fun onKeyDown(code: Int): Boolean {

        return super.onKeyDown(code)
    }
}