package tasks.anyue.base

import data.HeroBean
import kotlinx.coroutines.delay
import tasks.SimpleHeZuoHeroDoing
import tasks.XueLiang

class An69(val heroDoing: SimpleHeZuoHeroDoing,val qius:List<HeroBean>):AnSub {

    var status = 0//0等血  1打球 2打完球

    var bossXue = 1f
    override fun addToHeroDoing(){
        heroDoing.apply {
            addGuanDeal(69){
                over { curGuan>69 || status==2}
                chooseHero {
                     bossXue = XueLiang.getBossXueliang()
                    while(status==0 && curGuan<70){
                        if(bossXue>0 && bossXue<0.5){
                            delay(300)
                            var xue = XueLiang.getBossXueliang()
                            if(xue == bossXue){
                                //第一次判断相等，有可能是boss自己回血导致刚好回到上次观察的血量
                                //延迟300毫秒再获取一次血量，如果还是相同就代表无敌了（boss回血频率比较低)
                                delay(300)
                                xue = XueLiang.getBossXueliang()
                                if(xue==bossXue){
                                    status = 1
                                }else{
                                    bossXue = xue
                                }
                            }else{
                                bossXue = xue
                            }
                        }else{
                            delay(500)
                            bossXue = XueLiang.getBossXueliang()
                        }

                    }
                    if(status==1){
                        delay(300)
                       var xueNow = XueLiang.getBossXueliang()
                        if(xueNow<bossXue){
                            //被打掉血了开始
                            status=2
                            return@chooseHero -1
                        }
                        upAny(qius)
                    }else {
                        status=2
                        -1
                    }
                }
            }
        }
    }

}