package tasks.anyue.zhanjiang

import MainData
import data.HeroBean
import data.HeroCreator
import kotlinx.coroutines.delay
import java.awt.event.KeyEvent

class AYWuZhanHeroDoingSimpleBo : BaseSimpleAnYueHeroDoing() {

    val zhanjiang = HeroCreator.zhanjiang.create()
    val tieqi = HeroCreator.tieqi.create()
    val shuiling = HeroCreator.shuiling.create()
    val tuling = HeroCreator.tuling.create()
    val niutou2 = HeroCreator.niutou.create()
    val baoku = HeroCreator.baoku.create()
    val dianfa = HeroCreator.dianfa.create()
    val dijing = HeroCreator.dijing.create()
    val kuangjiang = HeroCreator.kuangjiang.create()
    val guangqiu = HeroCreator.guangqiu.create()

    override fun initHeroes() {
        heros = arrayListOf(zhanjiang, tieqi, shuiling, tuling, niutou2, baoku, dianfa, dijing, kuangjiang, guangqiu)
        hero19 = dijing
        heros39Up4 = arrayListOf(niutou2, shuiling, tuling, dianfa)

        guanDealList.add(GuanDeal(0, isOver = {
            zhanjiang.isGold()
        }, chooseHero = {
            upAny(zhanjiang)
        }))
//        guanDealList.add(GuanDeal(19, isOver = { false }, chooseHero = {
//            deal19(this)
//        }, onGuanDealStart = {
//            start19Oberserver(true)
//        }))
        guanDealList.add(GuanDeal(9, isOver = {
            tieqi.isInCar() && dijing.isInCar()
        }, chooseHero = {
            var index = this.indexOf(tieqi)
            if(index>-1 && !tieqi.isInCar()){
                carDoing.downPosition(1)
                upAny(tieqi)
            }else {
                upAny(zhanjiang, niutou2, baoku, dijing,tieqi)
            }
        }))


        guanDealList.add(GuanDeal(16, isOver = {
            fulls(zhanjiang, niutou2, tieqi, dijing, tuling, dianfa, baoku)
        }, chooseHero = {
            upAny(zhanjiang, niutou2, tieqi, dijing, tuling, dianfa, baoku)
        }))

        guanDealList.add(GuanDeal(21, isOver = {
            fulls(zhanjiang, niutou2, tieqi, shuiling, tuling, dianfa, baoku)
        }, chooseHero = {
            upAny(zhanjiang, niutou2, tieqi, shuiling, tuling, dianfa, baoku)
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
            fulls(zhanjiang, niutou2, tieqi, shuiling, tuling, dianfa, baoku)
        }, chooseHero = {
            upAny(zhanjiang, niutou2, tieqi, shuiling, tuling, dianfa, baoku)
        }))
        guanDealList.add(GuanDeal(51, isOver = {
            fulls(zhanjiang, tieqi, shuiling, tuling, dianfa, baoku) && kuangjiang.isInCar()
        }, chooseHero = {
            upAny(zhanjiang, kuangjiang, tieqi, shuiling, tuling, dianfa, baoku)
        }, onGuanDealStart = {
            carDoing.downHero(niutou2)
        }))
        guanDealList.add(GuanDeal(71, isOver = {
            fulls(zhanjiang, niutou2, tieqi, shuiling, tuling, dianfa, baoku)
        }, chooseHero = {
            upAny(zhanjiang, niutou2, tieqi, shuiling, tuling, dianfa, baoku)
        }, onGuanDealStart = {
            carDoing.downHero(kuangjiang)
        }))

        guanDealList.add(GuanDeal(111, isOver = { kuangjiang.isInCar() }, chooseHero = {
            upAny(kuangjiang)
        }, onGuanDealStart = {
            carDoing.downHero(baoku)
            carDoing.downHero(niutou2)
        }))

        guanDealList.add(GuanDeal(119, isOver = { false }, chooseHero = {
            g119Index(this)
        }))

        guanDealList.add(GuanDeal(120, isOver = {
            fulls(niutou2, baoku)
        }, chooseHero = {
            upAny(niutou2, baoku)
        }, onGuanDealStart = {
            carDoing.downHero(kuangjiang)
            g1191State = -1
        }
        ))

        guanDealList.add(GuanDeal(129, isOver = { false }, chooseHero = { g129Index(this) }))


        guanDealList.add(
            GuanDeal(
                130,
                isOver = { shuiling.isFull() && dianfa.isFull() && baoku.isFull() },
                chooseHero = {
                    upAny(shuiling, dianfa, baoku)
                },
                onGuanDealStart = {
                    carDoing.downHero(kuangjiang)
                    g129State = -1
                })
        )

        curGuanDeal = guanDealList.get(0)
    }

    var g1191State = 0//0等待

    suspend fun g119Index(heros: List<HeroBean?>): Int {
        while (g1191State == 0) {
            delay(100)
        }
        when (g1191State) {
            5 -> {
                carDoing.downHero(kuangjiang)
                var index = heros.indexOf(kuangjiang)
                if (index > -1) {
                    while (g1191State == 5) {
                        delay(100)
                    }
                    return index
                }
                return -1
            }

            6 -> {

                if (!kuangjiang.isInCar()) {//狂将没在车，就上狂将
                    return heros.indexOf(kuangjiang)
                } else {//狂将上了车
                    var index = heros.indexOf(baoku)
                    if (index > -1) {//如果有宝库，就等上宝库
                        while (g1191State == 6) {
                            delay(100)
                        }
                        return index
                    } else {//没宝库就上狂将，满了也容易出宝库了呢
                        return heros.upAny(kuangjiang)
                    }
                }

            }

            7 -> {


                if (!baoku.isInCar()) {
                    return heros.indexOf(baoku)
                } else {
                    g1191State = 0
                    return -1
                }
            }
        }
        return -1
    }


