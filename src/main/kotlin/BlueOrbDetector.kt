import org.opencv.core.*
import org.opencv.imgcodecs.Imgcodecs
import org.opencv.imgproc.Imgproc
import java.io.File

object BlueOrbDetector {

    /**
     * 基于颜色和形状的蓝球检测（推荐方法）
     * 比模板匹配更鲁棒，不受尺寸、旋转影响
     */
    fun detectBlueOrbByColor(target: Mat, debugMode: Boolean = false): Boolean {
        val hsv = Mat()
        Imgproc.cvtColor(target, hsv, Imgproc.COLOR_BGR2HSV)

        // 蓝色范围（HSV 色彩空间）
        // H: 色调 (蓝色约在 100-140)
        // S: 饱和度 (高饱和度 > 100)
        // V: 亮度 (高亮度 > 150)
        val lowerBlue = Scalar(100.0, 100.0, 150.0)
        val upperBlue = Scalar(140.0, 255.0, 255.0)

        val mask = Mat()
        Core.inRange(hsv, lowerBlue, upperBlue, mask)

        // 形态学操作去除噪点
        val kernel = Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, Size(5.0, 5.0))
        Imgproc.morphologyEx(mask, mask, Imgproc.MORPH_OPEN, kernel)
        Imgproc.morphologyEx(mask, mask, Imgproc.MORPH_CLOSE, kernel)

        // 计算蓝色像素占比
        val bluePixels = Core.countNonZero(mask)
        val totalPixels = mask.rows() * mask.cols()
        val ratio = bluePixels.toDouble() / totalPixels

        if (debugMode) {
            println("蓝色像素数: $bluePixels / $totalPixels")
            println("蓝色像素占比: ${String.format("%.4f", ratio)}")
            // 保存调试图片
            Imgcodecs.imwrite("debug_mask.png", mask)
        }

        hsv.release()
        mask.release()
        kernel.release()

        // 阈值：如果蓝色像素占比超过 5%，认为存在蓝球
        return ratio > 0.05
    }

    /**
     * 基于轮廓的蓝球检测（更精确）
     * 不仅检测颜色，还检查是否为圆形
     */
    fun detectBlueOrbByContour(target: Mat, debugMode: Boolean = false): Boolean {
        val hsv = Mat()
        Imgproc.cvtColor(target, hsv, Imgproc.COLOR_BGR2HSV)

        // 蓝色范围
        val lowerBlue = Scalar(100.0, 100.0, 150.0)
        val upperBlue = Scalar(140.0, 255.0, 255.0)

        val mask = Mat()
        Core.inRange(hsv, lowerBlue, upperBlue, mask)

        // 形态学操作
        val kernel = Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, Size(5.0, 5.0))
        Imgproc.morphologyEx(mask, mask, Imgproc.MORPH_OPEN, kernel)
        Imgproc.morphologyEx(mask, mask, Imgproc.MORPH_CLOSE, kernel)

        // 查找轮廓
        val contours = mutableListOf<MatOfPoint>()
        val hierarchy = Mat()
        Imgproc.findContours(mask, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE)

        var foundOrb = false

        if (debugMode) {
            println("找到 ${contours.size} 个轮廓")
        }

        for (contour in contours) {
            val area = Imgproc.contourArea(contour)

            // 面积阈值：至少 300 像素（根据实际调整）
            if (area < 300) continue

            // 计算圆形度
            val perimeter = Imgproc.arcLength(MatOfPoint2f(*contour.toArray()), true)
            val circularity = 4 * Math.PI * area / (perimeter * perimeter)

            if (debugMode) {
                println("轮廓面积: ${area.toInt()}, 圆形度: ${String.format("%.3f", circularity)}")
            }

            // 圆形度阈值：> 0.5 认为是圆形（完美圆形=1.0）
            if (circularity > 0.5) {
                foundOrb = true
                if (debugMode) {
                    println("✓ 检测到蓝色圆形区域！")
                }
                break
            }
        }

        // 保存调试图片
        if (debugMode && contours.isNotEmpty()) {
            val debugImg = target.clone()
            Imgproc.drawContours(debugImg, contours, -1, Scalar(0.0, 255.0, 0.0), 2)
            Imgcodecs.imwrite("debug_contours.png", debugImg)
            debugImg.release()
        }

        hsv.release()
        mask.release()
        kernel.release()
        hierarchy.release()
        contours.forEach { it.release() }

        return foundOrb
    }

    /**
     * 多尺度模板匹配（改进版）
     */
    fun detectBlueOrbByTemplateMultiScale(
        template: Mat,
        target: Mat,
        threshold: Double = 0.70,
        debugMode: Boolean = false
    ): Pair<Boolean, Double> {
        val scales = listOf(0.5, 0.6, 0.7, 0.8, 0.9, 1.0, 1.1, 1.2, 1.3, 1.5)
        var maxScore = 0.0
        var bestScale = 1.0

        for (scale in scales) {
            val width = (template.cols() * scale).toInt()
            val height = (template.rows() * scale).toInt()

            if (width > target.cols() || height > target.rows()) continue
            if (width < 10 || height < 10) continue

            val resized = Mat()
            Imgproc.resize(template, resized, Size(width.toDouble(), height.toDouble()))

            val result = Mat()
            Imgproc.matchTemplate(target, resized, result, Imgproc.TM_CCOEFF_NORMED)
            val mmr = Core.minMaxLoc(result)

            if (debugMode) {
                println("缩放 ${String.format("%.2f", scale)}x: 匹配度 = ${String.format("%.4f", mmr.maxVal)}")
            }

            if (mmr.maxVal > maxScore) {
                maxScore = mmr.maxVal
                bestScale = scale
            }

            resized.release()
            result.release()
        }

        if (debugMode) {
            println("最佳缩放比例: ${String.format("%.2f", bestScale)}x, 匹配度: ${String.format("%.4f", maxScore)}")
        }

        return Pair(maxScore >= threshold, maxScore)
    }

    /**
     * 综合检测方法（推荐使用）
     * 结合颜色和形状检测，准确率最高
     */
    fun detectBlueOrb(target: Mat, method: DetectionMethod = DetectionMethod.COLOR_AND_CONTOUR): Boolean {
        return when (method) {
            DetectionMethod.COLOR_ONLY -> detectBlueOrbByColor(target, false)
            DetectionMethod.COLOR_AND_CONTOUR -> detectBlueOrbByContour(target, false)
        }
    }

    enum class DetectionMethod {
        COLOR_ONLY,           // 仅颜色检测（快速但可能误判）
        COLOR_AND_CONTOUR     // 颜色+形状检测（准确但稍慢）
    }
}

