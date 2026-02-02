package tasks.anyue.base

import data.Config
import data.HeroBean
import getImage
import getImageFromRes
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import opencv.MatSearch
import opencv.toMat
import org.apache.commons.compress.harmony.pack200.PackingUtils.log
import org.opencv.core.*
import org.opencv.imgproc.Imgproc
import tasks.XueLiang
import utils.AYUtil
import java.awt.event.KeyEvent
import java.awt.image.BufferedImage
import java.io.File
import kotlin.math.abs

class An109(val heroDoing: BaseAnYueHeroDoing) : AnSub {

    private var count = 0
    override fun addToHeroDoing() {
        heroDoing.apply {
           addGuanDeal(109){
               over {
                   curGuan > 109 || count>2
               }
               chooseHero {

                   if(count==0){
                       delay(31*1000)
                       var index = indexOfFirst {
                           it?.heroName == "bingqiu"
                       }
                       count++
                       return@chooseHero index
                   }else if(count == 1|| count == 2){
                       delay(if(count==1) 2500 else 1000)
                       count++
                       return@chooseHero indexOfFirst {
                           it?.heroName == "bingqiu"
                       }
                   }

                   -1
               }

           }
        }
    }

}