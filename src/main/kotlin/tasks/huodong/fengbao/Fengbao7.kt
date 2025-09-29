package tasks.huodong.fengbao

import data.HeroCreator
import tasks.SimpleHeZuoHeroDoing

class Fengbao7 : SimpleHeZuoHeroDoing() {

    val zhanjiangb = HeroCreator.houzi3.create()
    val nvwang = HeroCreator.longwang.create()
    val yuren = HeroCreator.gugu.create()
    val daoke = HeroCreator.xiaoye.create()
    val fuke = HeroCreator.bingqi.create()
    val wangjiang2 = HeroCreator.haiyao.create()

    val efei = HeroCreator.youling.create()
    val saman2 = HeroCreator.fenghuang.create()
    val niutou2 = HeroCreator.moqiu.create()
    val moqiu = HeroCreator.muqiu.create()
    override fun initHeroes() {
        super.initHeroes()
        heros = arrayListOf(zhanjiangb, nvwang, yuren, daoke, fuke, saman2, wangjiang2, moqiu, niutou2, efei)
        addGuanDeal(0){
            over {
               fulls(zhanjiangb,nvwang,yuren,fuke,wangjiang2,daoke)
            }

            chooseHero {
                upAny(zhanjiangb,nvwang,yuren,fuke,wangjiang2,daoke)
            }
        }

        gudingShuaQiuTask("moqiu",30,5000,null,150)

        curGuanDeal = guanDealList.get(0)
    }

}
