package tasks.daxuanwo

import data.HeroCreator
import kotlinx.coroutines.delay
import java.awt.event.KeyEvent

class XWZJHeroDoingBo4 : BaseSimpleXWHeroDoing() {
    val niutou = HeroCreator.niutou.create()
    val zhanjiang = HeroCreator.zhanjiang.create()
    val tieqi = HeroCreator.tieqi.create()
    val tianshi = HeroCreator.tianshi.create()
    val sishen = HeroCreator.sishen.create()
    val yuren = HeroCreator.yuren.create()
    
    val wangjiang = HeroCreator.wangjiang.create()
    val feiting = HeroCreator.feiting.create()
    val haiyao = HeroCreator.haiyao.create()

   
    val guangqiu = HeroCreator.guangqiu.create()


    override fun initHeroes() {
        super.initHeroes()
        auto59 = true
        heros = arrayListOf(
            sishen, tieqi, zhanjiang, haiyao, niutou, wangjiang, feiting, tianshi, guangqiu, yuren
        )
        addGuanDeal(0) {
            over {
                fulls(zhanjiang, niutou,sishen, feiting)
            }
            chooseHero {
                if (zhanjiang.isInCar()) {
                    if(feiting.isInCar()){
                        upAny(zhanjiang,feiting  ,niutou,sishen)
                    }else
                    upAny(feiting,zhanjiang,  niutou,sishen)
                } else upAny(zhanjiang,feiting)
            }
        }

        addGuanDeal(17) {
            over {
                fulls(zhanjiang, niutou, feiting, tieqi, sishen,yuren,haiyao)
            }
            chooseHero {
                upAny(zhanjiang, niutou, feiting, tieqi, sishen,yuren,haiyao)
            }
        }


        addGuanDealWithHerosFull(38, listOf(tianshi), listOf(haiyao), delay = 3000)

//        addGuanDeal(38) {
//            over {
//                fulls(zhanjiang, niutou, feiting, tieqi, yuren, tianshi, sishen)
//            }
//            chooseHero {
//                upAny(zhanjiang, niutou, feiting, tieqi, tianshi, sishen, yuren)
//            }
//            onStart {
//                carDoing.downHero(haiyao)
//                delay(3000)
//            }
//        }
        addGuanDealWithHerosFull(40, listOf(haiyao), listOf(tianshi,yuren))


        addGuanDealWithHerosFull(47, listOf(tianshi))


        add49(feiting)


        addGuanDeal(50){

            onlyDo {
                carDoing.downHero(tianshi)
            }
        }
        //内部实际是52关开始
        add50(listOf(zhanjiang, niutou, feiting, tieqi, sishen, yuren, wangjiang),listOf(yuren,wangjiang))

        add69(listOf(yuren,tianshi))
        g69State = 1//跟波打，上来就是1  下卡，等掉血上去抗个无敌就可以了，就不需要快捷键上下卡了

        addGuanDealWithHerosFull(70, listOf(wangjiang), listOf(tianshi))
        addGuanDealWithHerosFull(78, listOf(tianshi), listOf(yuren))

        addGuanDealWithHerosFull(82, listOf(yuren), listOf(wangjiang))

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