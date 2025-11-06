package tasks.anyue.zhanjiang

import data.HeroBean
import data.HeroCreator
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import log
import tasks.XueLiang
import java.awt.event.KeyEvent

class AYWuZhanHeroDoingSimpleBack2 : BaseSimpleAnYueHeroDoing() {

    val zhanjiang = HeroCreator.zhanjiang.create()
    val tieqi = HeroCreator.tieqi.create()
    val tuling = HeroCreator.tuling.create()
    val sishen = HeroCreator.sishen.create()
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
            var bossXue = XueLiang.getBossXueliang()
            while(!qiu69 && curGuan<70){
                if(bossXue>0 && bossXue<0.5){
                    delay(300)
                    var xue = XueLiang.getBossXueliang()
                    if(xue == bossXue){
                        //第一次判断相等，有可能是boss自己回血导致刚好回到上次观察的血量
                        //延迟300毫秒再获取一次血量，如果还是相同就代表无敌了（boss回血频率比较低)
                        delay(300)
                        xue = XueLiang.getBossXueliang()
                        if(xue==bossXue){
                            qiu69 = true
                        }else{
                            bossXue = xue
                        }
                    }else{
                        bossXue = xue
                    }
                }else{
                    delay(500)
                    bossXue = XueLiang.getBossXueliang()
                }

            }
            if(qiu69 && XueLiang.getBossXueliang()<0.9f){
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
            fulls(dianfa,shexian) && longxin
        }, chooseHero = {
            upAny(dianfa,shexian, zhuangbei = {longxin})
        }, onGuanDealStart = {carDoing.downHero(yuren)}
        ))

        guanDealList.add(GuanDeal(129, isOver = { curGuan > 129 }, chooseHero = { g129Index(this) }, onGuanDealStart = {
            GlobalScope.launch {
                check129Xue()
            }
        })
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

    override suspend fun onHuanQiuPost() {
        if (guankaTask?.currentGuanIndex == 69 || guankaTask?.currentGuanIndex == 68){
            return
        }
        super.onHuanQiuPost()
    }

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


    var g129State = 1//0等待,1 下宝库 备宝库，2上宝库 //回到了初始态，等1再下宝库循环


    var g129XueCount = 1//0,1 下射线，2，3上射线

    suspend fun check129Xue(){

        while(curGuan <= 129){
            XueLiang.observerXueDown(xueRate = 0.6f, over = {curGuan>129})
            g129XueCount++
            if(g129XueCount==2){
                delay(1500)
                g129XueCount = 0
                g129State = if (g129State == 0) 1 else 0
            }
            delay(2000)
        }

    }

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