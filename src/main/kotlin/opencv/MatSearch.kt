package opencv

import getImageFromRes
import org.opencv.core.Core
import org.opencv.core.Mat
import org.opencv.core.Scalar
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


    fun templateFitWithMask(template: Mat, target: Mat,rate: Double = 0.75):Boolean{
        val result = templateMatchWithMask(template,target)
        println("fit rate ${result?.maxVal?:0}")
        return (result?.maxVal?:0.0) >= rate
    }

    fun templateMatchWithMask(templateArgb: Mat, target: Mat, threshold: Double = 0.75): Core.MinMaxLocResult? {
        // 1. 分离 ARGB -> BGR + Alpha
        val bgr = Mat()
        val alpha = Mat()
        // 假设 templateArgb 是 CV_8UC4 (ARGB)
        if (templateArgb.channels() != 4) throw IllegalArgumentException("Template must be 4-channel ARGB")

        // 拆分通道：OpenCV 默认是 BGRA（注意顺序！）
        val channels = mutableListOf(Mat(), Mat(), Mat(), Mat())
        Core.split(templateArgb, channels)
        // channels[0]=B, [1]=G, [2]=R, [3]=A
        val b = channels[0]
        val g = channels[1]
        val r = channels[2]
        val a = channels[3]

        // 2. 构建 mask：alpha > 0 的区域为 255，否则 0
        val mask = Mat()
        Core.compare(a, Scalar(1.0), mask, Core.CMP_GT) // alpha > 0 → 255

        // 3. 构建 templateBGR：仅保留 alpha > 0 的像素，其余置黑
        val templateBgr = Mat()
        Core.merge(listOf(b, g, r), templateBgr)
        // 将非 mask 区域置为 0（黑）
        var maskNot = Mat()
        Core.bitwise_not(mask, maskNot)
        templateBgr.setTo(Scalar.all(0.0), maskNot)

        // 4. 执行带 mask 的模板匹配（必须用 TM_CCORR_NORMED 或 TM_SQDIFF）
        val result = Mat()
        Imgproc.matchTemplate(
            target,
            templateBgr,
            result,
            Imgproc.TM_CCORR_NORMED, // ✅ 支持 mask
            mask
        )

        val minMax = Core.minMaxLoc(result)
        println("Match score: ${minMax.maxVal}")

        return if (minMax.maxVal >= threshold) minMax else null
    }





}