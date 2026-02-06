import data.MRect
import data.toHSB
import data.toHSBFirst
import utils.ImgUtil.forEach
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


//    WX79Test.test()
    WX49Test.test()
//    getKeyImg()


    println("耗时:${System.currentTimeMillis() - start}毫秒")

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
    val doRect = MRect.createWH(47,22,58,101)
    doRect.forEach { i, i2 ->
        val color = img.getRGB(i, i2)
            val chs = color.toHSBFirst()
        if (listColors.count {
                chs in it-15..it+15
            } > 0) {
            newImg.setRGB(i, i2, color)
        }
        false
    }

    newImg.saveTo(File("C:\\Users\\Administrator\\Desktop\\debug3\\sssss2.png"))

}