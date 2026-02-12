package opencv

import androidx.compose.ui.res.loadImageBitmap
import data.MRect
import getImageFromFile
import org.opencv.core.*
import org.opencv.imgcodecs.Imgcodecs
import org.opencv.imgproc.Imgproc
import java.awt.image.BufferedImage
import java.awt.image.BufferedImage.*
import java.awt.image.DataBufferByte
import java.awt.image.DataBufferInt
import java.io.File

fun Mat.toGray(autoRelease: Boolean = true): Mat {
    val gray = Mat()
    Imgproc.cvtColor(this, gray, Imgproc.COLOR_BGR2GRAY)
    if (autoRelease) {
        this.release()
    }
    return gray
}

fun String.toMat(): Mat = Imgcodecs.imread(this)

/**
 * 裁剪小的，一般可能原图还继续处理，比如继续裁剪其他图标，所以不自动释放
 */
fun Mat.subMat(rect: MRect, autoRelease: Boolean = false): Mat {
    val mat = Mat(this, Rect(rect.left, rect.top, rect.width, rect.height)).clone()
    if (autoRelease) {
        this.release()
    }
    return mat
}

fun Mat.binary(autoRelease: Boolean = true): Mat {
    val binary = Mat()
    Imgproc.threshold(this, binary, 0.0, 255.0, Imgproc.THRESH_BINARY + Imgproc.THRESH_OTSU)
    if (autoRelease) {
        this.release()
    }
    return binary
}

fun Mat.saveToImg(src: Mat? = null, path: String? = null): BufferedImage? {

    val path = if (path == null) "${System.getProperty("user.dir")}/temp.png"
    else if (path.contains(File.separatorChar)) {
        path
    } else "${System.getProperty("user.dir")}/${path}.png"

    try {

        Imgcodecs.imwrite(path, this)
    } catch (e: Exception) {
// 绘制所有轮廓到新图像并保存（便于查看）
        if (this is MatOfPoint && src != null) {
            this.saveImg(src)
        } else {
            return null
        }
    }

    return getImageFromFile(File(path))
}

private fun MatOfPoint.saveImg(roi: Mat) {
    val mask = Mat.zeros(roi.size(), CvType.CV_8UC1)
// ✅ 关键：thickness = -1 表示实心填充！
    Imgproc.drawContours(mask, listOf(this), -1, Scalar(255.0), -1)

// 提取原图中轮廓区域内容（保留真实颜色）
    val filled = Mat()
    Core.bitwise_and(roi, roi, filled, mask)
    val path = "${java.lang.System.getProperty("user.dir")}/temp.png"
// 保存（PNG支持透明，但此处是彩色图）
    Imgcodecs.imwrite(path, filled)

    mask.release()
    filled.release()
    getImageFromFile(File(path))
}


private fun convertDataBufferIntToBytes(dataBufferInt: DataBufferInt): DataBufferByte {
    val intData = dataBufferInt.data
    // 每个int值代表RGB三个字节（去掉Alpha通道）
    val byteSize = intData.size * 3  // 每个int有3个字节（RGB）
    val byteData = ByteArray(byteSize)

    for (i in intData.indices) {
        val intValue = intData[i]
        // 将int拆分为3个字节（RGB）mat 顺序是bgr，
        byteData[i * 3] = (intValue and 0xFF).toByte()          // Blue
        byteData[i * 3 + 1] = ((intValue shr 8) and 0xFF).toByte()  // Green
        byteData[i * 3 + 2] = ((intValue shr 16) and 0xFF).toByte() // Red
    }

    return DataBufferByte(byteData, byteData.size)
}

private fun convertDataBufferIntToBytes4(dataBufferInt: DataBufferInt): DataBufferByte {
    val intData = dataBufferInt.data
    // 假设每个int值代表RGBA四个字节
    val byteSize = intData.size * 4  // 每个int有4个字节
    val byteData = ByteArray(byteSize)

    for (i in intData.indices) {
        val intValue = intData[i]
        // 将int拆分为4个字节
        byteData[i * 4] = (intValue and 0xFF).toByte()          // Blue
        byteData[i * 4 + 1] = ((intValue shr 8) and 0xFF).toByte()  // Green
        byteData[i * 4 + 2] = ((intValue shr 16) and 0xFF).toByte() // Red
        byteData[i * 4 + 3] = ((intValue shr 24) and 0xFF).toByte()     // Alpha
    }

    return DataBufferByte(byteData, byteData.size)
}


