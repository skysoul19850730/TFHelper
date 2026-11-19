package tasks.anyue.base

import data.Config
import getImage
import kotlin.math.min
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


    var guanStartTime = 0L
    var fengCheckTimeList = arrayListOf<Long>()

    override fun addToHeroDoing() {
        addToHeroDoingUseBing()
    }

    var state = 0//0 开头打个冰， 1//开启识别  ，2 一样的球，  3 不一样的球， 4 一样的那个球结束了，永远不再打冰也不需要操作了

    var lastBing = 0L
    var timePreBing = 3200L

    var curBottomIndex = 0

    private fun addToHeroDoingUseBing() {
        heroDoing.apply {

            if (test) {
                addGuanDeal(139) {
                    onlyDo {
                        autoShibie()
                    }
                }

            } else {
                val bingqiu = heros.firstOrNull { it.heroName == "bingqiu" } ?: return //没冰球就不控
                addGuanDeal(139) {
                    over { curGuan > 139 || state == 4 }
                    chooseHero {

                        if (state == 0) {
                            delay(fengCheckTimeList.get(curBottomIndex) - 500 - System.currentTimeMillis())
                            state = 1
                            lastBing = System.currentTimeMillis()
                            return@chooseHero indexOf(bingqiu)
                        }
                        while(state == 1){
                            delay(100)
                        }

//                        if (state == 1) {
//                            var bottom = AY139Util.getBottomRunningMat()
//                            while (heroDoing.curGuan == 139 && state < 2 && bottom == null) {
//                                bottom = AY139Util.getBottomRunningMat()
//                            }
//                            if(bottom == null)return@chooseHero -1
//                            log("底部结果：${bottom.toPString()}")
//                            if (bottom.toPString() == differentMat?.toPString()) {//但这个不用操作，本身就一直冰
//                                log("底部与不同一致,继续冰")
//                                //是不同的那个
//                                state = 2
//                            } else {
//                                log("暂停打冰，3秒后恢复打冰")
//                                state = 3
//                            }
//                        }
                        if(state == 2) {
                            //下面的注释先保留，都错了，state =  2只需要处理成 刚好把自己冰完，撞的上就可以了，不需要管下一个球了，因为撞一个就可以结束了

                            //要一直打冰，判断能依赖state=0的时候的冰 接上，就可以把state赋值0，下次刷卡就走state = 0 了
                            val time1 = timePreBing - (System.currentTimeMillis() - lastBing)//实际的冰间隔

                            //一定在time2或time2多一点点的时候打一个冰，然后state=0，这里用-2500，state = 0是-500左右，留2000秒足够进入下一轮识别完之后进入state=0的分支了，而此时可以冰3000ms呢，足够
                            val time2 = fengCheckTimeList.get(min(curBottomIndex+1,3)) - 2500 - System.currentTimeMillis()


                            //这里主要要给最后一下的冰留下时间 避免time1=3000,time2 = 3200这种情况，下一轮就只剩200ms了还是识别和打出去，
                            //time2小于time1自然就直接time2了。如果差大于1500，就用time1就可以，即还有1500ms给下一轮。否则的话，就按time2-1500强行留1500给下一轮
//                            val time = if(time2<time1) time2 else if((time2> time1+1500)) time1 else time2 -1500
//                            delay(time)
//                            if(fengCheckTimeList.get(min(curBottomIndex+1,3)) - System.currentTimeMillis()<timePreBing){
//                                state = 4
//                            }

                            ////上面都错了，根本不需要管下一个球了，这个state==2的时候，只要考虑距离下一个出来2500秒的时候左右能扔一个球就足够足够的了，即打不死当前这个就可以了，因为后面就不冰了

                            val minTime = min(time1, time2)
                            delay(minTime)
                            if(minTime == time2){//如果使用的时time2，即这个球扔完，2500ms后就是下一个球了，state设置为4，后面就不冰了，一直打
                                state = 4
                            }

                            lastBing = System.currentTimeMillis()
                            return@chooseHero indexOf(bingqiu)

                        }

                        if( state == 3) {

                            //这个状态下，就是直接打死的, 啥都不上，state改0，下一轮就进state=0了，而state=0是按下一个球的出现时间-500来打冰的
                            //所以  curBottomIndex++ ，加入cur = 0，上来打冰，识别到state==3，那cur=1，下一轮识别就进state = 0 ，就会取1的出现时间-500ms来打冰
                            //所以通篇逻辑的保障就是，每个球出来前500ms我就打冰，打完识别到英雄后马上识别底部出现的（所以这里的识别卡在了下一轮里，加入识别英雄卡了2000ms，那就出问题了，不过要相信识别，识别有问题，那就处理识别）
                            //当然也可以保留最早逻辑，就是识别底部那里的，自己定时好，到时间就识别，然后deal这里只需要 while(state == 1)卡住就行了。
                            state = 0
                            curBottomIndex++
                            return@chooseHero -1
                        }

                        return@chooseHero indexOf(bingqiu)
                    }
                    onStart {
                        guanStartTime = System.currentTimeMillis()
                        var time = guanStartTime + 16000
                        fengCheckTimeList.add(time)
                        time = time + 10000
                        fengCheckTimeList.add(time)
                        time = time + 10000
                        fengCheckTimeList.add(time)
                        time = time + 10000
                        fengCheckTimeList.add(time)
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

            while (curGuan == 139 && (top4.size < 4 || differentMat == null) && running) {
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


            while (heroDoing.curGuan == 139 && state != 4 && heroDoing.running) {

                while(state!=1 && heroDoing.curGuan == 139 && state != 4 && heroDoing.running){//没通知 state =1 识别，就不要识别
                    delay(100)
                }

                if(state == 1) {
                    val bottom = AY139Util.getBottomRunningMat()
                    if (bottom == null) {
                        delay(100)
                    } else {
                        log("底部结果：${bottom.toPString()}")
                        if (bottom.toPString() == differentMat?.toPString()) {//但这个不用操作，本身就一直冰
                            log("底部与不同一致,继续冰")
                            //是不同的那个
                            log("10秒后，解除冰，不再打冰")
                            state = 2
                        } else {
                            log("暂停打冰，3秒后恢复打冰")
                            state = 3
                        }
                    }
                }
            }

        }
    }

}