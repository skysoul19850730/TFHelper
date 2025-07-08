package test

import com.sun.jna.platform.WindowUtils
import data.Config
import data.MPoint
import getImage
import kotlinx.coroutines.delay
import log
import logOnly
import nu.pattern.OpenCV
import org.apache.commons.io.FileUtils
import org.apache.http.HttpHeaders
import org.apache.http.NameValuePair
import org.apache.http.client.entity.UrlEncodedFormEntity
import org.apache.http.client.methods.HttpPost
import org.apache.http.impl.client.HttpClients
import org.apache.http.message.BasicNameValuePair
import org.apache.http.protocol.HTTP
import org.apache.http.util.EntityUtils
import org.opencv.core.Core
import org.opencv.core.Mat
import org.opencv.core.MatOfByte
import org.opencv.core.MatOfInt
import org.opencv.core.Size
import org.opencv.imgcodecs.Imgcodecs
import org.opencv.imgproc.Imgproc
import utils.ImgUtil
import utils.MRobot
import java.awt.event.InputEvent
import java.awt.event.KeyEvent
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.net.URI
import java.util.*
import javax.imageio.ImageIO
import kotlin.math.max

object WindowTest {
    data class Credentials(
        var ak :String="",
        var sk:String="",
        var service:String="",
        var region:String=""
    )
    fun test(){
        WindowUtils.getAllWindows(true).forEach {
            logOnly("${it.title}")
            if(it.title.contains("微信")){
                var x = it.locAndSize.x
                var y = it.locAndSize.y
                MRobot.robot.mouseMove(x+15,y+15)
                MRobot.robot.mousePress(InputEvent.BUTTON1_DOWN_MASK)
                MRobot.robot.delay(3000)
                MRobot.robot.mouseMove(15,15)
                MRobot.robot.delay(3000)
                MRobot.robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK)
            }
        }
    }

    suspend fun imgText(img:BufferedImage):String{
        return ""
    }

    fun Resize(mat: Mat, maxSize: Int): Mat {
        // 原图的宽、高
        val w = mat.cols()
        val h = mat.rows()
        val maxWH = max(w.toDouble(), h.toDouble()).toInt()
        if (maxWH < maxSize) {
            System.out.printf("image max side is: %d < %d, not resize\n", maxWH, maxSize)
            return mat
        }
        val ratio = maxSize.toDouble() / max(w.toDouble(), h.toDouble())
        val width = (w * ratio).toInt()
        val height = (h * ratio).toInt()
        System.out.printf("target image width: %d, height: %d\n", width, height)
        val size = Size(width.toDouble(), height.toDouble())
        Imgproc.resize(mat, mat, size, 0.0, 0.0, Imgproc.INTER_LINEAR)
        return mat
    }

    fun img2base64(img:BufferedImage): String {
        // 加载OpenCV动态库
//        OpenCV.loadShared()
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME)
//        val imgPath = "demo.jpg"
//        System.out.printf("image file size: %d 字节\n", FileUtils.sizeOf(File(imgPath)))

        var baos = ByteArrayOutputStream()
        ImageIO.write(img,"png",baos)

        // 1.1 从文件读取图片（OpenCV 会根据Exif方向信息把图片转正）
        val mat1 = Imgcodecs.imdecode(MatOfByte(*baos.toByteArray()), Imgcodecs.IMREAD_COLOR)
//        System.out.printf("image width: %d, height: %d\n", mat1.cols(), mat1.rows())

        // 1.2 从二进制数据解码图片
//        try {
//            // 读取图片，获取二进制数据
//            val bytes = FileUtils.readFileToByteArray(File(imgPath))
//            val mat2 = Imgcodecs.imdecode(MatOfByte(*bytes), Imgcodecs.IMREAD_COLOR)
//            System.out.printf("image width: %d, height: %d\n", mat2.cols(), mat2.rows())
//        } catch (e: IOException) {
//            println(e.message)
//            e.printStackTrace()
//        }

        // 2 图片缩放，长边最大到2048
        val mat3 = Resize(mat1, 2048)
        System.out.printf("after resize, image width: %d, height: %d\n", mat3.cols(), mat3.rows())

        // 3 保存图片
        // 保存成JPG格式，压缩系数quality=85，取值范围0~95，数字越大对图像压缩越小（图像质量越好）
        val outPath = "out_85.jpg"
        val quality = 85
        val map = MatOfInt(Imgcodecs.IMWRITE_JPEG_QUALITY, quality)
        Imgcodecs.imwrite(outPath, mat3, map)

//      String outPath1 = "out_100.jpg";
//      quality = 100;
//      MatOfInt map1 = new MatOfInt(Imgcodecs.IMWRITE_JPEG_QUALITY, quality);
//      Imgcodecs.imwrite(outPath1, mat3, map1);

        // 获取编码压缩图的二进制数据
        val bytemat = MatOfByte()
        Imgcodecs.imencode(".jpg", mat3, bytemat, map)
        val bytes = bytemat.toArray()

        // 把二进制数据保存到文件
        val outputFile = File("out_85_buf.jpg")
        try {
            FileUtils.writeByteArrayToFile(outputFile, bytes)
        } catch (e: IOException) {
            println(e.message)
            e.printStackTrace()
        }

        // Base64编码
        val base64 = org.apache.commons.codec.binary.Base64()
        val encodedString = String(base64.encode(bytes))
        System.out.printf(
            "image base64 encoded size: %d 字节, base64 prefix: %s\n",
            encodedString.length,
            encodedString.substring(0, 10)
        )
        return encodedString
    }


    suspend fun getLongPic():BufferedImage{
        var imgList = arrayListOf<BufferedImage>()
        var img = getImage(Config.changtuRect)
        imgList.add(img)

        var notDown = true

        while(notDown){
            MRobot.singleClick(MPoint(600,400),null)
            MRobot.robot.keyPress(KeyEvent.VK_PAGE_DOWN)
            delay(200)
            MRobot.robot.keyRelease(KeyEvent.VK_PAGE_DOWN)
            delay(1500)
            var img2 = getImage(Config.changtuRect)
            var imgTopSec = img2.getSubimage(0,0,img.width,15)


            var downOVer = false
            var img2Add = true
            for(y in 0 until img.height-15){
                var iT = img.getSubimage(0,y,img.width,15)
                if(ImgUtil.isImageSim(iT,imgTopSec,0.999)){
                    var h =img2.height-img.height+y
                    if(h>0) {
                        img2 = img2.getSubimage(0, img.height - y, img2.width, img2.height - img.height + y)
                    }else img2Add = false
                    notDown = false
                    break
                }
            }

            if(img2Add) {
                imgList.add(img2)
            }
            img = img2

        }

        //imglist中得图合成到一张长图里
        var totalH = imgList.sumOf {
            it.height
        }
        var imgR = BufferedImage(imgList[0].width,totalH,BufferedImage.TYPE_INT_RGB)
        var y = 0
        for(img in imgList){
            imgR.graphics.drawImage(img,0,y,null)
            y += img.height
        }

        return imgR
    }
}