package opencv

import data.MRect
import getImageFromFile
import org.opencv.core.*
import org.opencv.imgcodecs.Imgcodecs
import org.opencv.imgproc.Imgproc
import java.awt.image.BufferedImage
import java.awt.image.DataBufferByte
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

fun Mat.saveToImg(path:String?=null):BufferedImage{

    val path = if(path==null) "${System.getProperty("user.dir")}/temp.png"
    else if(path.contains(File.separatorChar)){
         path
    }else "${System.getProperty("user.dir")}/${path}.png"

    try {

        Imgcodecs.imwrite(path, this)
    } catch (e: Exception) {
// 绘制所有轮廓到新图像并保存（便于查看）
        val debugImg = Mat.zeros(this.size(), CvType.CV_8UC3)
        if (this is MatOfPoint) {
            Imgproc.drawContours(debugImg, listOf(this), -1, Scalar(0.0, 255.0, 0.0), 2) // 绿色轮廓
        }
        Imgcodecs.imwrite(
            path,
            debugImg
        )
    }

    return getImageFromFile(File(path))
}

fun BufferedImage.toMat(): Mat {
    val img = BufferedImage(this.width, this.height, this.type)
    img.graphics.drawImage(this, 0, 0, null)

    val mat = Mat(this.height, this.width, CvType.CV_8UC3)
    val data = (img.raster.dataBuffer as DataBufferByte).data
    mat.put(0, 0, data)
    return mat
}