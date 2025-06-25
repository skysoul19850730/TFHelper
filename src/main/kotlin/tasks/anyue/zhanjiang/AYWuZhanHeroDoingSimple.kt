package tasks.anyue.zhanjiang

import App
import data.HeroBean
import data.HeroCreator
import log
import utils.GuDingShuaKaUtil
import java.awt.event.KeyEvent

class AYWuZhanHeroDoingSimple : BaseSimpleAnYueHeroDoing() {

    val zhanjiang = HeroCreator.zhanjiang2.create()
    val tieqi = HeroCreator.tieqi.create()
    val saman = HeroCreator.saman2.create()
    val sishen = HeroCreator.sishen2.create()
    val yuren = HeroCreator.yuren.create()
    val shexian = HeroCreator.shexian.create()
    val xiaoye = HeroCreator.xiaoye.create()
    val dijing = HeroCreator.dijing.create()
    val huanqiu = HeroCreator.huanqiu.create()
    val guangqiu = HeroCreator.guangqiu.create()


    var guDingShuaKaUtil: GuDingShuaKaUtil? = null
    override fun onHeroPointByG19(hero: HeroBean): Boolean {//点战将无敌抗，点两次认输。。。
        return hero != zhanjiang
    }

    override fun initHeroes() {
        heros = arrayListOf(zhanjiang, tieqi, saman, sishen, yuren, shexian, xiaoye, dijing, huanqiu, guangqiu)
        hero19 = dijing
        heros39Up4 = arrayListOf(tieqi, saman, sishen, xiaoye)
        heroXiaoye = xiaoye

        guanDealList.add(GuanDeal(0, isOver = {
            fulls(zhanjiang, yuren, tieqi, dijing, sishen, xiaoye, shexian)
        }, chooseHero = {
            if (!zhanjiang.isGold()) {//直上战将
                log("战将没满")
                upAny(zhanjiang)
            } else {
                if (!yuren.isInCar()) {
                    this.indexOf(yuren)
                } else upAny(zhanjiang,yuren, tieqi, dijing, sishen, xiaoye, shexian)
            }
        }))
//        guanDealList.add(GuanDeal(19, isOver = { false }, chooseHero = {
//            deal19(this)
//        }, onGuanDealStart = {
//            start19Oberserver(true)
//        }))

        guanDealList.add(GuanDeal(20, isOver = {
            fulls(zhanjiang, yuren, tieqi, saman, sishen, xiaoye, shexian)
                    && yandou
        }, chooseHero = {
            upAny(zhanjiang, yuren, tieqi, saman, sishen, xiaoye, shexian, zhuangbei = { yandou })
        }, onGuanDealStart = {
            carDoing.downHero(dijing)
        }))

        guanDealList.add(
            GuanDeal(39, isOver = { false },
                chooseHero = {
                    deal39(this)
                })
        )

        guanDealList.add(GuanDeal(40, isOver = {
            fulls(zhanjiang, yuren, tieqi, saman, sishen, xiaoye, shexian)
                    && yandou
        }, chooseHero = {
            upAny(zhanjiang, yuren, tieqi, saman, sishen, xiaoye, shexian, zhuangbei = { yandou })
        }))

        guanDealList.add(GuanDeal(51, isOver = { qiangxi }, chooseHero = { upAny(zhuangbei = { qiangxi }) }))
        guanDealList.add(GuanDeal(71, isOver = { longxin }, chooseHero = { upAny(zhuangbei = { longxin }) }))

        guanDealList.add(GuanDeal(79, isOver = { xiaoye.isInCar() }, onGuanDealStart = {
            tiaoye79()
        }))
        guanDealList.add(GuanDeal(80, onlyDoSomething = {
            stopTiaoye79()
        }))

        guanDealList.add(GuanDeal(89, isOver = { xiaoye.isInCar() }, onGuanDealStart = {
            tiaoye79()
        }))

        guanDealList.add(GuanDeal(90, onlyDoSomething = {
            stopTiaoye79()
        }))
        guanDealList.add(GuanDeal(99, onlyDoSomething = {
            App.startAutoSave()
            tiaoye79()
        }))


        guanDealList.add(GuanDeal(100, isOver = { xiaoye.isFull() }, chooseHero = {
            upAny(xiaoye)
        }, onGuanDealStart = {
            stopTiaoye79()
        }))

        guanDealList.add(GuanDeal(109, onlyDoSomething = {
            App.startAutoSave()
        }))

        curGuanDeal = guanDealList.get(0)
    }





    override suspend fun onKeyDown(code: Int): Boolean {

        if(code == KeyEvent.VK_NUMPAD0 && (guankaTask!!.currentGuanIndex == 98 || guankaTask!!.currentGuanIndex == 99)){
            tiaozhengXiaoye = true
        }

        if (super.onKeyDown(code)) {
            return true
        }



        return doOnKeyDown(code)
    }

    suspend fun doOnKeyDown(code: Int): Boolean {

        return true
    }
}