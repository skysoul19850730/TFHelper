package tasks.anyue.base.ay139

import data.Config
import data.MPoint
import data.MRect
import getImage
import getImageFromRes
import getSubImage
import opencv.MatSearch
import opencv.toGray
import opencv.toMat
import org.opencv.core.Mat
import org.opencv.core.Rect
import org.opencv.core.Size
import org.opencv.imgproc.Imgproc
import utils.ImgUtil
import utils.ImgUtil.slidingPixelMatch
import java.awt.image.BufferedImage

object AY139Util {
    var anyueFolder = "${Config.platName}/tezheng/anyue/ay139"
    private fun path(name: String) = "${anyueFolder}/$name"
    private fun getImage(name: String): BufferedImage = getImageFromRes(path(name))

    //    val TOP_ICON_RECTS = listOf(//采集用的
//        MRect.createWH(375 + 4, 206 + 4, 77 - 8, 77 - 8),  // 0: 实心斧头，圆形
//        MRect.createWH(473 + 4, 168 + 4, 77 - 8, 77 - 8),  // 1: 实心法杖，方形
//        MRect.createWH(571 + 4, 169 + 4, 77 - 8, 77 - 8),  // 2: 空心法杖，方形
//        MRect.createWH(669 + 4, 208 + 4, 77 - 8, 77 - 8)   // 3: 空心法杖，圆形
//    )
    val TOP_ICON_RECTS = listOf(//真实判断时，略微大些也可以的
        MRect.createWH(374, 207, 77, 77),  //
        MRect.createWH(473, 168, 77, 77),  //
        MRect.createWH(572, 168, 77, 77),  //
        MRect.createWH(669, 208, 77, 77)   //
    )
    val sff = getImage("ffs.png")
    val sfe = getImage("fes.png")
    val szf = getImage("zfs.png")
    val sze = getImage("zes.png")
    val ss = getImage("border_square_small.png")

    val bff = getImage("ffb.png")
    val bfe = getImage("feb.png")
    val bzf = getImage("zfb.png")
    val bze = getImage("zeb.png")
    val bs = getImage("border_square.png")

    val bottomSize = Size(104.0, 104.0)
    val TopAllRect = MRect.createWH(350, 150, 446, 173)

//    val BottomCheckRect = MRect.createWH(340, 310, 450, 104)//测试时用，因为截图可能已经去到左边了，所以范围大些，范围大了效率就低
    val BottomCheckRect = MRect.createWH(630, 310, 150, 104)//正式用，假设已经比较准了，那差不多刚出来就可以时识别了，增加效率

    class MatType(val futou: Int, val circle: Int, val fill: Int) {
        fun toPString(): String {
            return "${if (futou == 1) "斧头" else "法杖"},${if (circle == 1) "圆" else "方形"},${if (fill == 1) "填充" else "描边"}"
        }
    }

//    private fun slidingPixelMatch(
//        template: BufferedImage,
//        target: BufferedImage,
//        tolerance: Int = 15,
//    ): Pair<Double, MPoint?> {
//        return ImgUtil.slidingPixelMatch(template, target, tolerance,30)
//    }

    fun getTopMatTypes(img2: BufferedImage? = null): List<MatType>? {
        val img = img2 ?: getImage(App.rectWindow)
        val list = arrayListOf<MatType>()
        TOP_ICON_RECTS.forEach {
            getOne(img.getSubImage(it))?.let {
                list.add(it)
            }
        }
        if (list.size == 4) {
            return list
        }

        return null
    }

    private fun getOne(target: BufferedImage): MatType? {
        var futou = -1
        var circle = -1
        var fill = -1

        var max = 0.0

        var r = slidingPixelMatch(sff, target)
        if(r.first>max){
            max = r.first
            futou = 1
            fill = 1
        }

        r = slidingPixelMatch(sfe, target)
        if(r.first>max){
            max = r.first
            futou = 1
            fill = 0
        }
        r = slidingPixelMatch(szf, target)
        if(r.first>max){
            max = r.first
            futou = 0
            fill = 1
        }
        r = slidingPixelMatch(sze, target)
        if(r.first>max){
            max = r.first
            futou = 0
            fill = 0
        }

//        if (slidingPixelMatch(sff, target).first > 0.3) {
//            futou = 1
//            fill = 1
//
//        } else if (slidingPixelMatch(sfe, target).first > 0.6) {
//            futou = 1
//            fill = 0
//        } else if (slidingPixelMatch(szf, target).first > 0.6) {
//            futou = 0
//            fill = 1
//        } else if (slidingPixelMatch(sze, target).first > 0.6) {
//            futou = 0
//            fill = 0
//        }

        if (slidingPixelMatch(ss, target).first > 0.6) {
            circle = 0
        } else {
            circle = 1
        }

        if (futou > -1 && max>0.6) {
            return MatType(futou, circle, fill).apply {
                println("${this.toPString()}")
            }
        }
        println("未识别到")
        return null

    }

    fun theDifferentMat(mats: List<MatType>): MatType {
        var countF = mats.count { it.futou == 1 }
        if (countF == 1) {
            return mats.first { it.futou == 1 }
        }
        if (countF == 3) {
            return mats.first { it.futou == 0 }
        }
        var countC = mats.count { it.circle == 1 }
        if (countC == 1) {
            return mats.first { it.circle == 1 }
        }
        if (countC == 3) {
            return mats.first { it.circle == 0 }
        }
        var countFill = mats.count { it.fill == 1 }
        if (countFill == 1) {
            return mats.first { it.fill == 1 }
        }
        return mats.first { it.fill == 0 }
    }

    fun getBottomRunningMat(target: BufferedImage? = null): MatType? {

        val target = target?.getSubImage(BottomCheckRect) ?: getImage(BottomCheckRect)
        var futou = -1
        var circle = -1
        var fill = -1

        var max = 0.0

        var r = slidingPixelMatch(bff, target)
        if(r.first>max){
            max = r.first
            futou = 1
            fill = 1
        }

        r = slidingPixelMatch(bfe, target)
        if(r.first>max){
            max = r.first
            futou = 1
            fill = 0
        }
        r = slidingPixelMatch(bzf, target)
        if(r.first>max){
            max = r.first
            futou = 0
            fill = 1
        }
        r = slidingPixelMatch(bze, target)
        if(r.first>max){
            max = r.first
            futou = 0
            fill = 0
        }

        if (slidingPixelMatch(bs, target).first > 0.6) {
            circle = 0
        } else {
            circle = 1
        }

        if (futou > -1 && max>0.6) {
            return MatType(futou, circle, fill)
        }
        return null

    }


}