package tasks.hanbing2.zhanjiang

import data.HeroBean
import data.HeroCreator
import tasks.hanbing2.BaseSimpleHBHeroDoing
import ui.zhandou.hanbing.HanBingModel

class HBZTHeroDoingZiQiang: BaseSimpleHBHeroDoing() {
    val isRenwu = false


    val zhanjiang = HeroCreator.zhanjiangb.create()
    val tieqi = HeroCreator.tieqi.create()
    val gugu = HeroCreator.gugu.create()
    val wangjiang = HeroCreator.wangjiang2.create()
    val sishen = HeroCreator.sishen2.create()
    val yuren = HeroCreator.yuren.create()
    val baoku = HeroCreator.baoku.create()
    val tianshi = HeroCreator.tianshi.create()

    val huanqiu = HeroCreator.huanqiu.create()

    val guangqiu = HeroBean(if(isRenwu) HanBingModel.renwuKa.value else "guangqiu", 40, needCar = false, compareRate = 0.95)


    override fun initHeroes() {
        super.initHeroes()
        heros = arrayListOf(
            zhanjiang,
            tieqi,
            gugu,
            wangjiang,
            sishen,
            yuren,
            baoku,
            tianshi,
            huanqiu,
            guangqiu
        )
        heroDoNotDown129 = zhanjiang

        addGuanDeal(0){
            over {
                fulls(zhanjiang,gugu,sishen,baoku)
            }
            chooseHero {
                upAny(zhanjiang,gugu,sishen,baoku)
            }
        }

        addGuanDeal(26){
            over{
                fulls(zhanjiang,gugu,sishen,tieqi,wangjiang,baoku)
            }
            chooseHero {
                upAny(zhanjiang,gugu,sishen,tieqi,wangjiang,baoku)
            }
        }

        addGuanDeal(60){
            over {
                fulls(yuren)
            }
            chooseHero {
                upAny(yuren)
            }
        }
        change2Tianshi(98)
        change2Wangjiang(100)
    }


    private fun change2Tianshi(guan:Int){

    }
    private fun change2Wangjiang(guan:Int){

    }
}