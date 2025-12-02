package tasks.huodong.huangfeihong

import data.HeroBean
import kotlinx.coroutines.delay
import tasks.SimpleHeZuoHeroDoing

open class BaseHFH:SimpleHeZuoHeroDoing() {


   lateinit var upHeros: List<HeroBean>
    var qiuTime:Long = 2000L
    var qiu:HeroBean? = null
    override fun initHeroes() {

        addGuanDeal(0){
            over { false }
            chooseHero {
                var index = upAny(*upHeros.toTypedArray(), useGuang = false)
                if(index>-1){
                    index
                }else{
                   if(qiu!=null){
                       index = indexOf(qiu!!)
                       if(index>-1){
                           if(System.currentTimeMillis()> lastQiuTime+qiuTime){
                               lastQiuTime=System.currentTimeMillis()
                               index
                           }else {
                               delay(300)
                               -1
                           }
                       }else -1
                   }else -1
                }

            }
            onStart {
                lastQiuTime = System.currentTimeMillis() +55000
            }
        }

        curGuanDeal = guanDealList[0]
    }


}