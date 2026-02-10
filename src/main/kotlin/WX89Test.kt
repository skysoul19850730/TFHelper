import data.Config
import data.MRect
import opencv.MatSearch
import opencv.toMat
import java.awt.geom.AffineTransform
import java.awt.image.BufferedImage
import java.io.File

class TSFSU {
    //460,190,7575


}

fun main() {
    try {
        Class.forName("nu.pattern.OpenCV")
        nu.pattern.OpenCV.loadLocally()
    } catch (e: Exception) {
        System.loadLibrary(org.opencv.core.Core.NATIVE_LIBRARY_NAME)
    }
    val start = System.currentTimeMillis()



    val rect1 = MRect.createWH(
        460,
        194,
        72,
        72
    )

    val rect2 =
        MRect.createWH(
            460 + 2 + 72,
            194,
            72,
            72
        )
    val rect3 =
        MRect.createWH(
            460 + 2 + 72 + 3 + 72,
            194,
            72,
            72
        )
    val rect4 =
        MRect.createWH(
            460,
            194 + 3 + 72,
            72,
            72
        )
    val rect5 =
        MRect.createWH(
            460 + 2 + 72,
            194 + 3 + 72,
            72,
            72
        )
    val rect6 =
        MRect.createWH(
            460 + 2 + 72 + 3 + 72,
            194 + 3 + 72,
            72,
            72
        )
    val rect7 =
        MRect.createWH(
            460,
            194 + 3 + 72 + 3 + 72,
            72,
            72
        )
    val rect8 =
        MRect.createWH(
            460 + 2 + 72,
            194 + 3 + 72 + 3 + 72,
            72,
            72
        )
    val rect9 =
        MRect.createWH(
            460 + 2 + 72 + 3 + 72,
            194 + 3 + 72 + 3 + 72,
            72,
            72
        )
    val rects = listOf(rect1, rect2, rect3, rect4, rect5, rect6, rect7, rect8, rect9)

//    val imgTest = getImageFromFile(File("E:\\ideaspace\\TFHelperHome\\tfres\\logs\\xuanwo\\26_02_09\\10_46_15\\24213324.png"))
    var anyueFolder = "${Config.platName}/tezheng/xuanwo/xw89"
//    val okImg = getImageFromRes("${anyueFolder}/xw89_${4}.png").toMat()

//    val r = MatSearch.templateFit(okImg,imgTest.toMat())

    File("C:\\Users\\sqc\\Desktop\\xw89test").listFiles().forEachIndexed { index, file ->
        val okImg = getImageFromRes("${anyueFolder}/xw89_${index}.png").toMat()
        println("folder :${file.name}")
        file.listFiles().forEach {
            val target = getImageFromFile(it).toMat()
            if(MatSearch.templateFit(okImg,target)){
                println(" ${it.name} 第${index + 1}位成功")
            }else{
                println(" ${it.name} 第${index + 1}位失败")
            }
        }
        println("\n\n\n")
    }
    return

//
//    rects.forEachIndexed { i, it ->
//        val okImg = getImageFromRes("${anyueFolder}/xw89_${i}.png").toMat()
//
//        var mImg = imgTest.getSubImage(it)
//        var count = 0
//        while (!MatSearch.templateFit(okImg,imgTest.getSubImage(it.scale(1.2f)).toMat())) {
//            count++
//            mImg = rotateImage(mImg, 90.0)
//
//            mImg.foreach { i, i2 ->
//
//                imgTest.setRGB(it.left + i, it.top + i2, mImg.getRGB(i, i2))
//                false
//            }
//            imgTest
//        }
////        if(MatSearch.templateFit(okImg,imgTest.getSubImage(it.scale(1.2f)).toMat())){
////            println("第${i + 1}位成功")
////        }else{
////            println("第${i + 1}位失败")
////        }
//        println("第${i + 1}位，需要旋转${count}次")
//    }
    println("耗时: ${System.currentTimeMillis() - start}")



    println("over")

}

fun rotateImage(image: BufferedImage, angle: Double): BufferedImage {
    val radians = Math.toRadians(angle)
    val sin = Math.abs(Math.sin(radians))
    val cos = Math.abs(Math.cos(radians))

    val width = image.width
    val height = image.height

    val newWidth = (width * cos + height * sin).toInt()
    val newHeight = (height * cos + width * sin).toInt()

    val transform = AffineTransform()
    transform.translate((newWidth - width) / 2.0, (newHeight - height) / 2.0)
    transform.rotate(radians, (width / 2).toDouble(), (height / 2).toDouble())

    val rotatedImage = BufferedImage(newWidth, newHeight, image.type)
    val g2d = rotatedImage.createGraphics()
    g2d.transform(transform)
    g2d.drawImage(image, 0, 0, null)
    g2d.dispose()

    return rotatedImage
}