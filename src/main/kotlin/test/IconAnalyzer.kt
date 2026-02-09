package test

import data.MRect
import getSubImage
import org.opencv.core.*
import org.opencv.imgproc.Imgproc
import utils.ImgUtil
import java.awt.image.BufferedImage
import java.util.*
import kotlin.math.abs

/**
 * 使用 OpenCV 分析游戏界面中的技能图标特征
 */
class IconAnalyzer {
    // 图标特征类
    data class IconFeature(
        val pattern: String, // "axe" / "staff"
        val border: String,  // "square" / "circle"
        val fill: String     // "hollow" / "solid"
    )
    // 图标区域（需根据实际截图调整）
    private val iconRects = listOf(
        MRect.create4P(350, 200, 400, 250),
        MRect.create4P(450, 200, 500, 250),
        MRect.create4P(350, 300, 400, 350),
        MRect.create4P(450, 300, 500, 350)
    )

    // 运行图标区域
    private val runningIconRect = MRect.create4P(300, 380, 360, 440)

    /**
     * 主入口：传入 BufferedImage，返回是否为“不一样的那个”
     */
    fun isRunningIconDifferent(bufferedImage: BufferedImage): Boolean {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME)
        val iconImages = iconRects.map { rect ->
            bufferedImage.getSubImage(rect)
        }

        val runningImage = bufferedImage.getSubImage(runningIconRect)

        val features = extractFeatures(iconImages)
        val runningFeature = extractFeature(runningImage)

        val differentIndex = findDifferentIcon(features)
        val matchedIndex = features.indexOfFirst { it == runningFeature }

        return differentIndex == matchedIndex
    }

    /**
     * 提取单个图标的特征
     */
    private fun extractFeature(image: BufferedImage): IconFeature {
        val mat = toMat(image)
        val pattern = detectPattern(mat)
        val border = detectBorder(mat)
        val fill = detectFill(mat)
        return IconFeature(pattern, border, fill)
    }

    /**
     * 提取多个图标的特征
     */
    private fun extractFeatures(images: List<BufferedImage>): List<IconFeature> {
        return images.map { extractFeature(it) }
    }

    /**
     * 检测图案：法杖 vs 斧头
     */
    private fun detectPattern(mat: Mat): String {
        val gray = Mat()
        Imgproc.cvtColor(mat, gray, Imgproc.COLOR_BGR2GRAY)
        val contours = ArrayList<MatOfPoint>()
        Imgproc.findContours(gray, contours, Mat(), Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE)

        val largestContour = contours.maxByOrNull { Imgproc.contourArea(it) } ?: return "axe"
        val hull = MatOfInt()
        val contourMat = Mat()
        largestContour.convertTo(contourMat, CvType.CV_32F)
        Imgproc.convexHull(contourMat as MatOfPoint?, hull)
         val area = Imgproc.contourArea(largestContour)
        val hullArea = Imgproc.contourArea(hull)
        val solidity = area / hullArea

        // 法杖通常更细长，solidity 更高
        return if (solidity > 0.8) "staff" else "axe"
    }

    /**
     * 检测边框：方形 vs 圆形
     */
    private fun detectBorder(mat: Mat): String {
        val gray = Mat()
        Imgproc.cvtColor(mat, gray, Imgproc.COLOR_BGR2GRAY)
        val contours = ArrayList<MatOfPoint>()
        Imgproc.findContours(gray, contours, Mat(), Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE)

        val largestContour = contours.maxByOrNull { Imgproc.contourArea(it) } ?: return "circle"
        val approx = MatOfInt()
        val contourMat = MatOfPoint()
        largestContour.convertTo(contourMat, CvType.CV_32F)
        Imgproc.convexHull(contourMat, approx)
        return if (approx.total() == 4L) "square" else "circle"
    }

    /**
     * 检测填色：空心 vs 实心
     */
    private fun detectFill(mat: Mat): String {
        val gray = Mat()
        Imgproc.cvtColor(mat, gray, Imgproc.COLOR_BGR2GRAY)
        val mean = Core.mean(gray)
        val threshold = 100
        return if (mean.`val`.get(0) < threshold) "solid" else "hollow"
    }

    /**
     * 找出唯一不同的图标索引
     */
    private fun findDifferentIcon(features: List<IconFeature>): Int {
        val counts = mutableMapOf<String, Int>()
        features.forEach { feature ->
            val key = "${feature.pattern}_${feature.border}_${feature.fill}"
            counts[key] = counts.getOrDefault(key, 0) + 1
        }
        return features.indexOfFirst { 
            val key = "${it.pattern}_${it.border}_${it.fill}"
            counts[key] == 1 
        }
    }

    /**
     * 将 BufferedImage 转换为 OpenCV Mat
     */
    private fun toMat(image: BufferedImage): Mat {
        val mat = Mat(image.height, image.width, CvType.CV_8UC3)
        val buffer = image.getRaster().getDataBuffer()
        val data = ByteArray(buffer.size)
        for (i in 0 until buffer.size) {
            data[i] = buffer.getElem(i).toByte()
        }
        mat.put(0, 0, data)
        return mat
    }
}
