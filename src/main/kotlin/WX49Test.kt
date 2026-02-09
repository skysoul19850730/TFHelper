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

    val user = "sqc"
//    val user = "Administrator"
    fun test() {
//        val rect = MRect.createWH(395,320,60,80)
//
//    getImageFromFile(File("C:\\Users\\$user\\Desktop\\debug3\\xw49\\aaa12313.png")).saveSubTo( MRect.createWH(395,320,60,80), File("C:\\Users\\$user\\Desktop\\debug3\\xw49\\tsm.png"))
//    getImageFromFile(File("C:\\Users\\$user\\Desktop\\debug3\\xw49\\wx_20260206125513.png")).saveSubTo( MRect.createWH(435,320,60,80), File("C:\\Users\\$user\\Desktop\\debug3\\xw49\\thy.png"))
//    getImageFromFile(File("C:\\Users\\$user\\Desktop\\debug3\\xw49\\wx_20260206125513.png")).saveSubTo( MRect.createWH(665,320,60,80), File("C:\\Users\\$user\\Desktop\\debug3\\xw49\\tjn.png"))
//    getImageFromFile(File("C:\\Users\\$user\\Desktop\\debug3\\xw49\\aaa12333.png")).saveSubTo( MRect.createWH(437,320,60,80), File("C:\\Users\\$user\\Desktop\\debug3\\xw49\\tzhu.png"))
//return
        val targetRect = MRect.createWH(337,320,424,80)
        val pjv = getImageFromFile(File("C:\\Users\\$user\\Desktop\\debug3\\tjn_1.png")) to "jv"
        val psm = getImageFromFile(File("C:\\Users\\$user\\Desktop\\debug3\\tsm_1.png")) to "sm"
        val psy = getImageFromFile(File("C:\\Users\\$user\\Desktop\\debug3\\thy_1.png")) to "sy"
        val pzhu = getImageFromFile(File("C:\\Users\\$user\\Desktop\\debug3\\tzhu_1.png")) to "zhu"

        val plats = listOf(pjv, psm, psy, pzhu)

//        val platPath = "C:\\Users\\Administrator\\Desktop\\debug3\\sssss_1770378427649.png"
//        val platImg = getImageFromFile(File(platPath))
//
////        val img = getImageFromFile(File("C:\\Users\\Administrator\\Desktop\\debug3\\aaa12313.png"))
//        val img = getImageFromFile(File("C:\\Users\\Administrator\\Desktop\\debug3\\aaa123134.png"))
//            .getSubImage(MRect.createWH(337,284,424,144))

        File("C:\\Users\\$user\\Desktop\\debug3\\xw49").listFiles().forEach {

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