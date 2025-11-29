package tasks.anyue.base

import data.Config
import kotlinx.coroutines.delay
import utils.AYUtil

class Ay89(val heroDoing: BaseAnYueHeroDoing) : AnSub {

    var curPuke = 0
    var pukes = arrayListOf<Int>()
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
            addGuanDeal(89) {
                over { curGuan > 89 || pukes.size == 5}
                chooseHero {
                    if (curPuke > 0) {
                        if (needPuke()) {
                            //停冰 杀牌
                            while (paiXueZai()) {//血条不在了就是杀掉了认为
                                delay(500)
                            }
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

    fun needPuke(): Boolean {
        if (curPuke == 20) {
            pukes.add(curPuke)
            return true
        } else {
            var min = pukes.filter { it < 20 }.minOrNull()
            var max = pukes.filter { it < 20 }.maxOrNull()

            if (min == null || max == null) {//min为空就代码没 普通牌了
                pukes.add(curPuke)
                return true
            }

            if (!pukes.contains(curPuke)
                && ((curPuke > min && curPuke - min < 5) || (curPuke < max && max - curPuke < 5))
            ) {
                pukes.add(curPuke)
                return true
            }
        }
        return false
    }

}