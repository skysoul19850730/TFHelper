package tasks.huodong.qiuxiang2

import data.HeroCreator

class QiuXiang2Day4 : BaseQiuxiang2() {
    val yuren = HeroCreator.yuren.create()
    val zhanjiang2 = HeroCreator.zhanjiang.create()
    val saman2 = HeroCreator.saman.create()
    val niutou2 = HeroCreator.niutou.create()
    val wangjiang2 = HeroCreator.wangjiang.create()
    val maomi = HeroCreator.maomi.create()
    val bingqi = HeroCreator.bingqi.create()
    val guangqiu = HeroCreator.guangqiu.create()
    val huanqiu = HeroCreator.huanqiu.create()
    val muqiu = HeroCreator.muqiu.create()




    override fun initHeroes() {
        heros = arrayListOf(yuren, zhanjiang2, saman2, niutou2, wangjiang2, maomi, bingqi, guangqiu, huanqiu, muqiu)

        muQiu = muqiu
        damu = true

        guanDealList.add(GuanDeal(
            startGuan = 0,
            isOver = {
                fulls(zhanjiang2,saman2,niutou2)
            },
            chooseHero = {
                if(zhanjiang2.isInCar()) {
                    upAny(zhanjiang2, saman2, niutou2)
                }else{
                    upAny(zhanjiang2)
                }
            }
        ))

        guanDealList.add(GuanDeal(
            startGuan = 30,
            isOver = {
                fulls(wangjiang2,yuren) && qiangxi
            },
            chooseHero = {
                upAny(wangjiang2,yuren, zhuangbei = {qiangxi})
            }

        ))

        guanDealList.add(GuanDeal(
            startGuan = 50,
            isOver = {
                fulls(maomi)
            },
            chooseHero = {
                upAny(maomi)
            }
        ))

        addHuan(140,{longxin})
        add149()
        addHuan(150,{qiangxi})

        curGuanDeal = guanDealList.get(0)
    }


}