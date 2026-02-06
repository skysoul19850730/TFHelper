import org.opencv.core.*
import org.opencv.imgproc.Imgproc
import org.opencv.imgcodecs.Imgcodecs
import java.awt.Point

/**
 * 鲁棒模板匹配器：通过动态占位色隔离透明区域，并归一化匹配得分
 * 支持任意颜色模板，彻底解决“透明区拉低匹配率”问题
 */
object RobustMatcher {

    private val CANDIDATE_COLORS = listOf(
        Scalar(0.0, 0.0, 0.0),        // 黑
        Scalar(255.0, 255.0, 255.0),  // 白
        Scalar(0.0, 0.0, 255.0),      // 红
        Scalar(0.0, 255.0, 0.0),      // 绿
        Scalar(255.0, 0.0, 0.0),      // 蓝
        Scalar(255.0, 0.0, 255.0)     // 洋红
    )

    /**
     * 匹配模板到目标图
     * @param templatePath 模板路径（PNG，含 Alpha）
     * @param targetPath 目标图路径（任意格式）
     * @param minScore 归一化后的最小匹配阈值（默认 0.75）
     * @return Pair<归一化匹配率 [0.0~1.0], 最佳位置 Point?>，失败返回 (0.0, null)
     */
    fun match(templatePath: String, targetPath: String, minScore: Double = 0.75): Pair<Double, Point?> {
        val template = Imgcodecs.imread(templatePath, Imgcodecs.IMREAD_UNCHANGED)
        val target = Imgcodecs.imread(targetPath, Imgcodecs.IMREAD_COLOR)

        if (template.empty() || target.empty()) return 0.0 to null

        try {
            return robustMatch(template, target, minScore)
        } finally {
            template.release()
            target.release()
        }
    }

    fun robustMatch(template: Mat, target: Mat, minScore: Double): Pair<Double, Point?> {
        // 1. 分离 BGR + Alpha（OpenCV 默认 BGRA）
        val channels = mutableListOf(Mat(), Mat(), Mat(), Mat())
        Core.split(template, channels)
        val b = channels[0]; val g = channels[1]; val r = channels[2]; val a = channels[3]
        val templateBgr = Mat(); Core.merge(listOf(b, g, r), templateBgr)
        val alphaMask = Mat()
        Core.compare(a, Scalar(1.0), alphaMask, Core.CMP_GT) // alpha > 0 → 255

        // 2. 提取非透明颜色 + 计算占比
        val (nonTransparentColors, validRatio) = getNonTransparentInfo(templateBgr, alphaMask)
        if (nonTransparentColors.isEmpty() || validRatio <= 0) return 0.0 to null

        // 3. 动态选色
        val placeholderA = selectBestColor(nonTransparentColors, CANDIDATE_COLORS)
        val placeholderB = selectBestColor(
            nonTransparentColors + listOf(placeholderA),
            CANDIDATE_COLORS.filter { it != placeholderA }
        )

        // 4. 处理模板：非透明保留，透明→placeholderA
        val procTemplate = Mat.zeros(templateBgr.size(), CvType.CV_8UC3)
        templateBgr.copyTo(procTemplate, alphaMask)
        val transparentMask = Mat()
        Core.compare(alphaMask, Scalar(0.0), transparentMask, Core.CMP_EQ) // alpha == 0
        procTemplate.setTo(placeholderA, transparentMask)

        // 5. 处理目标图：所有 ≈ placeholderA 的像素 → placeholderB
        val diff = Mat()
        Core.absdiff(target, placeholderA, diff)
        val maxDist = Math.sqrt(3.0) * 255.0 * 0.1
        val maskA = Mat()
        Core.compare(diff, Scalar(maxDist, maxDist, maxDist), maskA, Core.CMP_LT)
        val procTarget = Mat.zeros(target.size(), CvType.CV_8UC3)
        target.copyTo(procTarget)
        procTarget.setTo(placeholderB, maskA)

        // 6. 精确匹配：滑动窗口计算非透明像素匹配率
        val (bestScoreRaw, bestPos) = findBestMatch(procTarget, procTemplate, alphaMask, validRatio)
        val normalizedScore = if (validRatio > 0) bestScoreRaw / validRatio else 0.0

        return normalizedScore to (if (normalizedScore >= minScore) bestPos else null)
    }

    /**
     * 返回 (非透明颜色列表, 非透明像素占比)
     */
    private fun getNonTransparentInfo(bgr: Mat, alphaMask: Mat): Pair<List<Scalar>, Double> {
        val colors = mutableListOf<Scalar>()
        var validCount = 0L
        for (y in 0 until bgr.rows()) {
            for (x in 0 until bgr.cols()) {
                if (alphaMask.get(y, x)[0] > 0) {
                    val b = bgr.get(y, x)[0]
                    val g = bgr.get(y, x)[1]
                    val r = bgr.get(y, x)[2]
                    colors.add(Scalar(b, g, r))
                    validCount++
                }
            }
        }
        val total = bgr.total().toDouble()
        val ratio = if (total > 0) validCount.toDouble() / total else 0.0
        return colors to ratio
    }

    /**
     * 滑动窗口查找最佳匹配位置（返回 rawScore 和位置）
     * rawScore = 匹配成功的非透明像素数 / 模板总像素数
     */
    private fun findBestMatch(
        target: Mat,
        template: Mat,
        mask: Mat,
        validRatio: Double
    ): Pair<Double, Point> {
        val tw = template.cols()
        val th = template.rows()
        val dw = target.cols()
        val dh = target.rows()

        var bestRawScore = 0.0
        var bestPos = Point(0, 0)

        for (ty in 0 until dh - th + 1) {
            for (tx in 0 until dw - tw + 1) {
                var matchCount = 0.0
                for (y in 0 until th) {
                    for (x in 0 until tw) {
                        if (mask.get(y, x)[0] > 0) {
                            val tVal = template.get(y, x)
                            val tgVal = target.get(ty + y, tx + x)
                            if (euclideanDist(
                                    Scalar(tVal[0], tVal[1], tVal[2]),
                                    Scalar(tgVal[0], tgVal[1], tgVal[2])
                                ) < 15.0) {
                                matchCount++
                            }
                        }
                    }
                }
                val rawScore = matchCount / (tw * th).toDouble()
                if (rawScore > bestRawScore) {
                    bestRawScore = rawScore
                    bestPos = Point(tx, ty)
                }
            }
        }
        return bestRawScore to bestPos
    }

    private fun selectBestColor(excludeColors: List<Scalar>, candidates: List<Scalar>): Scalar {
        var bestDist = -1.0
        var bestColor: Scalar? = null
        for (cand in candidates) {
            var minDist = Double.MAX_VALUE
            for (col in excludeColors) {
                val d = euclideanDist(col, cand)
                if (d < minDist) minDist = d
            }
            if (minDist > bestDist) {
                bestDist = minDist
                bestColor = cand
            }
        }
        return bestColor ?: Scalar(0.0, 0.0, 0.0)
    }

    private fun euclideanDist(c1: Scalar, c2: Scalar): Double {
        val db = c1.`val`[0] - c2.`val`[0]
        val dg = c1.`val`[1] - c2.`val`[1]
        val dr = c1.`val`[2] - c2.`val`[2]
        return Math.sqrt(db * db + dg * dg + dr * dr)
    }
}
