import data.MPoint
import data.MRect
import model.CarDoing
import opencv.*
import org.opencv.core.Core
import org.opencv.core.Mat
import org.opencv.core.Scalar
import org.opencv.imgcodecs.Imgcodecs
import org.opencv.imgproc.Imgproc
import java.awt.Color
import java.awt.image.BufferedImage
import java.io.File

object WX79Test {

    fun test() {
        //耗时：82
        //耗时：44 1.0
        //slidingPixelMatch 还是快一些

        //修正
        // 耗时：23
        //耗时：42 1.0
        //上面是因为start记录在前面，第一个打印包含了读两个文件的时间。。。。。，还是opencv快啊

//        val start = System.currentTimeMillis()
        val img = getImageFromFile(File("C:\\Users\\sqc\\Desktop\\debug3\\ttt.png"))
        val bigImage = getImageFromFile(File("C:\\Users\\sqc\\Desktop\\debug3\\a79t.png"))
        val start = System.currentTimeMillis()
        val fit = bigImage.hasImage(img,true)

        val s2 = System.currentTimeMillis()
        println("耗时：${s2 - start}")

        val fit2 = slidingPixelMatch(img,bigImage)
        val s3 = System.currentTimeMillis()
        println("耗时：${s3 - s2} ${fit2.first}")

//        val mat = Imgcodecs.imread("C:\\Users\\sqc\\Desktop\\debug3\\ttt.png",Imgcodecs.IMREAD_UNCHANGED)
//        val mat2 = Imgcodecs.imread("C:\\Users\\sqc\\Desktop\\debug3\\a79t.png",Imgcodecs.IMREAD_UNCHANGED)
//
//        // 1. 分离 ARGB -> BGR + Alpha
//        val channels = mutableListOf<Mat>()
//        Core.split(mat, channels)
//        val b = channels[0]
//        val g = channels[1]
//        val r = channels[2]
//        val a = channels[3]
//        // 2. 构建 mask：alpha > 0 的区域为 255，否则 0
//        val mask = Mat()
//        Core.compare(a, Scalar(1.0), mask, Core.CMP_GT) // alpha > 0 → 255
//        // 3. 构建 templateBGR：仅保留 alpha > 0 的像素，其余置黑
////    val templateBgr = Mat()
////    Core.merge(listOf(b, g, r), templateBgr)
////    val maskNot = Mat()
////    Core.bitwise_not(mask, maskNot)
////    templateBgr.setTo(Scalar.all(0.0), maskNot)
//
//        // 4. 执行带 mask 的模板匹配
//        val result = Mat()
////        Imgproc.matchTemplate(
////            mat2,
////            mat,
////            result,
////            Imgproc.TM_CCORR_NORMED, // 支持 mask
////            mask
////        )
//        Imgproc.matchTemplate(mat2, mat, result, Imgproc.TM_CCOEFF_NORMED)
//        val rrr = Core.minMaxLoc(result)

        val aaaaa = 0

    }

    /**
     * 在 target 图像中滑动 template（ARGB PNG），仅对 template 中 Alpha > 0 的像素进行 RGB 硬匹配，
     * 返回最大匹配率（[0.0, 1.0]）及最佳位置。
     *
     * @param template ARGB BufferedImage（含 Alpha）
     * @param target   更大的 BufferedImage（如截图）
     * @param tolerance 颜色容差（用于 colorCompare），设为 0 则严格相等
     * @return Pair<匹配率, 最佳位置 MPoint?>，若无有效模板像素则返回 (0.0, null)
     */
    fun slidingPixelMatch(
        template: BufferedImage,
        target: BufferedImage,
        tolerance: Int = 15
    ): Pair<Double, MPoint?> {
        val tw = template.width
        val th = template.height
        val dw = target.width
        val dh = target.height

        if (tw > dw || th > dh) return 0.0 to null

        // ✅ 预计算：仅非透明像素列表（x, y, Color）
        val validPixels = mutableListOf<Triple<Int, Int, Color>>()
        for (y in 0 until th) {
            for (x in 0 until tw) {
                val argb = template.getRGB(x, y)
                val a = (argb shr 24) and 0xFF
                if (a > 0) {
                    validPixels.add(Triple(x, y, Color(argb)))
                }
            }
        }

        val validCount = validPixels.size
        if (validCount == 0) return 0.0 to null

        var bestRate = 0.0
        var bestPos: MPoint? = null

        // ✅ 滑动窗口：仅遍历有效像素（不是整个模板网格！）
        for (ty in 0 until dh - th + 1) {
            for (tx in 0 until dw - tw + 1) {
                var matchCount = 0
                // 🔥 关键优化：直接遍历 validPixels（200 次），而非 55×55=3025 次
                for ((tx0, ty0, tmplColor) in validPixels) {
                    val px = tx + tx0
                    val py = ty + ty0
                    val targetColor = Color(target.getRGB(px, py))
                    if (colorCompare(tmplColor, targetColor, tolerance)) {
                        matchCount++
                    }
                }
                val rate = matchCount.toDouble() / validCount
                if (rate > bestRate) {
                    bestRate = rate
                    bestPos = MPoint(tx, ty)
                }
            }
        }

        return bestRate to bestPos
    }


}