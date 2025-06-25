package tasks.huodong.shuihu2

import data.HeroCreator
import tasks.SimpleHeZuoHeroDoing

class Day1:SimpleHeZuoHeroDoing() {
    val zhanjiang = HeroCreator.zhanjiang2.create()
    val nvwang = HeroCreator.nvwang.create()
    val shahuang = HeroCreator.shahuang.create()
    val dasheng = HeroCreator.dasheng.create()
    val jiaonv = HeroCreator.jiaonv.create()
    val wangjiang2 = HeroCreator.wangjiang2.create()
    val ganglie = HeroCreator.ganglie.create()
    val muqiu = HeroCreator.muqiu.create()
    val huanqiu = HeroCreator.huanqiu.create()
    val shexian = HeroCreator.shexian.create()

    override fun initHeroes() {
        heros = arrayListOf(zhanjiang, nvwang, shahuang, dasheng, jiaonv, wangjiang2, ganglie, muqiu, huanqiu, shexian)

        guanDealList.add(GuanDeal(0, isOver = {
            fulls(zhanjiang,jiaonv,wangjiang2,ganglie,nvwang,shahuang,shexian) && longxin
        }, chooseHero = {
            if(!zhanjiang.isFull()){
                upAny(zhanjiang,jiaonv,shexian, zhuangbei = {longxin})
            }else{
                if(!jiaonv.isInCar()){
                    upAny(jiaonv,shexian, zhuangbei = {longxin})
                }else{
                    if(!wangjiang2.isInCar() && !ganglie.isInCar()){
                        upAny(wangjiang2,ganglie,jiaonv,shexian, zhuangbei = {longxin})
                    }else if(!wangjiang2.isInCar()){
                        upAny(wangjiang2,jiaonv,ganglie,shexian, zhuangbei = {longxin})
                    }else if(!ganglie.isInCar()){
                        upAny(ganglie,jiaonv,wangjiang2,shexian, zhuangbei = {longxin})
                    }else{
                        upAny(nvwang,shahuang,jiaonv,ganglie,wangjiang2,shexian, zhuangbei = {longxin})
                    }
                }
            }
        }))

        guanDealList.add(GuanDeal(48, onlyDoSomething = {
            carDoing.downHero(wangjiang2)
            carDoing.downHero(ganglie)
        }))
        guanDealList.add(GuanDeal(50, isOver = {
            fulls(wangjiang2,ganglie)
        }, chooseHero = {
            upAny(wangjiang2,ganglie)
        }))

        guanDealList.add(GuanDeal(100,isOver = {qiangxi}, chooseHero = {zhuangbei { qiangxi }}))
        guanDealList.add(GuanDeal(110,isOver = {yandou}, chooseHero = {zhuangbei { yandou }}))
        guanDealList.add(GuanDeal(120,isOver = {qiangxi}, chooseHero = {zhuangbei { qiangxi }}))
        guanDealList.add(GuanDeal(130,isOver = {yandou}, chooseHero = {zhuangbei { yandou }}))
        guanDealList.add(GuanDeal(140,isOver = {longxin}, chooseHero = {zhuangbei { longxin }}))
        curGuanDeal = guanDealList.get(0)
    }
}