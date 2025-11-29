package tasks

import MainData
import data.HeroBean
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import log
import java.awt.event.KeyEvent

open class SimpleHeZuoHeroDoing : HeroDoing(0, FLAG_GUANKA or FLAG_KEYEVENT) {

    var dropThisDeal = false

    fun fulls(vararg heros: HeroBean): Boolean {
        return heros.all {
            it.isFull()
        }
    }

    fun inCars(vararg heros: HeroBean): Boolean {
        return heros.all {
            it.isInCar()
        }
    }

    val noHuanqiu:Boolean
        get() = !(heros.any {
            it.heroName == "huanqiu"
        })

    val longxin: Boolean
        get() = (Zhuangbei.isLongxin() && Zhuangbei.hasZhuangbei())|| noHuanqiu
    val qiangxi: Boolean
        get() = (Zhuangbei.isQiangxi() && Zhuangbei.hasZhuangbei())|| noHuanqiu
    val yandou: Boolean
        get() = (Zhuangbei.isYandou() && Zhuangbei.hasZhuangbei())|| noHuanqiu
    val shengjian: Boolean
        get() = (Zhuangbei.isShengjian() && Zhuangbei.hasZhuangbei())|| noHuanqiu

    suspend fun Int.notOk(block: suspend () -> Int): Int {
        if (this < 0) return block()
        return this
    }

    suspend fun List<HeroBean?>.upSingleHero(hero: HeroBean, useGuang: Boolean = true): Int {
        if (hero.isFull()) return -1
        return indexOf(hero).notOk {
            if (hero.isInCar() && useGuang) {
                indexOfFirst {
                    it?.heroName == "guangqiu"
                }
            } else -1
        }
    }

    fun List<HeroBean?>.upAny(heros:List<HeroBean>, zhuangbei: (() -> Boolean)? = null,
                              useGuang: Boolean = true
    ): Int {
       return upAny(*heros.toTypedArray(), zhuangbei = zhuangbei, useGuang = useGuang)
    }
    fun List<HeroBean?>.upAny(
        vararg heros: HeroBean,
        zhuangbei: (() -> Boolean)? = null,
        useGuang: Boolean = true
    ): Int {
        heros.forEach {
            var index = indexOf(it)
            if (index > -1) {
                return index
            }
        }

        if (zhuangbei != null && !noHuanqiu) {
            var index = zhuangbei { zhuangbei() }
            if (index > -1) {
                return index
            }
        }

        if (useGuang) {
            if (heros.filter { it.isInCar() && !it.isFull() }.isNotEmpty()) {
                return indexOfFirst {
                    it?.heroName == "guangqiu"
                }
            }
        }
        return -1
    }

    fun List<HeroBean?>.zhuangbei(block: () -> Boolean): Int {
        if (!block() && Zhuangbei.hasZhuangbei()) {
            return indexOfFirst {
                it?.heroName == "huanqiu"
            }
        }
        return -1
    }

    class GuanDeal(
        var startGuan: Int,
        var isOver: () -> Boolean = { true },
        var chooseHero: suspend List<HeroBean?>.() -> Int = { -1 },
        var onGuanDealStart: (suspend () -> Unit)? = null,
        var onGuanDealEnd: (suspend () -> Unit)? = null,
        var onlyDoSomething: (suspend (() -> Unit))? = null,//遇到管卡做件事，比如开始截图，或者下个猴子之类的。不赋值管卡
    ) {
        var des = ""

        fun over(isOver: () -> Boolean){
            this.isOver = isOver
        }

        fun chooseHero(chooseHero: suspend List<HeroBean?>.() -> Int) {
            this.chooseHero = chooseHero
        }

        fun onStart(onGuanDealStart: suspend () -> Unit) {
            this.onGuanDealStart = onGuanDealStart
        }

        fun onEnd(onGuanDealEnd: suspend () -> Unit) {
            this.onGuanDealEnd = onGuanDealEnd
        }

        fun onlyDo(onlyDoSomething: suspend (() -> Unit)) {
            this.onlyDoSomething = onlyDoSomething
        }
    }


    fun addGuanDeal(guan: Int, guanDeal: GuanDeal.() -> Unit) {

        guanDealList.add(GuanDeal(guan).apply {
            guanDeal.invoke(this)
        })
    }

    fun addGuanDealWithHerosFull(guan:Int,fullHeros:List<HeroBean>, downHeros:List<HeroBean>?=null,zhuangbei: (() -> Boolean)? = null,delay:Long = 0L){
        addGuanDeal(guan){
            over {
                fulls(*fullHeros.toTypedArray()) && zhuangbei?.invoke()?:true
            }
            chooseHero {
                upAny(*fullHeros.toTypedArray(),zhuangbei=zhuangbei)
            }
            onStart {
                downHeros?.forEach {
                    carDoing.downHero(it)
                }
                if(delay>0){
                    delay(delay)
                }
            }
        }
    }



    var curGuanDeal: GuanDeal? = null