fun BufferedImage.toMat3(): Mat {
    val img = BufferedImage(this.width, this.height, BufferedImage.TYPE_3BYTE_BGR)
    img.graphics.drawImage(this, 0, 0, null)
    val mat = Mat(this.height, this.width, CvType.CV_8UC3)
    var imgbuffer = img.raster.dataBuffer as DataBufferByte
    val data = imgbuffer.data
    mat.put(0, 0, data)
    return mat
}

/**
 * 尽量只具有透明特征的模板图使用mat4通道，配合mask只对比非透明部分，防止背景干扰，比如很多球状的模板
 * 模板使用mat4后，target也要用mat4
 */
fun BufferedImage.toMat4(): Mat {
    val img = BufferedImage(this.width, this.height, BufferedImage.TYPE_4BYTE_ABGR)
    img.graphics.drawImage(this, 0, 0, null)
    val mat = Mat(this.height, this.width, CvType.CV_8UC4)
    var imgbuffer = img.raster.dataBuffer as DataBufferByte
    val data = imgbuffer.abgrToBgra().data
    mat.put(0, 0, data)
    return mat
}

fun DataBufferByte.abgrToBgra(): DataBufferByte {
    val originalData = this.data
    val pixelCount = originalData.size / 4
    val bgraData = ByteArray(originalData.size)

    for (i in 0 until pixelCount) {
        bgraData[i * 4] = originalData[i * 4 + 1]     // Blue
        bgraData[i * 4 + 1] = originalData[i * 4 + 2] // Green
        bgraData[i * 4 + 2] = originalData[i * 4 + 3] // Red
        bgraData[i * 4 + 3] = originalData[i * 4]     // Alpha
    }

    return DataBufferByte(bgraData, bgraData.size)
}

/**
 * 根据原图通道数量构建mat，但比较时比如一个透明一个不透明是会报错的，要两个mat一样，所以如果确定不一样，想强制一样的话，就用toMat3和toMat4
 */
fun BufferedImage.toMat(): Mat {
    val newType = when (this.type) {
        TYPE_INT_ARGB, TYPE_4BYTE_ABGR -> BufferedImage.TYPE_4BYTE_ABGR
        else -> TYPE_3BYTE_BGR
    }
    val matType = when (this.type) {
        TYPE_INT_ARGB, TYPE_4BYTE_ABGR -> CvType.CV_8UC4
        else -> CvType.CV_8UC3
    }
    val img = BufferedImage(this.width, this.height, newType)
    img.graphics.drawImage(this, 0, 0, null)

    val mat = Mat(this.height, this.width, matType)
    var imgbuffer = img.raster.dataBuffer as DataBufferByte
    if(matType == CvType.CV_8UC4){
        imgbuffer = imgbuffer.abgrToBgra()
    }
    val data = imgbuffer.data
    mat.put(0, 0, data)
    return mat
}

fun BufferedImage.hasImage(template: BufferedImage,isAlpha:Boolean=false,rate: Double = 0.75):Boolean{
    if(isAlpha){
        val mat = template.toMat4()
        val channels = mutableListOf<Mat>()
        Core.split(mat, channels)
        val a = channels[3]
        // 2. 构建 mask：alpha > 0 的区域为 255，否则 0
        val mask = Mat()
        Core.compare(a, Scalar(1.0), mask, Core.CMP_GT) // alpha > 0 → 255
        val result = Mat()
        val target = this.toMat4()
        Imgproc.matchTemplate(
            target,
            mat,
            result,
            Imgproc.TM_CCORR_NORMED, // 支持 mask
            mask
        )
        val rrr = Core.minMaxLoc(result)
        mat.release()
        mask.release()
        channels.forEach {
            it.release()
        }
        target.release()
        result.release()
        return rrr.maxVal>=rate
    }else{
        val tM3 = template.toMat3()
        val target = this.toMat3()

        val result = MatSearch.templateFit(tM3, target,rate)
        tM3.release()
        target.release()
        return result
    }
}


