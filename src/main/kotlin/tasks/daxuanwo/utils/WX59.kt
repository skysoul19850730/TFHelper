package tasks.daxuanwo.utils

import MainData
import data.MPoint
import data.MRect
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import tasks.WxUtil
import tasks.anyue.base.AnSub
import ui.MainUIData
import utils.CaijiUtil
import utils.XWUtil

object WX59 {
    val keyboard59Map = mapOf(
        "1" to MPoint(460,385),
        "4" to MPoint(460,385-66),
        "7" to MPoint(460,385-66*2),
        "2" to MPoint(460+76,385),
        "5" to MPoint(460+76,385-66),
        "8" to MPoint(460+76,385-66*2),
        "3" to MPoint(460+76*2,385),
        "6" to MPoint(460+76*2,385-66),
        "9" to MPoint(460+76*2,385-66*2),
    )


    //57.35
    //57.42+2
    //46+2
    //50
    suspend fun clickKey(num:String){
        keyboard59Map.get(num)?.clickPc()
    }
    fun autoDo(autoClick:Boolean) {
        val list = arrayListOf<Int>()
        GlobalScope.launch {
            delay(9000)
            list.add( XWUtil.getShuzi59())
            logList(list)
            while (list.size<6){
                delay(4000)
                list.add( XWUtil.getShuzi59())
                logList(list)
            }

            if(autoClick) {
                delay(12000)
                list.forEach {

                    clickKey(it.toString())
                    delay(300)
                }
            }
        }
    }

    private fun logList(list:ArrayList<Int>){
        MainData.curGuanKaDes.value = "识别到密码指令:${list.joinToString { it.toString() }}"
    }

    fun caiji(){

//        CaijiUtil.saveRectByFolder(App.caijiPath+"\\xw59\\1",rect1)
//        CaijiUtil.saveRectByFolder(App.caijiPath+"\\xw59\\2",rect2)

    }

}