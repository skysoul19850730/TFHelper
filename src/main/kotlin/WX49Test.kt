import data.MPoint
import data.MRect
import model.CarDoing
import opencv.MatSearch
import opencv.saveToImg
import opencv.subMat
import opencv.toMat
import org.opencv.imgcodecs.Imgcodecs
import utils.ImgUtil.slidingPixelMatch
import java.awt.Color
import java.awt.image.BufferedImage
import java.io.File

object WX49Test {

    fun test() {

        val targetRect = MRect.createWH(337,274,424,164)
        val pjv = getImageFromFile(File("C:\\Users\\Administrator\\Desktop\\debug3\\plat_jv.png")) to "jv"
        val psm = getImageFromFile(File("C:\\Users\\Administrator\\Desktop\\debug3\\plat_sm.png")) to "sm"
        val psy = getImageFromFile(File("C:\\Users\\Administrator\\Desktop\\debug3\\plat_sy.png")) to "sy"
        val pzhu = getImageFromFile(File("C:\\Users\\Administrator\\Desktop\\debug3\\plat_zhu.png")) to "zhu"

        val plats = listOf(pjv, psm, psy, pzhu)

//        val platPath = "C:\\Users\\Administrator\\Desktop\\debug3\\sssss_1770378427649.png"
//        val platImg = getImageFromFile(File(platPath))
//
////        val img = getImageFromFile(File("C:\\Users\\Administrator\\Desktop\\debug3\\aaa12313.png"))
//        val img = getImageFromFile(File("C:\\Users\\Administrator\\Desktop\\debug3\\aaa123134.png"))
//            .getSubImage(MRect.createWH(337,284,424,144))

        File("C:\\Users\\Administrator\\Desktop\\debug3\\xw49").listFiles().forEach {

            val img = getImageFromFile(it).getSubImage(targetRect)

            plats.forEach {plat->

                val pair = slidingPixelMatch(plat.first,img)
//                if(pair.first>0.1){
                    println("${it.name} ${pair.first} 位置${pair.second?.x} 名字;${plat.second}")
//                }

            }

            println("${it.name} over\n\n")

        }

        //使用image的8个图 耗时777ms
//            val pair = slidingPixelMatch(platImg, img)
//            println("${pair.first} ${pair.second?.x}")
//            val has = (pair.first > 0.5)
//            println("has:$has")
        println("\n\n")


    }

}