    var g129State = 0//0等待,1 下宝库 备宝库，2上宝库，下水灵，上狂将，下狂将，上土灵，宝库满，备狂将， 3 下电法上狂将，下狂将，上电法满，//回到了初始态，等1再下宝库循环

    var kuangjiangUped = false
    var lastKuanJiangUpTime = 0L

    suspend fun g129Index(heros: List<HeroBean?>): Int {
        while (g129State == 0) {
            delay(100)
        }
        when (g129State) {
            1 -> {
                carDoing.downHero(baoku)
                var index = heros.indexOf(baoku)
                if (index > -1) {
                    while (g1191State == 1) {
                        delay(100)
                    }
                    return index
                }
                return -1
            }

            2 -> {
                if (!baoku.isInCar()) {
                    return heros.indexOf(baoku)
                } else {
                    if (!kuangjiangUped) {//没切过狂将,这个状态在afterhero里面处理成true，在按键里处理成false
                        var index = heros.indexOf(kuangjiang)
                        if(index>-1){
                            carDoing.downHero(shuiling)
                            return index
                        }
                        return heros.upAny(baoku)
                    } else {
                        if(kuangjiang.isInCar()) {
                            var index = heros.indexOf(shuiling)
                            if(index>-1) {//刷到水灵再下狂将
                                while (System.currentTimeMillis() - lastKuanJiangUpTime < kuangjiangTime) {//狂将上车超过500再下
                                    delay(50)
                                }
                                carDoing.downHero(kuangjiang)
                                return index
                            }else{
                                return heros.upAny(baoku, kuangjiang)
                            }
                        }
                        if (shuiling.isFull() && baoku.isFull()) {
                            var index = heros.indexOf(kuangjiang)
                            if (index > -1) {//备狂将
                                while (g129State == 2) {
                                    delay(100)
                                }
                                carDoing.downHero(dianfa)
                                return index
                            }
                            return -1
                        } else {
                            return heros.upAny(shuiling, baoku)
                        }
                    }
                }
            }

            3 -> {
                if (!kuangjiangUped) {
                    var index = heros.indexOf(kuangjiang)
                    if(index>-1){
                        carDoing.downHero(dianfa)
                        return index
                    }
                    //如果这时还没刷出狂将，证明上一步的水灵和宝库可能还没满，就再上水灵，宝库，然后刷狂将继续
                    return heros.upAny(shuiling,baoku)
                } else {
                    if(kuangjiang.isInCar()) {
                        var index = heros.indexOf(dianfa)
                        if(index>-1) {//刷到dianfa再下狂将
                            while (System.currentTimeMillis() - lastKuanJiangUpTime < kuangjiangTime) {//狂将上车超过500再下
                                delay(50)
                            }
                            carDoing.downHero(kuangjiang)
                            return index
                        }else{
                            return heros.upAny(shuiling,baoku, kuangjiang)
                        }
                    }
                    if (dianfa.isFull() && shuiling.isFull() && baoku.isFull()) {//一次循环切 结束，等按1 下宝库备宝库
                        g129State = 0
                        return -1
                    } else {
                        return heros.upAny(dianfa,shuiling,baoku)
                    }
                }
            }
        }
        return -1
    }

    override suspend fun doAfterHeroBeforeWaiting(heroBean: HeroBean) {

        if(heroBean == kuangjiang){
            kuangjiangUped = true
            lastKuanJiangUpTime = System.currentTimeMillis()
        }

        super.doAfterHeroBeforeWaiting(heroBean)
    }

    override suspend fun onKeyDown(code: Int): Boolean {

        if (guankaTask?.currentGuanIndex == 119 || guankaTask?.currentGuanIndex == 118
        ) {//初几 就按 几(只按5，6，7）
            when (code) {
                KeyEvent.VK_NUMPAD5 -> {
                    g1191State = 5
                }

                KeyEvent.VK_NUMPAD6 -> {
                    g1191State = 6
                }

                KeyEvent.VK_NUMPAD7 -> {
                    g1191State = 7
                }
            }

            return true
        }

        if (guankaTask?.currentGuanIndex == 129 || guankaTask?.currentGuanIndex == 128
        ) {//初几 就按 几(只按5，6，7）
            when (code) {
                KeyEvent.VK_NUMPAD1 -> {
                    g129State = 1
                }

                KeyEvent.VK_NUMPAD2 -> {
                    kuangjiangUped = false
                    g129State = 2
                }

                KeyEvent.VK_NUMPAD3 -> {
                    kuangjiangUped = false
                    g129State = 3
                }
            }

            return true
        }

        if(code == KeyEvent.VK_PLUS){
            MainData.kuangjiangUpTime.value+=100
        }else if(code == KeyEvent.VK_MINUS){
            MainData.kuangjiangUpTime.value-=100
        }


        if (super.onKeyDown(code)) {
            return true
        }
        return doOnKeyDown(code)
    }

    val kuangjiangTime
        get() = MainData.kuangjiangUpTime.value

    suspend fun doOnKeyDown(code: Int): Boolean {


        return true
    }
}