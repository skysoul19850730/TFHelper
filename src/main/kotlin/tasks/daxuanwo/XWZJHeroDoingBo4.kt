package tasks.daxuanwo

import data.HeroCreator
import kotlinx.coroutines.delay
import java.awt.event.KeyEvent

class XWZJHeroDoingBo4 : BaseSimpleXWHeroDoing() {
    val xuemo = HeroCreator.xuemo.create()
    val zhanjiang = HeroCreator.zhanjiang.create()
    val bingniao = HeroCreator.bingniao.create()
    val tianshi = HeroCreator.tianshi.create()
    val sishen = HeroCreator.sishen.create()
    val xiaoye = HeroCreator.xiaoye.create()

    val wangjiang = HeroCreator.wangjiang.create()
    val feiting = HeroCreator.feiting.create()
    val haiyao = HeroCreator.haiyao.create()


    val guangqiu = HeroCreator.guangqiu.create()


    override fun initHeroes() {
        super.initHeroes()
        auto59 = true
        heros = arrayListOf(
            sishen, bingniao, zhanjiang, haiyao, xuemo, wangjiang, feiting, tianshi, guangqiu, xiaoye
        )
        addGuanDeal(0) {
            over {
                fulls(zhanjiang, xuemo, sishen, feiting, bingniao, tianshi, xiaoye)
            }
            chooseHero {
                if (zhanjiang.isInCar()) {
                    if (feiting.isInCar()) {
                        upAny(zhanjiang, feiting, xuemo, sishen,bingniao,tianshi,xiaoye)
                    } else
                        upAny(feiting, zhanjiang, xuemo, sishen,bingniao,tianshi,xiaoye)
                } else upAny(zhanjiang, feiting)
            }
        }

        addGuanDealWithHerosFull(40, listOf(haiyao), listOf(xuemo))

        add49(feiting)

        //内部实际是52关开始
//        add50(listOf(zhanjiang, xuemo, feiting, bingniao, sishen, xiaoye, tianshi), listOf(bingniao, tianshi))

        addGuanDealWithHerosFull(50,listOf(zhanjiang, xuemo, feiting, bingniao, sishen, xiaoye, tianshi)
        , downHeros = listOf(haiyao)
        )

//        add69()
//        g69State = 1//跟波打，上来就是1  下卡，等掉血上去抗个无敌就可以了，就不需要快捷键上下卡了

        addGuanDealWithHerosFull(70, listOf(wangjiang), listOf(xuemo))

        addGuanDealWithHerosFull(82, listOf(xuemo), listOf(wangjiang))

//        addGuanDeal(80){
//            onlyDo {
//                carDoing.carps.forEach {
//                    it.downHero()
//                }
//            }
//        }

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