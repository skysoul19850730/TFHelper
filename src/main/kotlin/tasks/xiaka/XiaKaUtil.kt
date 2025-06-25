package tasks.xiaka

import data.HeroBean
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import model.CarDoing

object XiaKaUtil {


    fun downPositions(chePos:Int, positions:List<Int>){
        var carDoing = CarDoing(chePos)
        carDoing.initPositions()
        carDoing.addHero(HeroBean("zhanjiang"))
        carDoing.addHero(HeroBean("nvwang"))
        carDoing.addHero(HeroBean("saman2"))
        carDoing.addHero(HeroBean("jiaonv"))
        carDoing.addHero(HeroBean("shahuang"))
        carDoing.addHero(HeroBean("niutou2"))
        carDoing.addHero(HeroBean("baoku", isGongCheng = true))

        GlobalScope.launch {
            positions.forEach {
                carDoing.downPosition(it)
            }
        }

    }

}