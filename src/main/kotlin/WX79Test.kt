import data.MPoint
import data.MRect
import model.CarDoing
import opencv.MatSearch
import opencv.saveToImg
import opencv.subMat
import opencv.toMat
import org.opencv.imgcodecs.Imgcodecs
import java.awt.Color
import java.awt.image.BufferedImage
import java.io.File

object WX79Test {

    fun test() {

        val img = getImageFromFile(File("C:\\Users\\Administrator\\Desktop\\debug3\\a79.png"))

        val platImg = getImageFromFile(File("C:\\Users\\Administrator\\Desktop\\debug3\\ttt.png"))



//       val platImg = Imgcodecs.imread("C:\\Users\\Administrator\\Desktop\\debug3\\ttt.png",Imgcodecs.IMREAD_UNCHANGED)

       val car = CarDoing(0).apply {
            initPositions()
        }

        car.carps.forEach {
            val tti = img.getSubImage(it.mRect.scale(0.3f))
            val pair = slidingPixelMatch(platImg,tti)
            println("${pair.first}")
            val has =(pair.first>0.5)
            println("has:$has")
        }

//        val has = MatSearch.templateFit(platImg,img,0.3)
//        println("has:$has")
//75 340 55

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