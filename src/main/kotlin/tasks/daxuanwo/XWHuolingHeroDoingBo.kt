package tasks.daxuanwo

import data.HeroCreator
import kotlinx.coroutines.delay
import java.awt.event.KeyEvent

class XWHuolingHeroDoingBo : BaseSimpleXWHeroDoing() {
    val shuiling = HeroCreator.shuiling.create()
    val huoling = HeroCreator.huoling.create()
    val fengling = HeroCreator.huoling.create()
//    val fengling = HeroCreator.fengling.create()
    val dianfa = HeroCreator.dianfa.create()
    val gugong = HeroCreator.gugong.create()
    val feiting = HeroCreator.feiting.create()

    val haiyao = HeroCreator.haiyao.create()
    val bingqi = HeroCreator.bingqi.create()

    val hunqiu = HeroCreator.hunqiu.create()
    val guangqiu = HeroCreator.guangqiu.create()


    var lastHun = 0L

    private suspend fun backHun(index: Int): Int {
        if (index > -1) {
            if (System.currentTimeMillis() - lastHun > 2000) {
                return index
            } else {
                delay(2000 - (System.currentTimeMillis() - lastHun))
                return index
            }
        }
        return index
    }

    override fun initHeroes() {
        super.initHeroes()

        g49StartBoss = {
            var index = it.indexOf(hunqiu)
            backHun(index)
        }

        heros = arrayListOf(
            haiyao, shuiling, huoling, bingqi, dianfa, gugong, feiting, hunqiu, guangqiu, fengling
        )

        addGuanDealWithHerosFull(0, listOf(huoling,dianfa,shuiling,fengling,gugong,haiyao,feiting))


        add49(feiting)

        add50(listOf(huoling,dianfa,shuiling,fengling,gugong,bingqi,feiting), listOf(fengling,shuiling))

        add69()


        curGuanDeal = guanDealList.get(0)
    }

}