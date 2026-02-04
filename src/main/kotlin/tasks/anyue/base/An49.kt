package tasks.anyue.base

import data.Config
import data.HeroBean
import getImage
import getImageFromRes
import getSubImage
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

    var state = 0//0冰  1停止冰

    override fun addToHeroDoing() {
        heroDoing.apply {
            an49 = this@An49
            gudingShuaQiuTask("bingqiu",49,2500, customOverJudge = {
                state==1 || curGuan>49
            }, onGuanDealStart = {
                GlobalScope.launch {
                    shibieQiu()
                }

            })
        }
    }

    private suspend fun shibieQiu(){
        heroDoing.apply {
            val xuetiao = getImageFromRes("xiaochengxu/tezheng/xuetiao.png").toMat()
            var hasQiu = -1//0红，1蓝
            while (curGuan==49){
                delay(300)
                val img =getImage(App.rectWindow)
                val img2 = img.getSubImage(Config.rectCheckOfLeishen)
                if(hasQiu<0 && MatSearch.templateFit(xuetiao,img2.toMat())){
                    //有血条
                    log("有血条")
                    var count = Config.rectCheckOfLeishen.hasHSVColorCount(Config.colorLeishenHongqiuHSV1,Config.colorLeishenHongqiuHSV2, testImg = img2)
                    var count2 = Config.rectCheckOfLeishen.hasHSVColorCount(Config.colorLeishenLanqiuHSV, testImg = img2)
                    log("count:$count,count2:$count2")
                    if (count2 > 1500) {
                        log("是蓝:${count2}")
                        hasQiu = 1
                    } else if (count > 2000) {
                        log("是红:$count")
                        hasQiu = 0
                    } else {
                        log("不是红也不是蓝")
                        continue
                    }
                }else if(hasQiu<0){
                    log("没识别到血条")
                }
                var same = false
                if(hasQiu>=0){
                    if(hasRedShield(img.toMat())){
                        log("有红盾")
                        if(hasQiu==0){
                            same = true
                        }
                    }else if(hasQiu==1){
                        log("是蓝盾")
                        same = true
                    }

                    if(same){
                        log("同色")
                        XueLiang.observerXueDown { curGuan > 49 }
                        log("掉血了")
                        state = 1
                        break
                    }else{
                        //这里其实不用再识别了，识别掉两次血就可以了
                        log("不同色")
                        XueLiang.observerXueDown { curGuan > 49 }
                        log("掉血了")
                        delay(3000)
                        XueLiang.observerXueDown { curGuan > 49 }
                        state = 1
                        break
                    }
                }else{
                    log("没识别到球")
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
        return count > 1500 // 阈值可根据实测调整
    }
}