package tasks.anyue.base

import data.Config
import data.HeroBean
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import log
import tasks.SimpleHeZuoHeroDoing
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
        lastBing = System.currentTimeMillis()
    }

    private fun addToHeroDoingUseBing() {
        heroDoing.apply {
            val bingqiu = heros.firstOrNull { it.heroName == "bingqiu" }
            addGuanDeal(79) {
                over { curGuan > 79 || bossXue == 0 }
                chooseHero {
                    if (curPuke > 0 && needPuke()) {
                        val alreadyBingTime = (System.currentTimeMillis() - lastBing).coerceAtLeast(0)
                        delay(5000 - alreadyBingTime)//假设刚用了冰 5秒后能打死，已经冰了2秒了，就只需要等3秒
                        curPuke = 0
                    }
                    if (curPuke == 0) {
                        daBing()
                        return@chooseHero indexOf(bingqiu)
                    }
                    -1
                }
                onStart {
                    shibiePai()
                }
            }
        }
    }

    fun shibiePai() {
        GlobalScope.launch {
            while (heroDoing.curGuan == 79) {
                while (curPuke > 0) {
                    delay(500)
                }
                curPuke = AYUtil.getPuke()
                delay(200)
            }
        }
    }


    //    fun paiXueZai() = Config.AyPukeXuePoint.isFit()
    fun needPuke(): Boolean {
        var need = bossXue - curPuke < 0 || bossXue - curPuke > 10
        if (need) {
            bossXue -= curPuke
        }
        return need
    }
}