package tasks.daxuanwo

import data.HeroCreator
import kotlinx.coroutines.delay
import java.awt.event.KeyEvent

class XWZJHeroDoingDairen : BaseSimpleXWHeroDoing() {
    val tieqi = HeroCreator.tieqi.create()
    val zhanjiang = HeroCreator.zhanjiang.create()
    val yuren = HeroCreator.yuren.create()
    val feiting = HeroCreator.feiting.create()
    val gugu = HeroCreator.gugu.create()

    val xiaoye = HeroCreator.xiaoye.create()
    val sishen = HeroCreator.sishen.create()

    val muqiu = HeroCreator.muqiu.create()
    val guangqiu = HeroCreator.guangqiu.create()
    val bingqiu = HeroCreator.bingqiu.create()

    override fun initHeroes() {
        super.initHeroes()
        heros = arrayListOf(tieqi,zhanjiang,yuren,feiting,gugu,xiaoye,sishen,muqiu,guangqiu,bingqiu)
        addGuanDeal(0){
            over {
                fulls(zhanjiang,gugu,feiting,sishen,xiaoye)
            }
            chooseHero {
                if(zhanjiang.isInCar()) {
                    upAny(zhanjiang,gugu,feiting,sishen,xiaoye)
                }else upAny(zhanjiang)
            }
        }

        addGuanDeal(18){
            over {
                fulls(zhanjiang,sishen,feiting,tieqi,xiaoye,gugu,yuren)
            }
            chooseHero{
                upAny(zhanjiang,sishen,feiting,tieqi,xiaoye,gugu,yuren)
            }
        }

        addGuanDeal(29){
            onlyDo {
                start29()
            }
        }

        addGuanDeal(30){
            onlyDo { stop29() }
        }

        curGuanDeal = guanDealList.get(0)
    }

    override suspend fun onKeyDown(code: Int): Boolean {

        if(code == KeyEvent.VK_NUMPAD0){
            if(curGuan==49){
                g49 = 1
                return true
            }
        }

        return super.onKeyDown(code)
    }
}