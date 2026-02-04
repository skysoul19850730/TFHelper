import org.opencv.core.*
import org.opencv.imgcodecs.Imgcodecs
import org.opencv.imgproc.Imgproc

object RobustMatcher {

    /**
     * 亮度归一化模板匹配
     * 解决光照变化导致的匹配度下降
     */
    fun templateMatchNormalized(template: Mat, target: Mat): Core.MinMaxLocResult {
        // 转换为灰度图，减少颜色干扰
        val grayTemplate = Mat()
        val grayTarget = Mat()

        if (template.channels() > 1) {
            Imgproc.cvtColor(template, grayTemplate, Imgproc.COLOR_BGR2GRAY)
        } else {
            template.copyTo(grayTemplate)
        }

        if (target.channels() > 1) {
            Imgproc.cvtColor(target, grayTarget, Imgproc.COLOR_BGR2GRAY)
        } else {
            target.copyTo(grayTarget)
        }

        // 使用 TM_CCOEFF_NORMED 方法，对亮度变化不敏感
        val result = Mat()
        Imgproc.matchTemplate(grayTarget, grayTemplate, result, Imgproc.TM_CCOEFF_NORMED)
        val mmr = Core.minMaxLoc(result)

        grayTemplate.release()
        grayTarget.release()
        result.release()

        return mmr
    }

    /**
     * 多尺度模板匹配（修复版）
     * 尺寸范围更广，步长更小
     */
    fun multiScaleMatch(
        template: Mat,
        target: Mat,
        scaleRange: ClosedRange<Double> = 0.6..1.6,
        scaleStep: Double = 0.05,
        debugMode: Boolean = false
    ): Pair<Double, Double> {
        var bestScore = 0.0
        var bestScale = 1.0

        var scale = scaleRange.start
        while (scale <= scaleRange.endInclusive) {
            val width = (template.cols() * scale).toInt()
            val height = (template.rows() * scale).toInt()

            // 检查尺寸是否合法
            if (width <= 0 || height <= 0 || width > target.cols() || height > target.rows()) {
                scale += scaleStep
                continue
            }

            val resized = Mat()
            Imgproc.resize(template, resized, Size(width.toDouble(), height.toDouble()), 0.0, 0.0, Imgproc.INTER_LINEAR)

            val mmr = templateMatchNormalized(resized, target)

            if (debugMode) {
                println("缩放 ${String.format("%.2f", scale)}: 匹配度 ${String.format("%.4f", mmr.maxVal)}")
            }

            if (mmr.maxVal > bestScore) {
                bestScore = mmr.maxVal
                bestScale = scale
            }

            resized.release()
            scale += scaleStep
        }

        if (debugMode) {
            println("最佳缩放: ${String.format("%.2f", bestScale)}, 最高分: ${String.format("%.4f", bestScore)}")
        }

        return Pair(bestScore, bestScale)
    }

    /**
     * 旋转不变性匹配（针对有旋转的情况）
     */
    fun rotationInvariantMatch(
        template: Mat,
        target: Mat,
        angleRange: IntRange = -15..15,
        angleStep: Int = 5,
        debugMode: Boolean = false
    ): Pair<Double, Int> {
        var bestScore = 0.0
        var bestAngle = 0

        for (angle in angleRange step angleStep) {
            val rotated = Mat()
            val center = Point(template.cols() / 2.0, template.rows() / 2.0)
            val rotMat = Imgproc.getRotationMatrix2D(center, angle.toDouble(), 1.0)
            Imgproc.warpAffine(template, rotated, rotMat, template.size())

            val mmr = templateMatchNormalized(rotated, target)

            if (debugMode) {
                println("旋转 ${angle}°: 匹配度 ${String.format("%.4f", mmr.maxVal)}")
            }

            if (mmr.maxVal > bestScore) {
                bestScore = mmr.maxVal
                bestAngle = angle
            }

            rotated.release()
            rotMat.release()
        }

        if (debugMode) {
            println("最佳旋转: ${bestAngle}°, 最高分: ${String.format("%.4f", bestScore)}")
        }

        return Pair(bestScore, bestAngle)
    }

