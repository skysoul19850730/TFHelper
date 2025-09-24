package tasks.huodong.fengbao

import data.HeroCreator
import tasks.SimpleHeZuoHeroDoing

class Fengbao2 : SimpleHeZuoHeroDoing() {

    val zhanjiangb = HeroCreator.zhanjiangb.create()
    val nvwang = HeroCreator.nvwang.create()
    val yuren = HeroCreator.yuren.create()
    val daoke = HeroCreator.daoke.create()
    val fuke = HeroCreator.fuke.create()
    val saman2 = HeroCreator.saman2.create()
    val wangjiang2 = HeroCreator.wangjiang2.create()
    val moqiu = HeroCreator.moqiu.create()
    val niutou2 = HeroCreator.niutou2.create()
    val efei = HeroCreator.efei.create()
    override fun initHeroes() {
        super.initHeroes()
        heros = arrayListOf(zhanjiangb, nvwang, yuren, daoke, fuke, saman2, wangjiang2, moqiu, niutou2, efei)
        addGuanDeal(0){
            over {
                fulls(zhanjiangb, saman2, niutou2) || curGuan>30
            }

            chooseHero {
                upAny(zhanjiangb, saman2, niutou2)
            }
        }

       addGuanDeal(32){
           over {
               fulls(zhanjiangb, saman2, niutou2,wangjiang2, efei)
           }
           chooseHero {
             upAny(zhanjiangb, saman2, niutou2,wangjiang2, efei)
           }
       }

        addGuanDeal(80){
            over{
                nvwang.isInCar()
            }
            chooseHero{
                upAny(nvwang)
            }
        }

        curGuanDeal = guanDealList.get(0)
    }

}
