package tasks.anyue.base

import data.Config
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import utils.AYUtil

class Ay99(val heroDoing: BaseAnYueHeroDoing) : AnSub {

    var bossXue = 120
    var curPuke = 0
    var pukes = arrayListOf<Int>()
    var state = 0
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
            addGuanDeal(99) {
                over { curGuan > 99 || bossXue <= 0 }
                chooseHero {
                    if (curPuke > 0 && needPuke()) {
                        delay(6000-(System.currentTimeMillis()-shibieTime))
                        indexOf(bingqiu)
                    }else{
                        daBing()
                        if (curPuke > 0 && needPuke()) {
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
            while (heroDoing.curGuan == 99) {
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

    fun paiXueZai() = Config.AyPukeXuePoint.isFit()

    var count4State2 = 0//2阶段已吃牌数
    fun needPuke(): Boolean {
        if (pukes.size == 5) {
            state = 1
            if (bossXue == 120) {
                var num = 0
                pukes.forEach {
                    num += it
                }
                bossXue = 120 - num
            }
        }
        if (state == 0) {
            val wangCount = pukes.filter { it == 20 }.size
            var min = 8 - wangCount
            if (curPuke < min) return false//第一阶段太小的牌不能要
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
        } else {
            //二阶段  如果一阶段拿了8910jq 那就才50点，还需要70点，平均要到14点，所以必须得有王
            //50/4= 12.5

            if (curPuke >= bossXue) {
                bossXue-=curPuke
                return true
            } else if (bossXue - curPuke <= 10) {
                return false //吃掉后小于等于10就失败了，不能要
            } else {

                var min = bossXue - (count4State2 - 1) * 20  //假设后面都等王，这张牌最小可以是多少
                //就算70，我都可以拿1，69，就最低拿9，剩下就必须都是王了
                var avg = bossXue / count4State2 //平均要拿这么多，然后每一步取

                if(curPuke<min){
                    return false
                }
                if (count4State2 == 3) {
                    //单独做一下剩两张得逻辑,这种吃了等于没吃，吃前一个王可以解决，吃后还要一个王解决
                    //所以这种就不吃，要等一个吃了之后能多一个k得选项至少
                    if(bossXue in 14..20){
                        if(bossXue-curPuke in 14..20){
                            return false
                        }
                    }

                }
                if(avg>13){
                    avg = 13
                }

                if(avg-min>5){//min 和 avg 差距较大时，要尽量拿打牌

                    if( curPuke>=  (min+avg)/2){
                        bossXue-=curPuke
                        return true
                    }
                    return false
                }

                bossXue-=curPuke

                return true

            }
        }
        return false
    }

}