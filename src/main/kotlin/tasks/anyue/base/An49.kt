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

class An49(val heroDoing: BaseAnYueHeroDoing) : AnSub {

    private var state = 0//0冰  1停止冰

    override fun addToHeroDoing() {
        heroDoing.apply {
            gudingShuaQiuTask("bingqiu",49,2500, customOverJudge = {
                state==1
            }, onGuanDealStart = {
                shibieQiu()
            })
        }
    }

    private suspend fun shibieQiu(){
        heroDoing.apply {
            val xuetiao = getImageFromRes("xiaochengxu/tezheng/xuetiao.png").toMat()
            var hasQiu = -1//0红，1蓝
            while (curGuan==49){
                delay(300)
                val img = getImage(Config.rectCheckOfLeishen)
                if(hasQiu<0 && MatSearch.templateFit(xuetiao,img.toMat())){
                    //有血条
                    var count = Config.rectCheckOfLeishen.hasColorCount(Config.colorLeishenHongqiu, testImg = img)
                    var count2 = Config.rectCheckOfLeishen.hasColorCount(Config.colorLeishenLanqiu, testImg = img)
                    if(count>300){
                        hasQiu = 0
                    }else if(count2>500){
                        hasQiu = 1
                    }else{
                        continue
                    }
                }
                var same = false
                if(hasQiu>=0){
                    if(hasRedShield(img.toMat())){
                        if(hasQiu==0){
                            same = true
                        }
                    }else if(hasQiu==1){
                        same = true
                    }

                    if(same){
                        XueLiang.observerXueDown { curGuan > 49 }
                        state = 1
                        break
                    }
                }


            }
        }
    }

    private fun hasRedShield(img:Mat): Boolean {

        var bossRect = Rect(770,150,200,75)

        val roi = img.submat(bossRect)
        val hsv = Mat()
        Imgproc.cvtColor(roi, hsv, Imgproc.COLOR_BGR2HSV)

        val mask = Mat()
        // 合并红区
        Core.inRange(hsv, Scalar(0.0, 100.0, 120.0), Scalar(15.0, 255.0, 255.0), mask)
        val mask2 = Mat()
        Core.inRange(hsv, Scalar(165.0, 100.0, 120.0), Scalar(180.0, 255.0, 255.0), mask2)
        Core.bitwise_or(mask, mask2, mask)

        // 形态学增强
        val kernel = Mat.ones(3, 3, CvType.CV_8U)
        Imgproc.morphologyEx(mask, mask, Imgproc.MORPH_CLOSE, kernel)

        val count = Core.countNonZero(mask)
        roi.release(); hsv.release(); mask.release(); mask2.release()
        println("hasredcount ${count}")
        return count > 500 // 阈值可根据实测调整
    }
}