    private fun changeGuanKa(guan: Int, changeTo: GuanDeal) {


        if (curGuanDeal == changeTo) {
            throw Exception()
        }
        if (guan >= changeTo.startGuan) {

            if (changeTo.onlyDoSomething != null) {//only do的不改变关卡
                log("guandeal :${changeTo.startGuan},onlyDo ")
                GlobalScope.launch {
                    changeTo.onlyDoSomething!!.invoke()
                }
                guanDealList.remove(changeTo)
                throw Exception()
                return
            }

            curGuanDeal = changeTo
            MainData.curGuanKaDes.value = curGuanDeal?.des ?: ""
            log("curGuanDeal is ${curGuanDeal!!.startGuan}")
            GlobalScope.launch {
                curGuanDeal?.onGuanDealStart?.invoke()
                if (curGuanDeal?.isOver?.invoke() == true) {//start中判断不需要执行此deal，可以将waiting置true，这样直接触发end
                    curGuanDeal?.onGuanDealEnd?.invoke()
                } else {
                    waiting = false
                }
            }
            throw Exception()
        }
    }

    var guanDealList: ArrayList<GuanDeal> = arrayListOf()


    fun isGkOver(g: GuanDeal?): Boolean {
        if (g == null) return true
        return g.isOver.invoke()
    }

    override suspend fun doAfterHeroBeforeWaiting(heroBean: HeroBean) {
        if (heroBean.heroName == "muqiu") {
            return
        }
        if (!waiting && isGkOver(curGuanDeal)) {
            waiting = true
            curGuanDeal?.onGuanDealEnd?.invoke()
        }
    }

    override fun onGuanChange(guan: Int) {
        try {
            guanDealList.reversed().forEach {
                changeGuanKa(guan, it)
            }
        } catch (e: Exception) {

        }
    }

    override fun initHeroes() {
    }

    override suspend fun dealHero(heros: List<HeroBean?>): Int {
        while (waiting) {
            delay(100)
        }

        if (dropThisDeal) {//特殊情况下，有时会保留上次的预选卡组，但有手动改变了卡组。导致再waiting变false后继续执行时，依然认为是之前卡组
            //所以这里有这个特殊情形时（比如爱神时要幻强袭，幻完后waiting变true，同时卡组里有木，这个木会计到船长那里，但爱神会手动上下圣骑
            //所以这个木已经没有了，但程序认为还在。）可以在改变waitting等false前，把drop设置true，这样deal就直接返回-1，会重新刷新想要的卡
            dropThisDeal = false
            return -1
        }

        if (curGuanDeal?.isOver?.invoke() == true) {
            waiting = true
            return -1
        }

        return curGuanDeal?.chooseHero?.invoke(heros) ?: -1
    }

    override fun changeHeroWhenNoSpace(heroBean: HeroBean): HeroBean? {
        return null
    }

    var lastQiuTime = 0L
    var qiuAutoBeginTime = Long.MAX_VALUE
    var qiuStopFlag = false
    var qiuPlaying = false
    fun gudingShuaQiuTask(name: String, startGuan: Int, timePer: Long, allTime: Long? = null, overGuan: Int? = null
    ,dealTime :Long = 0L,sholudPasue:(suspend ()->Unit)?=null,customOverJudge:(()->Boolean)?=null) {

        var delayed = false

        guanDealList.add(GuanDeal(
            startGuan = startGuan,
            isOver = {
                if (qiuStopFlag) {
                    true
                } else {
                    if(customOverJudge!=null){
                        customOverJudge.invoke()
                    }else if (overGuan != null) {
                        curGuan > overGuan
                    } else if (allTime != null) {
                        System.currentTimeMillis() - qiuAutoBeginTime > allTime
                    } else qiuStopFlag
                }
            },
            chooseHero = {
                var index = indexOfFirst {
                    it?.heroName == name
                }
                if (index > -1) {
                    if(dealTime>0 && !delayed){
                        delay(dealTime)
                        delayed = true
                    }
                    while (System.currentTimeMillis() - lastQiuTime < timePer && !qiuStopFlag) {
                        delay(100)
                    }
                    sholudPasue?.invoke()
                    lastQiuTime = System.currentTimeMillis()
                }
                index
            },
            onGuanDealStart = {
                qiuPlaying = true
                qiuStopFlag = false
                if (allTime != null)
                    qiuAutoBeginTime = System.currentTimeMillis()
            },
            onGuanDealEnd = {
                qiuPlaying = false
                qiuAutoBeginTime = Long.MAX_VALUE
                qiuStopFlag = false
            }
        ).apply {
            des = "定数刷球，按3可停止刷球"
        })


    }

    fun changeZhuangbei(guan: Int, zhuangbei: () -> Boolean) {
        guanDealList.add(
            GuanDeal(
                guan,
                isOver = zhuangbei,
                chooseHero = {
                    upAny(zhuangbei = zhuangbei)
                }
            )
        )
    }
    override suspend fun onKeyDown(code: Int): Boolean {
        if (code == KeyEvent.VK_NUMPAD3 && qiuPlaying) {
            qiuStopFlag = true
        }
        if (code == KeyEvent.VK_NUMPAD9) {//9强制改变waitting，防止waiting有逻辑错误不上卡
            waiting = !waiting
            return true
        }


        return super.onKeyDown(code)
    }

    fun changeHero(guan:Int, down:HeroBean, up:HeroBean){
        addGuanDeal(guan){
            over {
                up.isFull()
            }
            chooseHero {
                upAny(up)
            }
            onStart {
                carDoing.downHero(down)
            }
        }

    }
}