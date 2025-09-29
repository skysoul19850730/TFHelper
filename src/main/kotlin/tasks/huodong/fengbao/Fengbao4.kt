package tasks.huodong.fengbao

import data.HeroCreator
import tasks.SimpleHeZuoHeroDoing

class Fengbao4 : SimpleHeZuoHeroDoing() {

    val zhanjiangb = HeroCreator.dianfa.create()
    val nvwang = HeroCreator.hugong.create()
    val yuren = HeroCreator.gugu.create()
    val daoke = HeroCreator.houyi.create()
    val fuke = HeroCreator.haiyao.create()
    val wangjiang2 = HeroCreator.bingqi.create()
    val efei = HeroCreator.shexian.create()


    val saman2 = HeroCreator.moqiu.create()
    val niutou2 = HeroCreator.shenv.create()
    val moqiu = HeroCreator.bingqiu.create()
    override fun initHeroes() {
        super.initHeroes()
        heros = arrayListOf(zhanjiangb, nvwang, yuren, daoke, fuke, saman2, wangjiang2, moqiu, niutou2, efei)
        addGuanDeal(0){
            over {
               fulls(zhanjiangb,nvwang,yuren,fuke,wangjiang2,daoke,efei) && longxin
            }

            chooseHero {
                upAny(zhanjiangb,nvwang,yuren,fuke,wangjiang2,daoke,efei,zhuangbei ={ longxin })
            }
        }

//        changeZhuangbei(110){yandou}
//        changeZhuangbei(120){longxin}
//        changeZhuangbei(130){yandou}
//        changeZhuangbei(140){longxin}
//        changeZhuangbei(160){yandou}
//        changeZhuangbei(170){longxin}

        curGuanDeal = guanDealList.get(0)
    }

}
