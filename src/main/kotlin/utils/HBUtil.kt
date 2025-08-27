package utils

import colorCompare
import data.Config
import data.MPoint
import data.MRect
import getImage
import getImageFromFile
import log
import logOnly
import utils.ImgUtil.forEach
import java.awt.Color
import java.awt.image.BufferedImage
import java.io.File
import kotlin.system.measureTimeMillis

object HBUtil {


    fun chuanzhang(img: BufferedImage): MPoint? {
        val allOutCirclePoints = arrayListOf<MPoint>()
        for (x in 0..400) {
            var firstPoing: MPoint? = null
            var lastPoint: MPoint? = null
            for (y in 100 until img.height) {
                var fit = try {
                    colorCompare(Color(img.getRGB(x, y)), Config.Color_ChuangZhang, 15)
                } catch (e: Exception) {
                    false
                }
                if (fit) {
                    if (firstPoing == null) {
                        firstPoing = MPoint(x, y)
                    }
                    lastPoint = MPoint(x, y)
                }
            }
            firstPoing?.let {
                allOutCirclePoints.add(it)
            }
            lastPoint?.let {
                allOutCirclePoints.add(it)
            }
        }
        if(allOutCirclePoints.size<150)return null//防止全屏就几十个点，根本不是圆圈的点，比如鲨鱼的颜色接近等等
        logOnly("chuanzhang allOutCirclePoints count:${allOutCirclePoints.size} ")
        val listP = arrayListOf<MPoint>()
        val map = hashMapOf<MPoint, Int>()

        repeat(500) {

            val points = allOutCirclePoints.shuffled().take(4).map {
                toPoint(it)
            }

            val chooseP = perpendicularBisectorIntersection(points)
            if (chooseP != null) {
                val mP = MPoint(chooseP.x.toInt(), chooseP.y.toInt())

                var pFromList = listP.firstOrNull { it.pointEqual(mP) }

                if (pFromList == null) {
                    pFromList = mP
                    listP.add(pFromList)
                }

                var count = map[pFromList] ?: 0

                count++


                map[pFromList] = count
            }
        }
        var p: MPoint? = null
        var ccc = 0
        map.forEach { t, u ->
            if (u > ccc) {
                p = t
                ccc = u
            }
        }
        log("max p count :${ccc}")
        return p
    }

    fun toPoint(p: MPoint): Point {
        return Point(p.x.toDouble(), p.y.toDouble())
    }

    data class Point(val x: Double, val y: Double)

    /**
     * 计算两条线段中垂线的交点
     * @param p1 线段1的第一个点
     * @param p2 线段1的第二个点
     * @param p3 线段2的第一个点
     * @param p4 线段2的第二个点
     * @return 中垂线的交点，如果平行则返回null
     */
    fun perpendicularBisectorIntersection(
        ps: List<Point>

    ): Point? {
        val p1 = ps.get(0)
        val p2 = ps.get(1)
        val p3 = ps.get(2)
        val p4 = ps.get(3)

        // 计算第一条线段的中点和中垂线
        val mid1 = Point((p1.x + p2.x) / 2, (p1.y + p2.y) / 2)
        val dx1 = p2.x - p1.x
        val dy1 = p2.y - p1.y

        // 计算第二条线段的中点和中垂线
        val mid2 = Point((p3.x + p4.x) / 2, (p3.y + p4.y) / 2)
        val dx2 = p4.x - p3.x
        val dy2 = p4.y - p3.y

        // 特殊情况处理（垂直线）
        if (dx1 == 0.0 && dx2 == 0.0) return null // 两中垂线都水平
        if (dy1 == 0.0 && dy2 == 0.0) return null // 两中垂线都垂直

        // 计算中垂线斜率
        val slope1 = if (dy1 == 0.0) null else -dx1 / dy1 // null表示垂直线
        val slope2 = if (dy2 == 0.0) null else -dx2 / dy2

        return when {
            slope1 == null && slope2 == null -> null // 都是垂直线
            slope1 == null -> Point(mid1.x, mid2.y + (slope2 ?: 0.0) * (mid1.x - mid2.x))
            slope2 == null -> Point(mid2.x, mid1.y + (slope1 ?: 0.0) * (mid2.x - mid1.x))
            slope1 == slope2 -> null // 平行线
            else -> {
                // 解方程组求交点
                val x = (mid2.y - mid1.y + (slope1 ?: 0.0) * mid1.x - (slope2 ?: 0.0) * mid2.x) /
                        ((slope1 ?: 0.0) - (slope2 ?: 0.0))
                val y = mid1.y + (slope1 ?: 0.0) * (x - mid1.x)
                Point(x, y)
            }
        }
    }


    fun test199Bai() {
        File(Config.caiji_main_path, "tmphb199").listFiles().forEach {

            var bai = is199Bai(getImageFromFile(it))
            log("图片${it.name}  ${if (bai) "是" else "不是"}199白")

        }
    }

    fun is199Bai(image: BufferedImage?): Boolean {
        var result = false
        measureTimeMillis {
            result = is199BaiDo(image)
        }.apply {
            log("is199Bai coast ${this}")
        }
        return result
    }

    //x  400-700  y 300, wh 60
    private fun is199BaiDo(image: BufferedImage?): Boolean {
        val img = image ?: getImage(App.rectWindow)
        for (x in 700 downTo 400) {
            if (isRectAllBai(img, MRect.createWH(x, 300, 60, 60))) {
                return true
            }
        }

        return false
    }

    private fun isRectAllBai(img: BufferedImage, rect: MRect): Boolean {
        var wCount = 0
        rect.forEach { x, y ->
            var color = img.getRGB(x, y).run {
                Color(this)
            }
            if (colorCompare(color, Color.WHITE, 10)) {
                wCount++
            }
        }
        val total = rect.width * rect.height
        if (wCount * 1f / total > 0.95) {
            return true
        }
        return false
    }

}