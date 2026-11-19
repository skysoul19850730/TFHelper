package tasks.anyue.base

import data.HeroBean
import data.MRect
import getImage
import getImageFromFile
import getSubImage
import java.awt.Color
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import tasks.SimpleHeZuoHeroDoing
import utils.hasColorCount
import java.io.File

//149，如果脚本车是右车，则另一车下一张卡，这边一卡控制，先下风向左，上卡就会向右（delay1秒下掉保持两边一样，下次风就会向左）
//如果脚本车是左车，则另一车不动，逻辑就和上面一致。
//如果是和群里车一样，实现另一个车上满卡，弄好星星数，就一直算上工程6个车位，即保持下一张卡得状态，那就要：
//******* 可以忽略另一种，这里不存在129得风险（因为这里风只有一趟，不需要在右边得时候要变向左边需要比另一边多出一张卡），没必要写两套逻辑了。 *******
//1.如果是右侧，刚好和第一行逻辑一样。
//2.如果是左车，逻辑就要变成，上来就下一张卡，保持平衡， 需要再下一张卡才能风向右边。延迟一秒后，赶紧上来，保持平衡
class Ay149(val heroDoing: BaseAnYueHeroDoing, var downHero1: HeroBean, var downHero2: HeroBean) : AnSub {

    var isLeft = heroDoing.chePosition == 0
    var guanStartTime = 0L
    var fengCheckTimeList = arrayListOf<Long>()
    override fun addToHeroDoing() {
        heroDoing.apply {
            addGuanDeal(149) {
                over {
                    var ov = false
                    if(isLeft){
                        ov =( (state == 3 || state == 5) && fulls(downHero1, downHero2))
                    }else{
                        //右侧时，state=3 或 =5 都可以，只要满上就结束了，只剩打英雄了，（某个形态下 需要打冰，但这里后面再考虑加点击按钮看boss属性的逻辑
                        ov =( (state == 3 || state == 5) && fulls(downHero1, downHero2))
                    }
                    curGuan > 149 || ov
                }
                chooseHero {

                    deal(this)
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
                    time = time + 10000
                    fengCheckTimeList.add(time)
                    time = time + 10000
                    fengCheckTimeList.add(time)

                    carDoing.downHero(downHero1)
                    autoShibie()
                }
            }
        }
    }

    private suspend fun BaseAnYueHeroDoing.deal(list: List<HeroBean?>): Int {
        if (isLeft) {

            if (state == 3) {
                return list.upAny(downHero1, downHero2)
            }

            if (state == 0) {
                var needUp = list.upAny(downHero1, downHero2, useGuang = false)
                if (needUp > -1) {
                    while (state == 0 && heroDoing.running && heroDoing.curGuan == 149) {
                        delay(100)
                    }
                    if (state == 1 || state==5) {//紫风飞5秒
                        delay(4000)
                    }
                    //下俩，飞右边去
                    if (state != 3) {
                        carDoing.downHero(downHero1)
                        carDoing.downHero(downHero2)

                        if(state==5){
                            //3个紫风了，保持下俩卡状态，只等所有黄风结束了
                            while(state == 5){
                                delay(200)
                            }
                        }else {
                            delay(1000)
                            //上卡回复平衡态
                            state = 0
                        }
                    }
                    return needUp

                } else {
                    return -1
                }
            }


        } else {

            if (state == 3 || state == 5) {
                return list.upAny(downHero1)
            }

            if (state != 0) {
                delay(1000)
                heroDoing.carDoing.downHero(downHero1)
                state = 0
            }

            var needUp = list.indexOf(downHero1)
            if (needUp > -1) {//预备好上的卡
                while (state == 0 && heroDoing.running && heroDoing.curGuan == 149) {
                    delay(100)
                }
                if (state == 1 || state == 5) {
                    delay(4000)
                }
                return needUp
            } else {
                return -1
            }

        }
        return -1
    }

    var state = 0 //0平衡状态,1.紫风状态，2黄凤状态    3 结束  5紫风3个了，可以不识别不动卡了，只等结束时满卡

    var fengCount = 0
    var zifengCount = 0

    //350 150 446,173
    private fun SimpleHeZuoHeroDoing.autoShibie() {
        GlobalScope.launch {
            while (zifengCount < 3 && running && curGuan == 149) {
                //改用时钟点，避免每次getFengType耗时，分析图片是第一个风16秒，后面每10秒一个
//                if (fengCount == 0) {
//                    delay(16000 - (System.currentTimeMillis() - guanStartTime))
//                } else {
//                    delay(10000 - (System.currentTimeMillis() - guanStartTime - 16000 - (fengCount-1)*10000))
//                }
                //比如任务开始时 赋值了checklist第0个，在8分16秒时开始，这里刚8分02秒，那么就delay14秒(
                delay(fengCheckTimeList.get(fengCount) - System.currentTimeMillis())

                getFengType()
            }
            if(zifengCount == 3){
                delay(fengCheckTimeList.last()+8000 - System.currentTimeMillis())
                state = 3
            }
        }
    }

    //紫风 198，110，228
    //黄蜂 255,255,160
    //531  73  170 80

    val rect = MRect.createWH(531, 73, 170, 80)
    val colorZi = Color(193, 110, 228)
    val colorYe = Color(255, 255, 160)

    private fun getFengType(): Int {
        //0 紫风，   1  黄风
        var type = -1

        while (type == -1) {
            //type = ?
            val img = getImage(rect)
            if (img.hasColorCount(colorZi) > 500) {
                type = 0
            } else if (img.hasColorCount(colorYe) > 500) {
                type = 1
            }
            if (type > -1) {
                fengCount++
                if (type == 1) {
                    state = 2
                } else if (type == 0) {
                    zifengCount++
                    if(zifengCount == 3){
                        state = 5
                    }else {
                        state = 1
                    }
                }
            }
        }

        return type
    }

}

fun main() {
    val rect = MRect.createWH(531, 73, 170, 80)
    val img = getImageFromFile(File("E:\\ideaspace\\TFHelperHome\\tfres\\window\\06_34_22_927.png")).getSubImage(rect)
    val colorZi = Color(193, 110, 228)
    val colorYe = Color(255, 255, 160)
    //mlgb 的，demo测试时没用到rect，直接整个图，打印1万多个count，保守写个7000，实际紫色rect中只有1000多点。md
    if (img.hasColorCount(colorZi, log = true) > 7000) {
    } else if (img.hasColorCount(colorYe, log = true) > 7000) {
    }
}