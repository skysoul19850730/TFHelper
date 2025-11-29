package tasks.anyue.base

import data.Config
import kotlinx.coroutines.delay
import utils.AYUtil

class Ay79(val heroDoing: BaseAnYueHeroDoing) : AnSub {

    var bossXue = 100
    var curPuke = 0
    override fun addToHeroDoing() {
        addToHeroDoingUseBing()
    }

    var lastBing = 0L
    var timePreBing = 2000L
    suspend fun daBing() {
        if (System.currentTimeMillis() - lastBing > timePreBing) {

        } else {
            delay(timePreBing - (System.currentTimeMillis() - lastBing))
        }
    }

    private fun addToHeroDoingUseBing() {
        heroDoing.apply {
            val bingqiu = heros.firstOrNull { it.heroName == "bingqiu" }
            addGuanDeal(79) {
                over { curGuan > 79 || bossXue == 0 }
                chooseHero {
                    if (curPuke > 0) {
                        if (needPuke()) {
                            //停冰 杀牌
                            while (paiXueZai()) {//血条不在了就是杀掉了认为
                                delay(500)
                            }
                            bossXue -= curPuke
                            curPuke = 0
                        } else {
                            if (!paiXueZai()) {//切牌了要
                                curPuke = 0
                            }
                            daBing()
                            return@chooseHero this.indexOf(bingqiu!!)
                        }
                    } else {
                        curPuke = AYUtil.getPuke()
                        if (curPuke > 0) {
                            if (needPuke()) {
                                //停冰 杀牌
                                while (paiXueZai()) {
                                    delay(500)
                                }
                                bossXue -= curPuke
                                curPuke = 0
                            } else {
                                if (!paiXueZai()) {//切牌了要
                                    curPuke = 0
                                }
                                daBing()
                                return@chooseHero this.indexOf(bingqiu!!)
                            }
                        } else {
                            daBing()
                            return@chooseHero this.indexOf(bingqiu!!)
                        }
                    }

                    -1
                }
            }
        }
    }

    fun paiXueZai() = Config.AyPukeXuePoint.isFit()
    fun needPuke() = bossXue - curPuke < 0 || bossXue - curPuke > 10
}