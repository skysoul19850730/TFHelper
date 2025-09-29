package tasks.huodong

import androidx.compose.runtime.mutableStateOf
import tasks.HeroDoing
import tasks.huodong.fengbao.*
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

    fun start(model:Int) {
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

    var huodongStartTime = LocalDate.of(2025, 9, 23)
    var perCircleDate = 7// 7天一轮，担心不是七天
    private fun getHuodongDoing():HeroDoing?{

        var today = LocalDate.now()
        var dayDt = abs( ChronoUnit.DAYS.between(today, huodongStartTime).toInt())
        return when(dayDt%perCircleDate){
            0-> Fengbao1()
            1-> Fengbao2()
            2-> Fengbao3()
            3-> Fengbao4()
            4-> Fengbao5()
            else->QiuXiang3Day2()
        }
    }
}