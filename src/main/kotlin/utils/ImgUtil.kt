package utils

import androidx.compose.runtime.mutableStateOf
import colorCompare
import data.MPoint
import data.MRect
import foreach
import getImage
import getImageFromRes
import java.awt.Color
import java.awt.image.BufferedImage
import java.awt.image.BufferedImage.TYPE_INT_RGB
import kotlin.math.abs

object ImgUtil {

    val _norRate = mutableStateOf(0.75)

    val sim = 20

    //        val simRate = 0.95 //太小了，比较管卡 都特么一样，操
    val simRate
        get() = if (App.caijing.value) 0.98  else _norRate.value

    fun isImageSim(img1: BufferedImage, img2: BufferedImage?, sim: Double = simRate,tag:String?=null): Boolean {
        img2 ?: return false
//        var startTime = System.currentTimeMillis()
        if (img1.width != img2.width || img1.height != img2.height) return false

        var rect = MRect.createWH(0, 0, img1.width, img1.height)

        var result = quickCompare(rect, sim,tag) { x, y ->
            img1.getRGB(x, y) to img2.getRGB(x, y)
        }
//        logOnly("isImageSim cost ${System.currentTimeMillis()-startTime}")
        return result
    }

    fun isHeroSim(img1: BufferedImage, img2: BufferedImage?, sim: Double = simRate): Boolean {
        img2 ?: return false
        if (img1.width != img2.width || img1.height != img2.height) return false

        var rect = MRect.createWH(0, 0, img1.width, img1.height)

        return quickCompare(rect, sim) { x, y ->
            val c1 = img1.getRGB(x, y)
            val c2 = img2.getRGB(x, y)
            if (c1 == Color.WHITE.rgb || c2 == Color.WHITE.rgb) {//白色就认为相同（刷新时的白色闪光
//                log("has shanguang baise ")
                Color.WHITE.rgb to Color.WHITE.rgb
            } else {
                c1 to c2
            }
        }
    }

    private fun quickCompare(rect: MRect, sim: Double = simRate,tag:String?=null, c1c2: (Int, Int) -> Pair<Int, Int>): Boolean {
        var yes = 0
        var no = 0
        val all = rect.width * rect.height
        val yesall = all * sim
        val noall = all * (1 - sim)
//        val noall = all
        //计算不合格已经超标就不用再继续循环后面的了

        rect.forEach { x, y ->
            var result = c1c2.invoke(x, y)
            if (colorCompare(result.first, result.second)) {
                yes++
                if (yes > yesall) {
                    var rate = (yes * 1f / all)
//                    println("pre over suc rate is $rate")
                    return true
                }
            } else {
                no++
                if (no > noall) {
                    var rate = (yes * 1f / all)
//                    println("pre over fail rate is $rate")
                    return false
                }
            }
        }
        var rate = (yes * 1f / (rect.width * rect.height))
        if(rate>0.75&&tag!=null) {
//            println("$tag rate is $rate")
        }
        return rate > sim
    }

    fun isImageInRect(imgName: String, rect: MRect, sim: Double = simRate): Boolean {
        val img = getImageFromRes(imgName)

        return isImageInRect(img, rect, sim)
    }

    fun isImageInRect(img: BufferedImage, rect: MRect, sim: Double = simRate): Boolean {
        val img2 = getImage(rect)
//        log(img)
//        log((img2))
        return isImageSim(img, img2, sim)
    }


    inline fun MRect.forEach(callback: (Int, Int) -> Unit) {
        for (x in left..right) {
            for (y in top..bottom) {
                callback.invoke(x, y)
            }
        }
    }
    inline fun MRect.forEach4Result(callback: (Int, Int) -> Boolean) {
        for (x in left..right) {
            for (y in top..bottom) {
                if(callback.invoke(x, y)){
                    return
                }
            }
        }
    }

    private fun colorCompare(color1: Int, color2: Int): Boolean {
        if (sim == 0) {
            return color1 == color2
        }
        return colorCompare(Color(color1), Color(color2))
    }


    private fun colorCompare(c1: Color, c2: Color): Boolean {
        return (abs(c1.red - c2.red) <= sim
                && abs(c1.green - c2.green) <= sim
                && abs(c1.blue - c2.blue) <= sim)
    }


    fun BufferedImage.copyWithColor(color:Color,minColorCount:Int):BufferedImage?{

        val newImg = BufferedImage(width, height, TYPE_INT_RGB)
        var count = 0
        foreach { x, y ->
            val tC = getRGB(x,y)
            if(colorCompare(Color(tC),color,30)){
                count++
                newImg.setRGB(x,y,tC)
            }
            false
        }
        if(count<minColorCount){
            return null
        }
        return newImg

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