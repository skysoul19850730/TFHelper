package tasks.huodong.huangfeihong

import data.HeroBean
import kotlinx.coroutines.delay
import log
import tasks.SimpleHeZuoHeroDoing

open class BaseHFH : SimpleHeZuoHeroDoing() {


    lateinit var upHeros: List<HeroBean>
    var qiuTime: Long = 2000L
    var qiu: HeroBean? = null

    var state = 0

    override fun initHeroes() {
        lastQiuTime = System.currentTimeMillis() + 55000
        addGuanDeal(0) {
            over { !running }
            chooseHero {
                if (state == 0) {
                    if (fulls(*upHeros.toTypedArray())) {
                        state = 1
                    }
                }
                if (state == 0) {
                    upAny(upHeros)
                } else {
                    if (qiu != null) {
                        val index = indexOf(qiu!!)
                        if (index > -1) {
                            if (System.currentTimeMillis() > lastQiuTime + qiuTime) {
                                log("first qiu when lastQiutime is ${lastQiuTime}")
                                lastQiuTime = System.currentTimeMillis()
                                index
                            } else {
                                delay(lastQiuTime + qiuTime - System.currentTimeMillis())
                                lastQiuTime = System.currentTimeMillis()
                                index
                            }
                        } else -1
                    } else -1
                }

            }
        }

        curGuanDeal = guanDealList[0]
    }


}