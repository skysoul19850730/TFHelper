package tasks.anyue.base

import data.Config
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import log
import tasks.anyue.base.ay139.AY139Util
import utils.AYUtil

class Ay139(val heroDoing: BaseAnYueHeroDoing) : AnSub {

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
            val bingqiu = heros.firstOrNull { it.heroName == "bingqiu" }?:return //没冰球就不控
            addGuanDeal(139) {
                over { curGuan > 139 }
                chooseHero {
                    if(state==0){
                        daBing()
                    }else{
                        while(state == 1){
                            delay(200)
                        }
                        daBing()
                    }
                    return@chooseHero indexOf(bingqiu)
                }
                onStart {
                    autoShibie()
                }
            }
        }
    }

    //350 150 446,173
    fun autoShibie() {
        GlobalScope.launch {
            delay(3000)//废话时间

            while (top4.size < 4 || differentMat == null) {
                val tops = AY139Util.getTopMatTypes()
                if (tops != null) {
                    top4.clear()
                    top4.addAll(tops)
                    differentMat = AY139Util.theDifferentMat(tops)
                }
                delay(1000)
            }

            while (heroDoing.curGuan == 139){
                val bottom = AY139Util.getBottomRunningMat(top4)
                if(bottom==null){
                    delay(300)
                }else {
                    if (bottom == differentMat) {//但这个不用操作，本身就一直冰
                        //是不同的那个
                        state = 0
                    } else {
                        state = 1
                    }
                    delay(3000)//一个球的时间
                }
            }

        }
    }

}