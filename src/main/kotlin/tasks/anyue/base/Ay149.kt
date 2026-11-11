package tasks.anyue.base

import data.Config
import data.HeroBean
import data.MRect
import getImage
import java.awt.Color
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import log
import tasks.SimpleHeZuoHeroDoing
import tasks.anyue.base.ay139.AY139Util
import utils.AYUtil
import utils.hasColorCount

//149，如果脚本车是右车，则另一车下一张卡，这边一卡控制，先下风向左，上卡就会向右（delay1秒下掉保持两边一样，下次风就会向左）
//如果脚本车是左车，则另一车不动，逻辑就和上面一致。
//如果是和群里车一样，实现另一个车上满卡，弄好星星数，就一直算上工程6个车位，即保持下一张卡得状态，那就要：
//******* 可以忽略另一种，这里不存在129得风险（因为这里风只有一趟，不需要在右边得时候要变向左边需要比另一边多出一张卡），没必要写两套逻辑了。 *******
//1.如果是右侧，刚好和第一行逻辑一样。
//2.如果是左车，逻辑就要变成，上来就下一张卡，保持平衡， 需要再下一张卡才能风向右边。延迟一秒后，赶紧上来，保持平衡
class Ay149(val heroDoing: BaseAnYueHeroDoing, var downHero1: HeroBean, var downHero2: HeroBean) : AnSub {

    var isLeft = heroDoing.chePosition == 0
    override fun addToHeroDoing() {
        heroDoing.apply {
            addGuanDeal(149) {
                over { curGuan > 149 || (state == 3 && fulls(downHero1,downHero2)) }
                chooseHero {

                    deal(this)
                }
                onStart {
                    carDoing.downHero(downHero1)
                    autoShibie()
                }
            }
        }
    }

    private suspend fun BaseAnYueHeroDoing.deal(list:List<HeroBean?>): Int {
        if (isLeft) {

            if(state == 3){
                return list.upAny(downHero1,downHero2)
            }

            if(state == 0){
                var needUp = list.upAny(downHero1,downHero2,useGuang = false)
                if(needUp>-1){
                    while (state == 0 && heroDoing.running && heroDoing.curGuan == 149) {
                        delay(100)
                    }
                    if (state == 1) {//紫风飞5秒
                        delay(4800)
                    }
                    //下俩，飞右边去
                    if(state!=3) {
                        carDoing.downHero(downHero1)
                        carDoing.downHero(downHero2)
                        delay(1000)
                        //上卡回复平衡态
                        state = 0
                    }
                    return needUp

                }else{
                    return -1
                }
            }


        } else {

            if(state == 3){
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
                if (state == 1) {
                    delay(5000)
                }
                return needUp
            } else {
                return -1
            }

        }
        return -1
    }

    var state = 0 //0平衡状态,1.紫风状态，2黄凤状态    3 结束

    var fengCount = 0
    var huangfengCount = 0

    //350 150 446,173
    private fun SimpleHeZuoHeroDoing.autoShibie() {
        GlobalScope.launch {
            while (state!=3 && running && curGuan == 149) {
                if (fengCount == 0) {
                    delay(15000)
                } else {
                    delay(10000)
                }
                if(fengCount == 6){//等6次风后，又delay10秒后，改state=3，因为等3后就开始满卡了,如果还有风就会影响风向
                    state = 3
                }else {
                    getFengType()
                }
            }
        }
    }

    //紫风 198，110，228
    //黄蜂 255,255,160
    //531  73  170 80

    val rect = MRect.createWH(531,73,170,80)
    val colorZi = Color(193, 110, 228)
    val colorYe = Color(255,255,160)

    private fun getFengType(): Int {
        //0 紫风，   1  黄风
        var type = -1

        while(type == -1) {
            //type = ?
            val img = getImage(rect)
            if(img.hasColorCount(colorZi)>1000){
                type = 0
            }else if(img.hasColorCount(colorYe)>1000){
                type = 1
            }
            if(type > -1) {
                fengCount++
                if (type == 1) {
                    state = 2
                    huangfengCount++
                } else if (type == 2) {
                    state = 1
                }
            }
        }
        return type
    }

}