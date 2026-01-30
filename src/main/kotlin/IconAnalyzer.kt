import data.MRect
import opencv.binary
import opencv.saveToImg
import opencv.toGray
import opencv.toMat
import org.opencv.core.*
import org.opencv.imgproc.Imgproc
import org.opencv.imgcodecs.Imgcodecs
import tasks.anyue.base.ay139.AY139Util
import java.io.File
import java.util.*
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

/**
 * 精准图标分析器：基于您确认的4类图标（实心斧头圆形 / 实心法杖方形 / 空心法杖方形 / 空心法杖圆形）
 * 输入：图片路径（main args）
 * 输出：每个图标的 Rect + 外框形状 + 内容类别
 */
class IconAnalyzer {
    companion object {
        // ✅ 四个图标绝对坐标（1000×607 原图，经您验证）
        val TOP_ICONS = listOf(
            Rect(375+4, 206+4, 77-8, 77-8),  // 0: 实心斧头，圆形
            Rect(473+4, 168+4, 77-8, 77-8),  // 1: 实心法杖，方形
            Rect(571+4, 169+4, 77-8, 77-8),  // 2: 空心法杖，方形
            Rect(669+4, 208+4, 77-8, 77-8)   // 3: 空心法杖，圆形
        )
    }


}

data class IconResult(
    val name: String,
    val rect: Rect,
    val shape: String,
    val category: String,
    val fillRatio: Double,
    val roi: Mat
)

fun saveMat(mat: Mat) {
    try {

        Imgcodecs.imwrite("C:\\Users\\Administrator\\Desktop\\debug\\${System.currentTimeMillis()}.png", mat)
    } catch (e: Exception) {
// 绘制所有轮廓到新图像并保存（便于查看）
        val debugImg = Mat.zeros(mat.size(), CvType.CV_8UC3)
        if (mat is MatOfPoint) {
            Imgproc.drawContours(debugImg, listOf(mat), -1, Scalar(0.0, 255.0, 0.0), 2) // 绿色轮廓
        }
        Imgcodecs.imwrite(
            "C:\\Users\\Administrator\\Desktop\\debug\\contours${System.currentTimeMillis()}.png",
            debugImg
        )
        debugImg.release()

    }
}

/**
 * 判断图标外圈形状：圆形 or 方形
 * @param iconMat 输入的图标图像（BGR 或 Gray）
 * @return "圆形" 或 "方形"
 */
fun detectOuterShape(iconMat: Mat): String {
    // 1. 转灰度
    val gray = Mat()
    Imgproc.cvtColor(iconMat, gray, Imgproc.COLOR_BGR2GRAY)

    // 2. 二值化（Otsu）
    val binary = Mat()
    Imgproc.threshold(gray, binary, 0.0, 255.0, Imgproc.THRESH_BINARY + Imgproc.THRESH_OTSU)

    // 3. 提取外轮廓（只取最大轮廓，假设外圈为最外层）
    val contours = ArrayList<MatOfPoint>()
    Imgproc.findContours(binary, contours, Mat(), Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE)

    gray.release()
    binary.release()

    if (contours.isEmpty()) return "未知"

    // 取最大轮廓（外圈）
    val largestContour = contours.maxByOrNull { Imgproc.contourArea(it) } ?: return "未知"
    val contourArea = Imgproc.contourArea(largestContour)
    val boundingRect = Imgproc.boundingRect(largestContour)
    val rectArea = boundingRect.width * boundingRect.height

    // 4. 形状判别：面积比 + 长宽比
    val areaRatio = contourArea.toDouble() / rectArea  // 圆 ≈ π/4 ≈ 0.785；方 ≈ 1.0
    val aspectRatio = boundingRect.width.toDouble() / boundingRect.height  // 圆 ≈ 1.0；方可偏离但通常接近1

    // ✅ 启发式阈值（经实测调优）
    return when {
        areaRatio < 0.82 && abs(aspectRatio - 1.0) < 0.15 -> "圆形"
        areaRatio >= 0.82 && abs(aspectRatio - 1.0) < 0.2 -> "方形"
        else -> "未知"
    }
}

