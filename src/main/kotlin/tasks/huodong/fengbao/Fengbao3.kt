package tasks.huodong.fengbao

import data.HeroCreator
import tasks.SimpleHeZuoHeroDoing

class Fengbao3 : SimpleHeZuoHeroDoing() {

    val zhanjiangb = HeroCreator.shan.create()
    val nvwang = HeroCreator.mengyan.create()
    val yuren = HeroCreator.gugu.create()
    val daoke = HeroCreator.bingqi.create()
    val fuke = HeroCreator.kui.create()
    val wangjiang2 = HeroCreator.haiyao.create()
    val efei = HeroCreator.shexian.create()


    val saman2 = HeroCreator.dasheng.create()
    val niutou2 = HeroCreator.huanqiu.create()
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

       gudingShuaQiuTask("bingqiu",49,2500,null,50)

       gudingShuaQiuTask("bingqiu",80,2500,null,90)


        changeZhuangbei(110){yandou}
        changeZhuangbei(120){longxin}
        changeZhuangbei(130){yandou}
        changeZhuangbei(140){longxin}
        changeZhuangbei(160){yandou}
        changeZhuangbei(170){longxin}

        curGuanDeal = guanDealList.get(0)
    }

}
