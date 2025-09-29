package tasks.huodong.fengbao

import data.HeroCreator
import tasks.SimpleHeZuoHeroDoing

class Fengbao5 : SimpleHeZuoHeroDoing() {

    val zhanjiangb = HeroCreator.shuiling.create()
    val nvwang = HeroCreator.huoling.create()
    val yuren = HeroCreator.gugu.create()
    val daoke = HeroCreator.xiongmao2.create()
    val fuke = HeroCreator.daoke.create()
    val wangjiang2 = HeroCreator.bingqi.create()
    val efei = HeroCreator.shexian.create()


    val saman2 = HeroCreator.haiyao.create()
    val niutou2 = HeroCreator.shenv.create()
    val moqiu = HeroCreator.bingqiu.create()
    override fun initHeroes() {
        super.initHeroes()
        heros = arrayListOf(zhanjiangb, nvwang, yuren, daoke, fuke, saman2, wangjiang2, moqiu, niutou2, efei)
        addGuanDeal(0){
            over {
               fulls(zhanjiangb,nvwang,yuren,fuke,wangjiang2,daoke,efei)
            }

            chooseHero {
                upAny(zhanjiangb,nvwang,yuren,fuke,wangjiang2,daoke,efei)
            }
        }

       changeHero(110,wangjiang2,saman2)
       changeHero(120,saman2,wangjiang2)
       changeHero(130,wangjiang2,saman2)
        changeHero(150,saman2,wangjiang2)

        curGuanDeal = guanDealList.get(0)
    }

}
