package tasks.huodong.qiuxiang2

import data.HeroCreator

class QiuXiang2Day1 : BaseQiuxiang2() {
    val zhanjiang = HeroCreator.zhanjiang2.create()
    val niutou = HeroCreator.niutou2.create()
    val saman2 = HeroCreator.saman2.create()
    val efei = HeroCreator.efei.create()
    val nvwang = HeroCreator.nvwang.create()
    val maomi = HeroCreator.maomi.create()

    val shengqi = HeroCreator.shengqi.create()

    val bingqi = HeroCreator.bingqi.create()
    val muqiu = HeroCreator.muqiu.create()
    val huanqiu = HeroCreator.huanqiu.create()




    override fun initHeroes() {
        heros = arrayListOf(zhanjiang, niutou, maomi, saman2, efei, nvwang, bingqi, muqiu, shengqi, huanqiu)
        damu = true

        guanDealList.add(GuanDeal(
            0,
            isOver = {
                fulls(zhanjiang,niutou,maomi,saman2)
            },
            chooseHero = {
                if(zhanjiang.isInCar()) {
                    upAny(zhanjiang, niutou, maomi, saman2)
                }else upAny(zhanjiang)
            }
        ))

        guanDealList.add(GuanDeal(
            40,
            isOver = {
                fulls(efei) && nvwang.isInCar() && qiangxi
            },
            chooseHero = {
                if(nvwang.isInCar()){
                    upAny(efei, zhuangbei = {qiangxi})
                }else{
                    upAny(efei,nvwang, zhuangbei = {qiangxi})
                }
            }
        ))

        add49()

        guanDealList.add(GuanDeal(
            50,
            isOver = {
                fulls(zhanjiang, niutou, maomi, saman2,efei) && nvwang.isInCar()
            },
            chooseHero = {
                if(nvwang.isInCar()){
                    upAny(zhanjiang, niutou, maomi, saman2,efei)
                }else{
                    upAny(zhanjiang, niutou, maomi, saman2,efei,nvwang)
                }
            }
        ))

        addHuan(140,{longxin})
        add149()
        addHuan(150,{qiangxi})

        curGuanDeal = guanDealList.get(0)
    }


}