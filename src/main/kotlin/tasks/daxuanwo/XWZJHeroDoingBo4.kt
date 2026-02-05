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
    val bingqi = HeroCreator.bingqi.create()
    
    val yuren = HeroCreator.yuren.create()
    val feiting = HeroCreator.feiting.create()
    val haiyao = HeroCreator.haiyao.create()

   
    val guangqiu = HeroCreator.guangqiu.create()


    override fun initHeroes() {
        super.initHeroes()
        auto59 = true
        heros = arrayListOf(
            sishen, tieqi, zhanjiang, haiyao, niutou, yuren, feiting, tianshi, guangqiu, bingqi
        )
        addGuanDeal(0) {
            over {
                fulls(zhanjiang, niutou,sishen, feiting)
            }
            chooseHero {
                if (zhanjiang.isInCar()) {
                    upAny(zhanjiang, feiting, niutou,sishen)
                } else upAny(zhanjiang)
            }
        }

        addGuanDeal(18) {
            over {
                fulls(zhanjiang, niutou, feiting, tieqi, sishen,haiyao)
            }
            chooseHero {
                upAny(zhanjiang, niutou, feiting, tieqi, sishen,haiyao)
            }
        }


        addGuanDeal(38) {
            over {
                fulls(zhanjiang, niutou, feiting, tieqi, haiyao, tianshi, yuren)
            }
            chooseHero {
                upAny(zhanjiang, niutou, feiting, tieqi, tianshi, yuren, haiyao)
            }
            onStart {
                carDoing.downHero(sishen)
                delay(3000)
            }
        }
        addGuanDeal(40){

            onlyDo {
                carDoing.downHero(tianshi)
            }
        }

        addGuanDealWithHerosFull(47, listOf(tianshi))


        add49(feiting)


        addGuanDeal(50){

            onlyDo {
                carDoing.downHero(tianshi)
            }
        }
        //内部实际是52关开始
        add50(listOf(zhanjiang, niutou, feiting, tieqi, sishen, bingqi, tianshi),listOf(tieqi,tianshi))

        add69()

        addGuanDealWithHerosFull(70, listOf(tieqi,yuren), listOf(bingqi))
        addGuanDealWithHerosFull(78, listOf(tianshi))
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