fun templateMatch(template: Mat, target: Mat): Double {
    val result = Mat()
    Imgproc.matchTemplate(target, template, result, Imgproc.TM_CCOEFF_NORMED)
    return Core.minMaxLoc(result).maxVal.toDouble()
}

fun saveBorder(){

}

fun main() {
//586,250,127,184
    try {
        Class.forName("nu.pattern.OpenCV")
        nu.pattern.OpenCV.loadLocally()
    } catch (e: Exception) {
        System.loadLibrary(org.opencv.core.Core.NATIVE_LIBRARY_NAME)
    }
    val startTime = System.currentTimeMillis()

    val img = getImageFromFile(File("C:\\Users\\Administrator\\Desktop\\debug\\r2.png"))
//
//    IconAnalyzer.TOP_ICONS.forEach {
//        img.saveSubTo(MRect.createWH(it.x,it.y,it.width,it.height),File("C:\\Users\\Administrator\\Desktop\\debug\\${it.x}_${it.y}.png"))
//    }
//    return


    val tops = AY139Util.getTopMatTypes(img)

    if(tops!=null) {
        tops.forEach {
            println(it.toPString())
        }
        val diff = AY139Util.theDifferentMat(tops)
        println("diff is :"+diff.toPString())

        val img = getImageFromFile(File("C:\\Users\\Administrator\\Desktop\\debug\\r2.png"))

        val bottom = AY139Util.getBottomRunningMat(tops,img)
        println("bottom is ${bottom?.toPString()}")
        println(if(bottom == diff) "是不同的那个继续冰" else "不是目标，停冰攻击")
    }
    println( " 时间：${System.currentTimeMillis() - startTime}")
    return
    val src = "C:\\Users\\Administrator\\Desktop\\debug\\m1.png".toMat()
    val hsv = Mat()
    Imgproc.cvtColor(src, hsv, Imgproc.COLOR_BGR2HSV)

    // 紫色/品红范围（更宽松）：H 110~170（覆盖紫、蓝紫、粉紫），S>60, V>50
    val lower = Scalar(110.0, 60.0, 50.0)
    val upper = Scalar(170.0, 255.0, 255.0)
    val mask = Mat()
    Core.inRange(hsv, lower, upper, mask)

//    val filled = Mat()
//    Imgproc.morphologyEx(mask, filled, Imgproc.MORPH_CLOSE, Mat.ones(5, 5, CvType.CV_8U))


    val contours = ArrayList<MatOfPoint>()
    Imgproc.findContours(mask, contours, Mat(), Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE)

// 取面积最大的轮廓（斧头主体）
//    val largestContour = contours.maxByOrNull { Imgproc.contourArea(it) } ?: return

//    contours.forEachIndexed { index, largestContour ->
//        val roiMask = Mat.zeros(mask.size(), CvType.CV_8UC1)
//        Imgproc.drawContours(roiMask, listOf(largestContour), -1, Scalar(255.0), -1) // 实心填
//        roiMask.saveToImg("C:\\Users\\Administrator\\Desktop\\debug\\roiMask${index}.png")
//    }

    val roiMask = Mat.zeros(mask.size(), CvType.CV_8UC1)
    Imgproc.drawContours(roiMask, contours.subList(15,17), -1, Scalar(255.0), 2) // 实心填充


    saveCircularRegionFromMask(src,roiMask, Point(76.0/2+1.5,76.0/2),35.0,"axe_circle.png")
return
    // 补充：用V通道二值化增强高亮区域（发光部分）
    val vChannel = Mat()
    Core.extractChannel(hsv, vChannel, 2) // V通道
    val vMask = Mat()
    Imgproc.threshold(vChannel, vMask, 150.0, 255.0, Imgproc.THRESH_BINARY)
    Core.bitwise_and(mask, vMask, mask)
    // 形态学清理
    val kernel = Mat.ones(3, 3, CvType.CV_8U)
    Imgproc.morphologyEx(mask, mask, Imgproc.MORPH_CLOSE, kernel)
    Imgproc.morphologyEx(mask, mask, Imgproc.MORPH_OPEN, kernel)

//    getImageFromFile(File("C:\\Users\\Administrator\\Desktop\\pukepai\\pk_w.png"))
//        .saveSubTo(MRect.createWH(586, 250, 127, 184),
//            File("C:\\Users\\Administrator\\Desktop\\debug\\pk_w.png"))



    val target = "C:\\Users\\Administrator\\Desktop\\debug\\r6.png".toMat().toGray().binary()

    val targets = IconAnalyzer.TOP_ICONS.map {
        target.submat(it).clone()
    }
//600,330,70,114
    // 高斯模糊去噪（保留光晕低频）
//    targets.forEachIndexed { index, tg ->
//        listOf(374, 473, 572, 669).forEach {
            var model = Imgcodecs.imread("C:\\Users\\Administrator\\Desktop\\debug\\pk_w.png").toGray().binary()
            var modelScale = model
            Imgproc.resize(model, modelScale, Size(92.33, 133.78), 0.5, 0.5, Imgproc.INTER_AREA)
    (0..50 step 2).map { it.toDouble() }.forEach {

        // 1. 旋转模板
        val modelScale = Mat()
        val center = Point(model.cols() / 2.0, model.rows() / 2.0)
        val rotMat = Imgproc.getRotationMatrix2D(center, it, 1.0)
        Imgproc.warpAffine(model, modelScale, rotMat, model.size(), Imgproc.INTER_LINEAR)
        val doubl = templateMatch(modelScale, target)
        println("模板角度：${it} " + doubl + " 时间：${System.currentTimeMillis() - startTime}")

    }


//            val doubl = templateMatch(modelScale, tg)
//            println("target位置:${index} 模板：${it} " + doubl + " 时间：${System.currentTimeMillis() - startTime}")
//        }


//    }


}


