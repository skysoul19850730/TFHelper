package tasks.huodong.qiuxiang2

import data.HeroCreator

class QiuXiang2Day3 : BaseQiuxiang2() {
    val dianfa = HeroCreator.dianfa.create()
    val mengyan = HeroCreator.mengyan.create()
    val xiaoye = HeroCreator.xiaoye.create()
    val xiongmao = HeroCreator.xiongmao.create()
    val kuiqian = HeroCreator.kuiqian.create()
    val ganglie = HeroCreator.ganglie.create()
    val dasheng = HeroCreator.dasheng.create()
    val huanqiu = HeroCreator.huanqiu.create()
    val moqiu = HeroCreator.moqiu.create()
    val wawa = HeroCreator.wawa.create()




    override fun initHeroes() {
        heros = arrayListOf(dianfa, xiaoye, ganglie, wawa, moqiu, dasheng, xiongmao, kuiqian, mengyan, huanqiu)

        addGuanDeal(0){
            over {
                fulls(mengyan,kuiqian,dianfa,xiongmao,xiaoye,ganglie,wawa) && qiangxi
            }
            chooseHero {
                if(mengyan.isInCar().not()){
                    upAny(mengyan,wawa)
                }else if(kuiqian.isInCar().not()){
                    upAny(mengyan,kuiqian,wawa)
                }else{
                    upAny(mengyan,kuiqian,dianfa,xiongmao,xiaoye,ganglie,wawa, zhuangbei = {qiangxi})
                }
            }
        }

        addGuanDealWithHerosFull(90,listOf(dasheng),listOf(kuiqian))


        var start = 98
        for (i in start..199) {

            if (i % 10 == 9) {
               gudingShuaQiuTask("moqiu",i,5000)
            }

            if(i==141){
                changeZhuangbei(i){longxin}
            }

            if(i==151){
                changeZhuangbei(i){qiangxi}
            }
            if(i==161){
                changeZhuangbei(i){yandou}
            }
            if(i==171){
                changeZhuangbei(i){qiangxi}
            }
            if(i==181){
                changeZhuangbei(i){yandou}
            }
        }

        curGuanDeal = guanDealList.get(0)
    }


}