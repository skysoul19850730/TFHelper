package opencv

import getImageFromRes
import org.opencv.core.Core
import org.opencv.core.Mat
import org.opencv.imgproc.Imgproc

object MatSearch {
    fun init(){
        try {
            Class.forName("nu.pattern.OpenCV")
            nu.pattern.OpenCV.loadLocally()
        } catch (e: Exception) {
            System.loadLibrary(org.opencv.core.Core.NATIVE_LIBRARY_NAME)
        }
    }

    /**
     * 有时也想获取位置，比如识别球有没有到达某个位置
     */
    fun templateMatch(template: Mat, target: Mat): Core.MinMaxLocResult {
        val result = Mat()
        Imgproc.matchTemplate(target, template, result, Imgproc.TM_CCOEFF_NORMED)
        return Core.minMaxLoc(result)
    }

    fun templateFit(template: Mat, target: Mat,rate: Double = 0.75):Boolean{
        val result = templateMatch(template,target)
        println("fit rate ${result.maxVal}")
        return result.maxVal >= rate
    }




}