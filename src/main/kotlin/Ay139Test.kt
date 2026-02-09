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


        val files = File("C:\\Users\\Administrator\\Desktop\\ay139test").listFiles()

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

}