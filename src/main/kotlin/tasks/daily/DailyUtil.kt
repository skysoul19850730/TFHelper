package tasks.daily

import data.Config
import data.MPoint
import data.Recognize
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

object DailyUtil {


    fun doDaily(){
        GlobalScope.launch {
            //如果每日任务可以点



        }



    }

    /**
     * 商店
     */
    private suspend fun dailyStore(){

    }

    /**
     * 每日任务
     */
    private suspend fun dailyBox() {

        //点击“任务”
        //xxx.click

        //看箱子颜色
//        if(box.isyellow){
//            seeAdv(box,true){
//                box.isFit
//            }
//        }

        //看下面任务
        //listpoint
        //listpoint.foreach{

//    }


    }













    private suspend fun seeAdv(clickPoint:MPoint,hasGift:Boolean,checkAdvNotStart:()->Boolean){
        if(!checkAdvNotStart.invoke())return
        while (checkAdvNotStart.invoke()){
            //点一下，6秒后看看是否有 无广告，有的话就点了，没有的话下次进while如果还能进证明没广告，否则就应该已经进广告了
            clickPoint.click()
            delay(6000)
            if(Recognize.NoAdvOk.isFit()){
                Recognize.NoAdvOk.click()
                if(hasGift) {
                    delay(3000)
                    MPoint(100, 130).click()
                }
                return
            }
        }
        delay(3000)
        Config.adv_close.click()
        if(hasGift) {
            delay(3000)
            MPoint(100, 130).click()
        }
    }
}