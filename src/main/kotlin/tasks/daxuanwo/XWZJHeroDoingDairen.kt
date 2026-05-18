package tasks.daxuanwo

import data.HeroBean
import data.HeroCreator
import kotlinx.coroutines.delay
import java.awt.event.KeyEvent

class XWZJHeroDoingDairen : BaseSimpleXWHeroDoing() {
    val tieqi = HeroCreator.tieqi.create()
    val zhanjiang = HeroCreator.zhanjiang.create()
    val sishen = HeroCreator.sishen.create()
    val yuren = HeroCreator.yuren.create()
    val tianshi = HeroCreator.tianshi.create()
    val xiaoye = HeroCreator.xiaoye.create()
    val feiting = HeroCreator.feiting.create()

    val huanqiu = HeroCreator.huanqiu.create()
    val wangjiang = HeroCreator.wangjiang.create()


    val guangqiu = HeroCreator.guangqiu.create()


    override fun initHeroes() {
        super.initHeroes()
        auto59 = true

        heros = arrayListOf(
            sishen, tieqi, zhanjiang, wangjiang, huanqiu, yuren, feiting, tianshi, guangqiu, xiaoye
        )
        addGuanDeal(0) {
            over {
                fulls(zhanjiang, tieqi,tianshi,yuren,xiaoye,sishen, feiting)
            }
            chooseHero {
                if (zhanjiang.isInCar()) {
                    if (feiting.isInCar()) {
                        upAny(zhanjiang, feiting,tieqi,tianshi,yuren, xiaoye, sishen)
                    } else
                        upAny(feiting, zhanjiang,tieqi,tianshi,yuren, xiaoye, sishen)
                } else upAny(zhanjiang,feiting)
            }
        }

        add49(feiting)


        //内部实际是52关开始
        add50(listOf(zhanjiang, tieqi, feiting, sishen, xiaoye,tianshi, yuren),listOf(tianshi, yuren))

        add69()

        addGuanDealWithHerosFull(70, listOf(wangjiang), listOf(yuren))

        curGuanDeal = guanDealList.get(0)
    }


    override suspend fun onKeyDown(code: Int): Boolean {

        if (code == KeyEvent.VK_NUMPAD0) {
            if (curGuan == 49) {
                g49 = 1
                return true
            }
        }

        return super.onKeyDown(code)
    }
}