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
    var timePreBing = 2800L
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
                        delay(6000-(System.currentTimeMillis()-shibieTime))
                        indexOf(bingqiu)
                    }else{
                        daBing()
                        if (curPuke > 0 && needPuke()) {//要的就等6秒
                            delay(6000-(System.currentTimeMillis()-shibieTime))
                        }
                        indexOf(bingqiu)
                    }


                }
                onStart {
                    shibiePai()
                }
            }
        }
    }

    var shibieTime = 0L

    fun shibiePai() {
        GlobalScope.launch {
            while (heroDoing.curGuan == 79) {
//                while (curPuke > 0) {
//                    delay(500)
//                }
                curPuke = AYUtil.getPuke()
                if(curPuke>0){
                    shibieTime = System.currentTimeMillis()
                    delay(6000-(System.currentTimeMillis()-shibieTime))
                    curPuke = 0
                }
                delay(100)
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