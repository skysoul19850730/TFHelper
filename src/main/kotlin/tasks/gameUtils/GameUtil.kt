package tasks.gameUtils

import androidx.compose.runtime.mutableStateOf
import data.MPoint
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

object GameUtil {
    var ShuaMoValue = mutableStateOf(false)
    var shuamoHeroDoing :DaMoHeroDoing?=null

    var maiLuing = mutableStateOf(false)
    var SomeDelay = mutableStateOf(500L)
//    var maiLuHeroDoing:MaiLuHeroDoing?=null
    var maiLuHeroDoing:NiuKuangExchangeHeroDoing?=null

    var clickPoint = mutableStateOf(MPoint(0,0))
    fun startShuaMo(heroName:String) {
        shuamoHeroDoing?.stop()
        shuamoHeroDoing = DaMoHeroDoing(heroName)
        shuamoHeroDoing?.init()
        shuamoHeroDoing?.start()
    }
    fun stopShuaMo(){
        shuamoHeroDoing?.stop()
    }

    fun statrMaiLu(){
        maiLuHeroDoing?.stop()
        maiLuHeroDoing = NiuKuangExchangeHeroDoing()
        maiLuHeroDoing?.init()
        maiLuHeroDoing?.start()
    }
    fun stopMailu(){
        maiLuHeroDoing?.stop()
    }

}