package tasks.anyue.base

import data.Config
import getImage
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
    var timePreBing = 3200L
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
                        if (state == 1 && curGuan == 139) {
                            delay(4000)//这里的时间基本是纯粹攻击的时间，因为dabing()这个方法，就是按
                            //车主冰的等级时间 来按上次打冰时间进行了等待，如果没有这里就相当于 等冰快结束才扔冰
                            //所以代码到这里时，其实就是上一个冰结束了。
                            //这里控制（假装时间足够打死了）打死后，state =0后再继续识别，因为识别出
                            //需要打死时，识别线程会等打死后再继续识别。原逻辑是固定等4秒，但不知道是不是刚刚冰完
                            //所以可能0.几秒后就又开始识别，可能造成state混乱
                            state = 0
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
            log(getImage())
            MainData.curGuanKaDes.value = "顶部不同的是:${differentMat?.toPString()}"


            while (heroDoing.curGuan == 139 && state < 2) {
                val bottom = AY139Util.getBottomRunningMat()
                if (bottom == null) {
                    delay(100)
                } else {
                    log("底部结果：${bottom.toPString()}")
                    if (bottom.toPString() == differentMat?.toPString()) {//但这个不用操作，本身就一直冰
                        log("底部与不同一致,继续冰")
                        //是不同的那个
                        state = 0
                        delay(10000)
                        log("10秒后，解除冰，不再打冰")
                        state = 2
                    } else {
                        log("暂停打冰，3秒后恢复打冰")
                        state = 1
                        while(state == 1){
                            delay(200)
                        }
                    }

                    //这里不能简单3秒后，因为上一个冰球可能刚用完，delay3秒也才醒0.x秒就又要冰额，所以要关联上次冰的时间
                    //如果3秒能打死，要精准delay  冰的效果完3秒
//                    delay(4000)//打死一个球的时间//放流程里打冰延迟
//
//                    if(state!=2){//恢复冰
//                        state = 0
//                    }
                }
            }

        }
    }

}