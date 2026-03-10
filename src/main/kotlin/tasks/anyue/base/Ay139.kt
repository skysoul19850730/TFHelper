package tasks.anyue.base

import data.Config
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import log
import tasks.SimpleHeZuoHeroDoing
import tasks.anyue.base.ay139.AY139Util
import utils.AYUtil

class Ay139(val heroDoing: BaseAnYueHeroDoing, val test: Boolean = true) : AnSub {

    var top4 = arrayListOf<AY139Util.MatType>()
    var differentMat: AY139Util.MatType? = null

    override fun addToHeroDoing() {
        addToHeroDoingUseBing()
    }

    var state = 0//0 冰  1停止冰

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

            if (test) {
                addGuanDeal(139){
                    onlyDo {
                        autoShibie()
                    }
                }

            } else {
                val bingqiu = heros.firstOrNull { it.heroName == "bingqiu" } ?: return //没冰球就不控
                addGuanDeal(139) {
                    over { curGuan > 139 || state == 2 }
                    chooseHero {
                        if (state == 0) {
                            daBing()
                        }
                        while (state == 1 && curGuan == 139) {
                            delay(200)
                        }

                        if(state==2)return@chooseHero -1


                        return@chooseHero indexOf(bingqiu)
                    }
                    onStart {
                        autoShibie()
                    }
                }
            }
        }
    }

    //350 150 446,173
    fun SimpleHeZuoHeroDoing.autoShibie() {
        GlobalScope.launch {
            delay(10000)//废话时间

            while (curGuan == 139 && (top4.size < 4 || differentMat == null)) {
                val tops = AY139Util.getTopMatTypes()
                if (tops != null) {
                    top4.clear()
                    top4.addAll(tops)
                    differentMat = AY139Util.theDifferentMat(tops)
                }
                delay(500)
            }

            log("顶部结果：${top4.joinToString(";") { it.toPString() }} 不同的是:${differentMat?.toPString()}")

            while (heroDoing.curGuan == 139 && state < 2) {
                val bottom = AY139Util.getBottomRunningMat()
                if (bottom == null) {
                    delay(200)
                } else {
                    log("底部结果：${bottom.toPString()}")
                    if (bottom.toPString() == differentMat?.toPString()) {//但这个不用操作，本身就一直冰
                        log("底部与不同一致,停止冰")
                        //是不同的那个
                        state = 0
                        delay(10000)
                        log("10秒后，解除冰")
                        state = 2
                    } else {
                        state = 1
                    }
                    delay(3000)//打死一个球的时间
                }
            }

        }
    }

}