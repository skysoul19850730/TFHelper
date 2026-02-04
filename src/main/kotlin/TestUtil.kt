import data.Config
import data.MPoint
import data.MRect
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import opencv.MatSearch
import opencv.toMat
import org.apache.commons.compress.harmony.pack200.PackingUtils
import org.apache.commons.compress.harmony.pack200.PackingUtils.log
import org.opencv.core.*
import org.opencv.imgproc.Imgproc
import tasks.XueLiang
//import tasks.anyue.base.Ay99Test
import test.Utils
import utils.AYUtil
import utils.ImgUtil
import utils.ImgUtil.forEach
import java.awt.Color
import java.awt.image.BufferedImage
import java.io.File

object TestUtil {
    suspend fun test() {
//        MRobot.moveFullScreen()

//        CaijiUtil.saveRectByFolder(App.caijiPath+"\\pukepai",Config.AY_Puke_rect)
//        WX59().caiji()
//        Ay99Test().shibiePai()

        val img = Utils.getWindowFolderImg("y001.png")

        val diffIcon = AYUtil.getDiffIcon(img)
        log("diffIcon is $diffIcon")

//        val img2 = Utils.getWindowFolderImg("y001.png").getSubImage(rect2)
//        val transparentImage = BufferedImage(
//            img.width,
//            img.height,
//            BufferedImage.TYPE_INT_ARGB  // 支持alpha通道
//        )
//        img.foreach { i, i2 ->
//            val rgb = Color(img.getRGB(i,i2))
//            val rgb2 = Color(img2.getRGB(i,i2))
//            if(rgb.red<200 && rgb.green<200 && rgb.blue<200){
//
//                if(rgb2.red<200 && rgb2.green<200 && rgb2.blue<200){
//
//                }else{
//                    transparentImage.setRGB(i,i2,Color.BLUE.rgb)
//                }
//
//            }else{
//                if(rgb2.red<200 && rgb2.green<200 && rgb2.blue<200){
//                    transparentImage.setRGB(i,i2,Color.RED.rgb)
//                }else{
//
//                }
//
//            }
//            false
//        }


//        val img = Utils.getWindowFolderImg("y001.png").getSubImage(rect1)
//        val transparentImage = BufferedImage(
//            img.width,
//            img.height,
//            BufferedImage.TYPE_INT_ARGB  // 支持alpha通道
//        )
//        img.foreach { i, i2 ->
//            val rgb = Color(img.getRGB(i,i2))
//            if(rgb.red<200 && rgb.green<200 && rgb.blue<200){
//
//            }else{
//                transparentImage.setRGB(i,i2,Color.RED.rgb)
//            }
//            false
//        }
//        transparentImage.saveTo(File(App.caijiPath,"t222222.png"))
//        val img = Utils.getWindowFolderImg("y001.png")
//        testTZ(img)
    }

}

fun main() {

    try {
        Class.forName("nu.pattern.OpenCV")
        nu.pattern.OpenCV.loadLocally()
    } catch (e: Exception) {
        System.loadLibrary(org.opencv.core.Core.NATIVE_LIBRARY_NAME)
    }
    var caijiImg :BufferedImage?=null
    GlobalScope.launch{
        val imgs = File("C:\\Users\\Administrator\\Desktop\\debug2").listFiles().map {
            getImageFromFile(it)
        }.forEach {
            delay(100)
            caijiImg = it
        }

    }
    //蓝2000
    //红3000
    GlobalScope.launch {
        val xuetiao = getImageFromRes("xiaochengxu\\tezheng\\xuetiao.png").toMat()
        var hasQiu = -1//0红，1蓝
        while (true) {
            delay(50)
            val img =caijiImg?:continue
             val img2 =   img.getSubImage(Config.rectCheckOfLeishen)
            if (hasQiu < 0 && MatSearch.templateFit(xuetiao, img2.toMat())) {
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
            }
            var same = false
            if (hasQiu >= 0) {
                if (hasRedShield(img.toMat())) {
                    log("有红盾")
                    if (hasQiu == 0) {
                        same = true
                    }
                } else if (hasQiu == 1) {
                    log("是蓝盾")
                    same = true
                }

                if (same) {
                    log("同色")
                    log("掉血了")
                    break
                }else{
                    //这里其实不用再识别了，识别掉两次血就可以了
                    log("不同色")

                    var recordXue = XueLiang.getXueLiang(caijiImg)
                    var curXue = recordXue

                    while (recordXue - curXue < 0.1 ) {
                        delay(10)
                        curXue = XueLiang.getXueLiang(caijiImg)
                        if (curXue > recordXue) {//有可能处于回血状态，回血的话就把记录的血提升到当前血，再继续监听掉血
                            recordXue = curXue
                        }
                    }

                    log("掉血了")
                    delay(3000)
                    recordXue = XueLiang.getXueLiang(caijiImg)
                    curXue = recordXue

                    while (recordXue - curXue < 0.1 ) {
                        delay(10)
                        curXue = XueLiang.getXueLiang(caijiImg)
                        if (curXue > recordXue) {//有可能处于回血状态，回血的话就把记录的血提升到当前血，再继续监听掉血
                            recordXue = curXue
                        }
                    }
                    log("第二次掉血了，被同色球打掉血了，停止了")
                    break
                }
            }

        }
    }
    while(true){
    }
}

fun hasRedShield(img: Mat): Boolean {

    var bossRect = Rect(770, 150, 200, 75)

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