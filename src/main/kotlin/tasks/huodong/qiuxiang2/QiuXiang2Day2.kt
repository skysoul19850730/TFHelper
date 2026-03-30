package tasks.huodong.qiuxiang2

import data.HeroCreator

class QiuXiang2Day2 : BaseQiuxiang2() {
    val zhanjiang = HeroCreator.zhanjiang.create()
    val nvwang = HeroCreator.nvwang.create()
    val shahuang = HeroCreator.shahuang.create()
    val tianshi = HeroCreator.tianshi.create()
    val jiaonv = HeroCreator.jiaonv.create()
    val saman = HeroCreator.saman.create()
    val shengqi = HeroCreator.shengqi.create()
    val moqiu = HeroCreator.moqiu.create()
    val huanqiu = HeroCreator.huanqiu.create()
    val shexian = HeroCreator.shexian.create()




    override fun initHeroes() {
        heros = arrayListOf(zhanjiang, shahuang, saman, shexian, huanqiu, shengqi, tianshi, jiaonv, nvwang, moqiu)

        guanDealList.add(GuanDeal(
            startGuan = 0,
            isOver = {
                fulls(zhanjiang,jiaonv,saman,shexian,shengqi)
            },
            chooseHero = {
                if(zhanjiang.isInCar()){
                    upAny(zhanjiang,jiaonv,saman,shexian,shengqi)
                }else{
                    upAny(zhanjiang)
                }
            }
        ))


        addGuanDealWithHerosFull(50, listOf(zhanjiang,jiaonv,saman,shexian,nvwang,shahuang,tianshi)
        , listOf(shengqi), zhuangbei = {qiangxi}
        )



        curGuanDeal = guanDealList.get(0)
    }


}