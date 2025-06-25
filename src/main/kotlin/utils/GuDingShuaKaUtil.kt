package utils

import data.HeroBean
import kotlinx.coroutines.delay

/**
 * 设计之处，跳小野
 * 暂时只支持其他全满的时候（即不卡牌的时候）
 */
class GuDingShuaKaUtil( val heroBean:HeroBean,val delay:Long) {

    var lastTime = 0L

    var pred =false

    fun go(){
        pred = false
    }

    suspend fun dealHero(heros: List<HeroBean?>): Int {
        var result = heros.indexOf(heroBean)
        if(result<0)return result

        val now = System.currentTimeMillis()
        if(now-lastTime>delay){
            lastTime = now
            return result
        }

        delay(delay-(now-lastTime))
        lastTime = System.currentTimeMillis()
//        pred = true
//        while (pred){
//            delay(100)
//        }
        return result
    }



}