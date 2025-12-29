package tasks.huodong

import androidx.compose.runtime.mutableStateOf
import tasks.CustomHeroDoing
import tasks.HeroDoing
import tasks.huodong.fengbao.*
import tasks.huodong.huangfeihong.*
import tasks.huodong.qiuxiang.QiuXiangDay2
import tasks.huodong.qiuxiang.QiuXiangDay3
import tasks.huodong.qiuxiang.QiuXiangDay7
import tasks.huodong.qiuxiang.QiuXiangDay8
import tasks.huodong.qiuxiang2.*
import tasks.huodong.qiuxiang3.QiuXiang3Day2
import tasks.huodong.qiuxiang3.QiuXiang3Day3
import tasks.huodong.qiuxiang3.QiuXiang3Day4
import tasks.huodong.sanguo.*
import tasks.huodong.shuihu.*
import tasks.huodong.shuihu3.*
import toLogData
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import java.util.Date
import kotlin.math.abs

object HuodongUtil {
    var state = mutableStateOf(false)
    var shuamoHeroDoing :HeroDoing?=null

    var mModel = 0

    fun start(model:Int) {
        mModel = model
        state.value = true
        shuamoHeroDoing?.stop()
//        shuamoHeroDoing = when(model){
//            1001-> QiuXiang2Day1()
//            1002-> QiuXiang2Day2()
//            1003-> QiuXiangDay3()
//            1004-> SGHero4()
////            1007-> QiuXiangDay7()
//            1007-> QiuXiangDay8()
//
//            else ->ShuihuHeroDoing1()
//        }
        shuamoHeroDoing = getHuodongDoing()
        shuamoHeroDoing?.init()
        shuamoHeroDoing?.start()
    }
    fun stop(){
        state.value = false
        shuamoHeroDoing?.stop()
    }



    var huodongStartTime = LocalDate.of(2025, 12, 2)
    var perCircleDate = 7// 7天一轮，担心不是七天
    private fun getHuodongDoing():HeroDoing?{
        if(mModel == 1002){
            return CustomHeroDoing(-1)
        }else if(mModel == 1003){
            return CustomHeroDoing(0)
        }

        var today = LocalDate.now()
        var dayDt = abs( ChronoUnit.DAYS.between(today, huodongStartTime).toInt())
        return when(dayDt%perCircleDate){
            0-> HFHHero1()
            1-> HFHHero2()
            2-> HFHHero3()
            3-> HFHHero4()
            4-> HFHHero5()
//            5-> Fengbao6()//强组，需要带别人，再让别人带一把小号
            6-> HFHHero7()
            else-> HFHHero7()
        }
    }
}