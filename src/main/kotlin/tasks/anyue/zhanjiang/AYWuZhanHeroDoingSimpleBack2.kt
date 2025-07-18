package tasks.anyue.zhanjiang

import data.HeroBean
import data.HeroCreator
import kotlinx.coroutines.delay
import log
import tasks.XueLiang
import java.awt.event.KeyEvent

class AYWuZhanHeroDoingSimpleBack2 : BaseSimpleAnYueHeroDoing() {

    val zhanjiang = HeroCreator.zhanjiang2.create()
    val tieqi = HeroCreator.tieqi.create()
    val tuling = HeroCreator.tuling.create()
    val sishen = HeroCreator.sishen2.create()
    val dijing = HeroCreator.dijing.create()
    val yuren = HeroCreator.yuren.create()
    val huanqiu = HeroCreator.huanqiu.create()
    val shexian = HeroCreator.shexian.create()

    val dianfa = HeroCreator.dianfa.create()

    val gugu = HeroCreator.gugu.create()


    override fun onHeroPointByG19(hero: HeroBean): Boolean {//点战将无敌抗，点两次认输。。。
        return hero != zhanjiang
    }

    override fun initHeroes() {
        heros = arrayListOf(zhanjiang, tieqi, tuling, sishen, gugu, shexian, dianfa, dijing, huanqiu, yuren)
        heros39Up4 = arrayListOf(tuling, dianfa, sishen, gugu)

        guanDealList.add(GuanDeal(0, isOver = {
            fulls(zhanjiang, tuling, tieqi, dijing, dianfa, gugu, shexian)
        }, chooseHero = {
            if (!zhanjiang.isGold()) {//直上战将
                log("战将没满")
                upAny(zhanjiang)
            } else {
                if (!tieqi.isInCar()) {
                    this.indexOf(tieqi)
                } else upAny(zhanjiang,gugu, tieqi, dijing, dianfa, tuling, shexian)
            }
        }))
//        guanDealList.add(GuanDeal(19, isOver = { false }, chooseHero = {
//            deal19(this)
//        }, onGuanDealStart = {
//            start19Oberserver(true)
//        }))

        guanDealList.add(GuanDeal(27, isOver = {
            fulls(zhanjiang,  dianfa, tieqi, tuling, sishen, gugu, shexian)
                    && yandou
        }, chooseHero = {
            upAny(zhanjiang, gugu, tieqi, tuling, sishen, dianfa, shexian, zhuangbei = { yandou })
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
            fulls(zhanjiang,  dianfa, tieqi, tuling, sishen, gugu, shexian)
                    && yandou
        }, chooseHero = {
            upAny(zhanjiang, gugu, tieqi, tuling, sishen, dianfa, shexian, zhuangbei = { yandou })
        }))

        guanDealList.add(GuanDeal(51, isOver = { yuren.isFull() && qiangxi }, chooseHero = {
            carDoing.downHero(sishen)
            upAny(yuren, zhuangbei = { qiangxi }) }
        ,))


        guanDealList.add(GuanDeal(69, isOver = {
            false
        }, chooseHero = {
            while(!qiu69 && curGuan<70){
                delay(200)
            }
            if(qiu69 && XueLiang.getBossXueliang()<0.99f){
                upAny(huanqiu)
            }else {
                qiu69 = false
                -1
            }
        }))

        guanDealList.add(GuanDeal(71, isOver = {sishen.isFull() && longxin }, chooseHero = {
            carDoing.downHero(yuren)
            upAny(sishen,zhuangbei = { longxin }) }))


        guanDealList.add(GuanDeal(
            startGuan = 100,
            isOver = {
                fulls(yuren)
            },
            chooseHero = {
                upAny(yuren)
            }
            , onGuanDealStart = {
                carDoing.downHero(dianfa)
            }
        ))

        guanDealList.add(GuanDeal(111, onlyDoSomething = {
            carDoing.downHero(shexian)
        }).apply { des = "下射线" })


        guanDealList.add(GuanDeal(120, isOver = {
            fulls(shexian) && longxin
        }, chooseHero = {
            upAny(shexian, zhuangbei = {longxin})
        }
        ))

        guanDealList.add(GuanDeal(129, isOver = { curGuan > 129 }, chooseHero = { g129Index(this) })
            .apply { des = "按0下射线，再按0上射线" })


        guanDealList.add(
            GuanDeal(
                130,
                isOver = { shexian.isFull() },
                chooseHero = {
                    upAny(shexian)
                })
        )

        curGuanDeal = guanDealList.get(0)
    }
    var qiu69 = false

    override suspend fun onKeyDown(code: Int): Boolean {

        if (guankaTask?.currentGuanIndex == 69 || guankaTask?.currentGuanIndex == 68
//            || guankaTask?.currentGuanIndex == 49 || guankaTask?.currentGuanIndex == 48
        ) {
            if(code==KeyEvent.VK_NUMPAD0) {
                qiu69 = !qiu69
                return true
            }
        }

        if (guankaTask?.currentGuanIndex == 129 || guankaTask?.currentGuanIndex == 128
        ) {//按0 下射线，备射线，再按0，上射线
            when (code) {
                KeyEvent.VK_NUMPAD0 -> {
                    g129State = if (g129State == 0) 1 else 0
                }
            }

            return true
        }


        return super.onKeyDown(code)
    }


    var g129State = 0//0等待,1 下宝库 备宝库，2上宝库 //回到了初始态，等1再下宝库循环


    suspend fun g129Index(heros: List<HeroBean?>): Int {
        if (curGuan > 129) return -1
        while (g129State == 0) {
            delay(100)
            if (curGuan > 129) return -1
        }
        when (g129State) {
            1 -> {
                carDoing.downHero(shexian)
                var index = heros.indexOf(shexian)
                if (index > -1) {
                    while (g129State == 1) {
                        delay(100)
                        if (curGuan > 129) return -1
                    }
                    return index
                }
                return -1
            }

        }
        return -1
    }
}