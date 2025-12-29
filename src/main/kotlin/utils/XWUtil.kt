package utils

import data.Config
import data.MRect
import getImage
import getImageFromFile
import log
import resFile
import java.awt.image.BufferedImage

object XWUtil {
    var xwFolder = "${Config.platName}/tezheng/xuanwo"
   val num59s =  arrayListOf<Pair<BufferedImage, Int>>()
    val rect1 = MRect.createWH(620,210,15,21)
    val rect2 = MRect.createWH(672,210,15,21)
    fun getShuzi59(img2:BufferedImage?=null): Int {

        if (num59s.isEmpty()) {
            resFile("${xwFolder}/xw59").listFiles().forEach {
                num59s.add(
                    Pair(
                        getImageFromFile(it),
                        it.nameWithoutExtension.takeLast(1).toInt()
                    )
                )
            }
        }
        val img1 =img2?: getImage(rect1)
        val img2 =img2?: getImage(rect2)
        num59s.forEach {
            if(ImgUtil.isImageSim(img1,it.first,0.95)){
                log("识别到数字：${it.second}")
                return it.second
            }
            if(ImgUtil.isImageSim(img2,it.first,0.95)){
                log("识别到数字：${it.second}")
                return it.second
            }
        }

        return 0

    }
}