import data.MPoint
import data.MRect
import data.toHSBFirst
import model.CarDoing
import opencv.MatSearch
import opencv.saveToImg
import opencv.subMat
import opencv.toMat
import org.opencv.imgcodecs.Imgcodecs
import tasks.anyue.base.ay139.AY139Util
import utils.ImgUtil.slidingPixelMatch
import java.awt.Color
import java.awt.image.BufferedImage
import java.io.File
import kotlin.math.abs
import kotlin.math.min

object Ay139Test {

    fun test() {


        val files = File("E:\\ideaspace\\TFHelperHome\\tfres\\logs\\anyue\\26_09_13\\03_57_01").listFiles()

        files?.forEach {
            println("处理文件：${it.name}")

            val tops = AY139Util.getTopMatTypes(getImageFromFile(it))
            var diff: AY139Util.MatType? = null
            if (tops != null) {
                println("识别结果：${tops.joinToString(";") { it.toPString() }}")

                diff = AY139Util.theDifferentMat(tops)
                println("识别结果Diff：${diff.toPString()}")
            }
//
            val bottom = AY139Util.getBottomRunningMat(getImageFromFile(it))
            if(bottom!=null){
                println("识别结果Bottom：${bottom.toPString()}")
            }

            println("\n\n")
        }


    }


    fun testOne(img1:BufferedImage? = null){
        val img = getImageFromFile(File("E:\\ideaspace\\TFHelperHome\\tfres\\window\\01_23_53_472.png"))
        val tops = AY139Util.getTopMatTypes(img)
        var diff: AY139Util.MatType? = null
        if (tops != null) {
            println("识别结果：${tops.joinToString(";") { it.toPString() }}")

            diff = AY139Util.theDifferentMat(tops)
            println("识别结果Diff：${diff.toPString()}")
        }
//
        val bottom = AY139Util.getBottomRunningMat(img)
        if(bottom!=null){
            println("识别结果Bottom：${bottom.toPString()}")
        }
    }

}

fun main() {
    try {
        Class.forName("nu.pattern.OpenCV")
        nu.pattern.OpenCV.loadLocally()
    } catch (e: Exception) {
        System.loadLibrary(org.opencv.core.Core.NATIVE_LIBRARY_NAME)
    }
   Ay139Test. testOne()
}