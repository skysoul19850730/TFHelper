package tasks.huodong.huangfeihong

import data.HeroBean
import kotlinx.coroutines.delay
import log
import tasks.SimpleHeZuoHeroDoing

open class BaseHFH:SimpleHeZuoHeroDoing() {


   lateinit var upHeros: List<HeroBean>
    var qiuTime:Long = 2000L
    var qiu:HeroBean? = null
    override fun initHeroes() {
        lastQiuTime = System.currentTimeMillis() +55000
        addGuanDeal(0){
            over { false }
            chooseHero {
                var index = upAny(*upHeros.toTypedArray(), useGuang = false)
                var gindex = this.indexOfFirst { it?.heroName=="guangqiu" }
                if(index>-1){
                    index
                }else{
                   if(qiu!=null){
                       index = indexOf(qiu!!)
                       if(index>-1){
                           if(System.currentTimeMillis()> lastQiuTime+qiuTime){
                               log("first qiu when lastQiutime is ${lastQiuTime}")
                               lastQiuTime=System.currentTimeMillis()
                               index
                           }else {
                               delay(300)
                               gindex
                           }
                       }else gindex
                   }else gindex
                }

            }
            //直接塞进来得guandeal 0 的 onStart不走。。。。
//            onStart {
//                lastQiuTime = System.currentTimeMillis() +55000
//                log("onStart lastqiutime is ${lastQiuTime}")
//            }
        }

        curGuanDeal = guanDealList[0]
    }


}