    /**
     * 终极鲁棒匹配：多尺度 + 旋转不变性
     */
    fun robustMatch(
        template: Mat,
        target: Mat,
        scaleRange: ClosedRange<Double> = 0.7..1.3,
        scaleStep: Double = 0.05,
        angleRange: IntRange = -10..10,
        angleStep: Int = 5,
        debugMode: Boolean = false
    ): Triple<Double, Double, Int> {
        var bestScore = 0.0
        var bestScale = 1.0
        var bestAngle = 0

        var scale = scaleRange.start
        while (scale <= scaleRange.endInclusive) {
            val width = (template.cols() * scale).toInt()
            val height = (template.rows() * scale).toInt()

            if (width <= 0 || height <= 0 || width > target.cols() || height > target.rows()) {
                scale += scaleStep
                continue
            }

            val resized = Mat()
            Imgproc.resize(template, resized, Size(width.toDouble(), height.toDouble()))

            // 对每个尺寸测试不同角度
            for (angle in angleRange step angleStep) {
                val rotated = Mat()
                if (angle != 0) {
                    val center = Point(resized.cols() / 2.0, resized.rows() / 2.0)
                    val rotMat = Imgproc.getRotationMatrix2D(center, angle.toDouble(), 1.0)
                    Imgproc.warpAffine(resized, rotated, rotMat, resized.size())
                    rotMat.release()
                } else {
                    resized.copyTo(rotated)
                }

                val mmr = templateMatchNormalized(rotated, target)

                if (mmr.maxVal > bestScore) {
                    bestScore = mmr.maxVal
                    bestScale = scale
                    bestAngle = angle
                }

                rotated.release()
            }

            resized.release()
            scale += scaleStep
        }

        if (debugMode) {
            println("最佳参数: 缩放=${String.format("%.2f", bestScale)}, 旋转=${bestAngle}°, 分数=${String.format("%.4f", bestScore)}")
        }

        return Triple(bestScore, bestScale, bestAngle)
    }
}

// 测试代码
fun testRobustMatch() {
    val template = Imgcodecs.imread("blueball.png")
    if (template.empty()) {
        println("无法读取 blueball.png")
        return
    }

    println("=== 测试 493 (模板来源) ===")
    val img493 = Imgcodecs.imread("C:\\Users\\Administrator\\Desktop\\debug\\ay493.png")
    if (!img493.empty()) {
        val bossRect = Rect(200, 260, 700, 200)
        val roi493 = Mat(img493, bossRect)

        // 方法1：基础匹配
        val basic493 = RobustMatcher.templateMatchNormalized(template, roi493)
        println("基础匹配: ${String.format("%.4f", basic493.maxVal)}")

        // 方法2：多尺度匹配
        val (score493, scale493) = RobustMatcher.multiScaleMatch(template, roi493, debugMode = false)
        println("多尺度匹配: 分数=${String.format("%.4f", score493)}, 缩放=${String.format("%.2f", scale493)}")

        roi493.release()
        img493.release()
    }

    println("\n=== 测试 491 (蓝球移动后) ===")
    val img491 = Imgcodecs.imread("C:\\Users\\Administrator\\Desktop\\debug\\ay491.png")
    if (!img491.empty()) {
        val bossRect = Rect(200, 260, 700, 200)
        val roi491 = Mat(img491, bossRect)

        // 方法1：基础匹配
        val basic491 = RobustMatcher.templateMatchNormalized(template, roi491)
        println("基础匹配: ${String.format("%.4f", basic491.maxVal)}")

        // 方法2：多尺度匹配
        val (score491, scale491) = RobustMatcher.multiScaleMatch(template, roi491, debugMode = true)
        println("多尺度匹配: 分数=${String.format("%.4f", score491)}, 缩放=${String.format("%.2f", scale491)}")

        // 方法3：终极鲁棒匹配（如果还不行就用这个）
        println("\n尝试旋转不变性匹配：")
        val (scoreRobust, scaleRobust, angleRobust) = RobustMatcher.robustMatch(template, roi491, debugMode = false)
        println("鲁棒匹配: 分数=${String.format("%.4f", scoreRobust)}, 缩放=${String.format("%.2f", scaleRobust)}, 旋转=${angleRobust}°")

        roi491.release()
        img491.release()
    }

    template.release()
}

fun main() {
    try {
        Class.forName("nu.pattern.OpenCV")
        nu.pattern.OpenCV.loadLocally()
    } catch (e: Exception) {
        System.loadLibrary(org.opencv.core.Core.NATIVE_LIBRARY_NAME)
    }

    testRobustMatch()
}
