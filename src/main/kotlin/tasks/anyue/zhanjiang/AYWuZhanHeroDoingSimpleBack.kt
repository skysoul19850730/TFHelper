package tasks.anyue.zhanjiang

import App
import data.HeroBean
import data.HeroCreator
import log
import utils.GuDingShuaKaUtil
import java.awt.event.KeyEvent

class AYWuZhanHeroDoingSimpleBack : BaseSimpleAnYueHeroDoing() {

    val zhanjiang = HeroCreator.zhanjiang2.create()
    val tieqi = HeroCreator.tieqi.create()
    val saman = HeroCreator.saman2.create()
    val sishen = HeroCreator.sishen2.create()
    val dijing = HeroCreator.dijing.create()
    val guangqiu = HeroCreator.guangqiu.create()
    val huanqiu = HeroCreator.huanqiu.create()
    val shexian = HeroCreator.shexian.create()

    val kuangjiang = HeroCreator.yuren.create()

    val gugu = HeroCreator.gugu.create()


    override fun onHeroPointByG19(hero: HeroBean): Boolean {//点战将无敌抗，点两次认输。。。
        return hero != zhanjiang
    }

    override fun initHeroes() {
        heros = arrayListOf(zhanjiang, tieqi, saman, sishen, gugu, shexian, kuangjiang, dijing, huanqiu, guangqiu)
        heros39Up4 = arrayListOf(tieqi, kuangjiang, sishen, gugu)

        guanDealList.add(GuanDeal(0, isOver = {
            fulls(zhanjiang, saman, tieqi, dijing, sishen, gugu, shexian)
        }, chooseHero = {
            if (!zhanjiang.isGold()) {//直上战将
                log("战将没满")
                upAny(zhanjiang)
            } else {
                if (!saman.isInCar()) {
                    this.indexOf(saman)
                } else upAny(zhanjiang,gugu, tieqi, dijing, sishen, saman, shexian)
            }
        }))
//        guanDealList.add(GuanDeal(19, isOver = { false }, chooseHero = {
//            deal19(this)
//        }, onGuanDealStart = {
//            start19Oberserver(true)
//        }))

        guanDealList.add(GuanDeal(27, isOver = {
            fulls(zhanjiang,  kuangjiang, tieqi, saman, sishen, gugu, shexian)
                    && yandou
        }, chooseHero = {
            upAny(zhanjiang, gugu, tieqi, saman, sishen, kuangjiang, shexian, zhuangbei = { yandou })
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
            fulls(zhanjiang,  kuangjiang, tieqi, saman, sishen, gugu, shexian)
                    && yandou
        }, chooseHero = {
            upAny(zhanjiang, gugu, tieqi, saman, sishen, kuangjiang, shexian, zhuangbei = { yandou })
        }))

        guanDealList.add(GuanDeal(51, isOver = { qiangxi }, chooseHero = { upAny(zhuangbei = { qiangxi }) }))
        guanDealList.add(GuanDeal(71, isOver = { longxin }, chooseHero = { upAny(zhuangbei = { longxin }) }))


        curGuanDeal = guanDealList.get(0)
    }
}