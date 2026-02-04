package opencv

import androidx.compose.ui.res.loadImageBitmap
import data.MRect
import getImageFromFile
import org.opencv.core.*
import org.opencv.imgcodecs.Imgcodecs
import org.opencv.imgproc.Imgproc
import java.awt.image.BufferedImage
import java.awt.image.DataBufferByte
import java.awt.image.DataBufferInt
import java.io.File

fun Mat.toGray(autoRelease:Boolean = true):Mat{
    val gray = Mat()
    Imgproc.cvtColor(this, gray,Imgproc.COLOR_BGR2GRAY)
    if(autoRelease){
        this.release()
    }
    return gray
}

fun String.toMat():Mat = Imgcodecs.imread(this)

/**
 * 裁剪小的，一般可能原图还继续处理，比如继续裁剪其他图标，所以不自动释放
 */
fun Mat.subMat(rect: MRect,autoRelease:Boolean = false):Mat{
    val mat = Mat(this, Rect(rect.left,rect.top,rect.width,rect.height)).clone()
    if(autoRelease){
        this.release()
    }
    return mat
}

fun Mat.binary(autoRelease:Boolean = true):Mat{
    val binary = Mat()
    Imgproc.threshold(this, binary, 0.0, 255.0, Imgproc.THRESH_BINARY + Imgproc.THRESH_OTSU)
    if(autoRelease){
        this.release()
    }
    return binary
}

fun Mat.saveToImg(src:Mat?=null,path:String?=null):BufferedImage?{

    val path = if(path==null) "${System.getProperty("user.dir")}/temp.png"
    else if(path.contains(File.separatorChar)){
         path
    }else "${System.getProperty("user.dir")}/${path}.png"

    try {

        Imgcodecs.imwrite(path, this)
    } catch (e: Exception) {
// 绘制所有轮廓到新图像并保存（便于查看）
        if (this is MatOfPoint && src != null) {
            this.saveImg(src)
        }else{
            return null
        }
    }

    return getImageFromFile(File(path))
}

private fun MatOfPoint.saveImg(roi:Mat){
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

fun BufferedImage.toMat(): Mat {
    val img = BufferedImage(this.width, this.height, this.type)
    img.graphics.drawImage(this, 0, 0, null)

    val mat = Mat(this.height, this.width, CvType.CV_8UC3)
    var imgbuffer = img.raster.dataBuffer as? DataBufferByte
    if(imgbuffer == null){
        imgbuffer = convertDataBufferIntToBytes(img.raster.dataBuffer as DataBufferInt)
    }
    val data = imgbuffer.data
    mat.put(0, 0, data)
    return mat
}


private fun convertDataBufferIntToBytes(dataBufferInt: DataBufferInt): DataBufferByte {
    val intData = dataBufferInt.data
    // 假设每个int值代表RGBA四个字节
    val byteSize = intData.size * 4  // 每个int有4个字节
    val byteData = ByteArray(byteSize)

    for (i in intData.indices) {
        val intValue = intData[i]
        // 将int拆分为4个字节
        byteData[i * 4] = ((intValue shr 24) and 0xFF).toByte()     // Alpha
        byteData[i * 4 + 1] = ((intValue shr 16) and 0xFF).toByte() // Red
        byteData[i * 4 + 2] = ((intValue shr 8) and 0xFF).toByte()  // Green
        byteData[i * 4 + 3] = (intValue and 0xFF).toByte()          // Blue
    }

    return DataBufferByte(byteData, byteData.size)
}
