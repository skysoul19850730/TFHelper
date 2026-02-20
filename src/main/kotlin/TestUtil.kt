import data.Config
import androidx.compose.ui.window.WindowPosition.PlatformDefault.y
import data.MRect
import data.toHSB
import data.toHSBFirst
import kotlinx.coroutines.delay
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.sourceforge.tess4j.util.ImageHelper.getScaledInstance
import opencv.*
import org.opencv.core.Core
import org.opencv.core.Mat
import org.opencv.core.Rect
import org.opencv.core.Scalar
import org.opencv.imgcodecs.Imgcodecs
import org.opencv.imgproc.Imgproc
import tasks.daxuanwo.utils.WX89
import utils.ImgUtil
import utils.ImgUtil.forEach
import utils.MRobot
import java.awt.Color
import java.awt.image.BufferedImage
import java.io.File

fun main() {
    try {
        Class.forName("nu.pattern.OpenCV")
        nu.pattern.OpenCV.loadLocally()
    } catch (e: Exception) {
        System.loadLibrary(org.opencv.core.Core.NATIVE_LIBRARY_NAME)
    }
    val start = System.currentTimeMillis()

    val img = getImageFromRes("xiaochengxu/heros/xiaolu/xiaolu/xiaolu0.png")
    val img2 = getImageFromRes("xiaochengxu/heros/tuqiu/tuqiu/tuqiu0.png")

    val r= ImgUtil.isImageSim(img,img2)
//    WX89.autoDo {
//        System.currentTimeMillis()-start>10000
//    }
//
//    GlobalScope.launch {
//        delay(14000)
//        WX89.doing = false
//    }

    WX69Test.test()
//    Ay139Test.test()
//    WX79Test.test()
//    WX49Test.test()
//    getKeyImg()
//    getSmallImg()

//    while(System.currentTimeMillis() - start<10000){
//        Thread.sleep(1000)
//    }


    println("耗时:${System.currentTimeMillis() - start}毫秒")

}

fun getSmallImg() {
    var img = getImageFromFile(File("C:\\Users\\Administrator\\Desktop\\debug\\feb.png"))
//        .getSubImage( MRect.createWH(619 , 307 , 113, 113))
//669 208
    img.saveSubTo(
        MRect.createWH(1, 6, img.width - 11, img.height - 21),
        File("C:\\Users\\Administrator\\Desktop\\debug\\feb2.png")
    )
    return
    val newImg = BufferedImage(img.width, img.height, BufferedImage.TYPE_INT_ARGB)
//
    img.foreach { i, i2 ->

        if ((img.width / 2 - i) * (img.width / 2 - i) + (img.width / 2 - i2) * (img.width / 2 - i2) < 48 * 48) {
//            newImg.setRGB(i, i2, Color.BLACK.rgb)
            val color = img.getRGB(i, i2)
            if (colorCompare(Color(color), Color.WHITE) || color.toHSBFirst() in 260..298) {
                newImg.setRGB(i, i2, color)
            }

        }

        false

    }

    img = newImg
    var rect = MRect()

    for (x in 0 until img.width) {
        if (rect.left > 0) break
        for (y in 0 until img.height) {
            val color = img.getRGB(x, y)
            if (color != 0) {
                rect.left = x
                break
            }
        }
    }
    for (x in img.width - 1 downTo rect.left) {
        if (rect.right > 0) break
        for (y in 0 until img.height) {
            val color = img.getRGB(x, y)
            if (color != 0) {
                rect.right = x
                break
            }
        }
    }
    for (y in 0 until img.height) {
        if (rect.top > 0) break
        for (x in 0 until img.width) {
            val color = img.getRGB(x, y)
            if (color != 0) {
                rect.top = y
                break
            }
        }
    }
    for (y in img.height - 1 downTo rect.top) {
        if (rect.bottom > 0) break
        for (x in 0 until img.width) {
            val color = img.getRGB(x, y)
            if (color != 0) {
                rect.bottom = y
                break
            }
        }
    }
//    rect = MRect.create4P(8,7,58,59)
    img.saveSubTo(rect, File("C:\\Users\\Administrator\\Desktop\\debug\\feb.png"))
}

fun getKeyImg() {
    val listColors = listOf<Int>(
        Color(175, 49, 82).rgb.toHSBFirst(),
        Color(210, 170, 80).rgb.toHSBFirst(),
        Color(190, 90, 120).rgb.toHSBFirst(),
        Color(50, 180, 160).rgb.toHSBFirst(),
    )
    val img = getImageFromFile(File("C:\\Users\\Administrator\\Desktop\\debug3\\sssss.png"))

    val newImg = BufferedImage(img.width, img.height, BufferedImage.TYPE_INT_ARGB)


    val defaultRect = MRect.createWH(0, 0, img.width, img.height)

//    val doRect = defaultRect
    val doRect = MRect.createWH(47, 22, 58, 101)
    doRect.forEach { i, i2 ->
        val color = img.getRGB(i, i2)
        val chs = color.toHSBFirst()
        if (listColors.count {
                chs in it - 15..it + 15
            } > 0) {
            newImg.setRGB(i, i2, color)
        }
        false
    }

    newImg.saveTo(File("C:\\Users\\Administrator\\Desktop\\debug3\\sssss2.png"))

}