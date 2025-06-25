package tasks.huodong.sanguo

import data.HeroCreator
import kotlinx.coroutines.delay
import tasks.SimpleHeZuoHeroDoing
import tasks.XueLiang

class SG2Shushi : SimpleHeZuoHeroDoing() {
    val mengyan = HeroCreator.mengyan.create()
    val daoke = HeroCreator.daoke.create()
    val xiongmao = HeroCreator.xiongmao.create()
    val kui = HeroCreator.kui.create()
    val bingnv = HeroCreator.bingnv.create()
    val bingqi = HeroCreator.bingqi.create()
    val dianfa = HeroCreator.dianfa.create()
    val ganglie = HeroCreator.ganglie.create()

    val moqiu = HeroCreator.moqiu.create()
    val muqiu = HeroCreator.muqiu.create()


    var lastMo = 0L

    override fun initHeroes() {
        heros = arrayListOf(mengyan, daoke, xiongmao, kui, bingnv, bingqi, dianfa, ganglie, moqiu, muqiu)

        guanDealList.add(GuanDeal(0, {
            fulls(mengyan, dianfa, ganglie, kui, bingnv, bingqi)
        }, {
            upAny(mengyan, dianfa, ganglie, kui, bingnv, bingqi)
        }))
        //145后，停止，改手动
        guanDealList.add(GuanDeal(30, {
            guankaTask!!.currentGuanIndex > 145
        }, {
            //100后上刀客满，没刷到刀客就扔魔球木球
            if (guankaTask!!.currentGuanIndex > 70 && !xiongmao.isFull()) {
                carDoing.downHero(kui)

               var index = upAny(xiongmao, moqiu, muqiu)
                if (index > -1 && index == indexOf(moqiu)) {
                    lastMo = System.currentTimeMillis()
                }

                return@GuanDeal index
            }

            if (guankaTask!!.currentGuanIndex > 100 && !daoke.isFull()) {
                carDoing.downHero(bingqi)

                var index = upAny(daoke, moqiu, muqiu)
                if (index > -1 && index == indexOf(moqiu)) {
                    lastMo = System.currentTimeMillis()
                }

                return@GuanDeal index
            }

            var index = indexOf(moqiu)
            if (index > -1) {
                if (System.currentTimeMillis() - lastMo > 4000) {
                    lastMo = System.currentTimeMillis()
                    return@GuanDeal index
                } else {
                    while (System.currentTimeMillis() - lastMo < 4000 && !XueLiang.isMLess(0.5f)) {
                        //如果没到魔时，血量也健康
                        delay(200)
                    }
                    if (XueLiang.isMLess(0.5f)) {
                        index = upAny(muqiu, moqiu)
                        if (index > -1 && index == indexOf(moqiu)) {
                            lastMo = System.currentTimeMillis()
                        }
                        index
                    } else {
                        index = upAny(moqiu)
                        if (index > -1) {
                            lastMo = System.currentTimeMillis()
                        }
                        index
                    }
                }
            } else return@GuanDeal indexOf(muqiu)
        }))



        //145后，停止，改手动
        guanDealList.add(GuanDeal(151, {
            false
        }, {
            var index = indexOf(moqiu)
            if (index > -1) {
                if (System.currentTimeMillis() - lastMo > 4000) {
                    lastMo = System.currentTimeMillis()
                    return@GuanDeal index
                } else {
                    while (System.currentTimeMillis() - lastMo < 4000 && !XueLiang.isMLess(0.5f)) {
                        //如果没到魔时，血量也健康
                        delay(200)
                    }
                    if (XueLiang.isMLess(0.5f)) {
                        index = upAny(muqiu, moqiu)
                        if (index > -1 && index == indexOf(moqiu)) {
                            lastMo = System.currentTimeMillis()
                        }
                        index
                    } else {
                        index = upAny(moqiu)
                        if (index > -1) {
                            lastMo = System.currentTimeMillis()
                        }
                        index
                    }
                }
            } else return@GuanDeal indexOf(muqiu)
        }))

        curGuanDeal = guanDealList.get(0)
    }


}