// 测试代码
fun testDetectBlueOrb2() {
    val bossRect = Rect(200, 260, 700, 200)

    println("=== 测试 ay491.png (有蓝球) ===")
    val img491 = Imgcodecs.imread("C:\\Users\\Administrator\\Desktop\\debug\\ay493.png")
    val roi491 = Mat(img491, bossRect)

    // 方法1：仅颜色检测
    val result491Color = BlueOrbDetector.detectBlueOrbByColor(roi491, debugMode = true)
    println("颜色检测结果: $result491Color")

    // 方法2：颜色+轮廓检测
    val result491Contour = BlueOrbDetector.detectBlueOrbByContour(roi491, debugMode = true)
    println("轮廓检测结果: $result491Contour")

    roi491.release()
    img491.release()

    println("\n=== 测试 ay492.png (无蓝球) ===")
    val img492 = Imgcodecs.imread("C:\\Users\\Administrator\\Desktop\\debug\\ay492.png")
    val roi492 = Mat(img492, bossRect)

    // 方法1：仅颜色检测
    val result492Color = BlueOrbDetector.detectBlueOrbByColor(roi492, debugMode = true)
    println("颜色检测结果: $result492Color")

    // 方法2：颜色+轮廓检测
    val result492Contour = BlueOrbDetector.detectBlueOrbByContour(roi492, debugMode = true)
    println("轮廓检测结果: $result492Contour")

    roi492.release()
    img492.release()

    // 如果还想测试模板匹配，可以这样：
    println("\n=== 多尺度模板匹配测试 ===")
    val template = Imgcodecs.imread("blueball.png")
    if (!template.empty()) {
        val img491_2 = Imgcodecs.imread("C:\\Users\\Administrator\\Desktop\\debug\\ay491.png")
        val roi491_2 = Mat(img491_2, bossRect)

        val (found, score) = BlueOrbDetector.detectBlueOrbByTemplateMultiScale(
            template, roi491_2, threshold = 0.7, debugMode = true
        )
        println("491 多尺度模板匹配: found=$found, score=${String.format("%.4f", score)}")

        roi491_2.release()
        img491_2.release()
        template.release()
    }
}

// 实际使用示例
fun main() {
    // 加载 OpenCV 库
    try {
        Class.forName("nu.pattern.OpenCV")
        nu.pattern.OpenCV.loadLocally()
    } catch (e: Exception) {
        System.loadLibrary(org.opencv.core.Core.NATIVE_LIBRARY_NAME)
    }

    testDetectBlueOrb2()
}