fun saveCircularRegionFromMask(
    src: Mat,      // 原图（BGR）
    mask: Mat,     // 二值掩码（CV_8UC1，白色=255=有效区域）
    center: Point,
    radius: Double,
    outputPath: String
) {
    val w = src.cols()
    val h = src.rows()

    // 创建 BGRA 输出图（4通道：B,G,R,A）
    val bgra = Mat.zeros(Size(w.toDouble(), h.toDouble()), CvType.CV_8UC4)

    val cx = center.x.toInt()
    val cy = center.y.toInt()
    val r2 = (radius * radius).toInt()  // 平方避免浮点误差

    // 遍历圆内所有像素点
    for (y in max(0, cy - radius.toInt()) until min(h, cy + radius.toInt() + 1)) {
        for (x in max(0, cx - radius.toInt()) until min(w, cx + radius.toInt() + 1)) {
            val dx = x - cx
            val dy = y - cy
            if (dx * dx + dy * dy <= r2) {  // 在圆内
                // 1. 检查 mask 中该点是否为白色（非黑）
                val maskVal = mask.get(y, x)[0]
                if (maskVal > 0) {  // mask 中为白色 → 有效区域
                    // 2. 从原图 src 提取 BGR
//                    val pixel = DoubleArray(3)
//                    src.get(y, x, pixel)
                    val scalar = src.get(y, x)  // 返回 Scalar，自动适配通道数
                    val b = scalar[0]
                    val g = scalar[1]
                    val r = scalar[2]
                    bgra.put(y, x, b, g, r, 255.0)
                    // 3. 写入 BGRA：B,G,R + alpha=255（不透明）
//                    bgra.put(y, x, scalar[0], scalar[1], scalar[2], 255.0)
                }
                // 否则：保持默认 alpha=0（透明）
            }
        }
    }

    // 保存为 PNG（自动保留透明通道）
    Imgcodecs.imwrite(outputPath, bgra)
    bgra.release()
}


