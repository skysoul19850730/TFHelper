package tasks.anyue.base.ay139

import data.Config
import data.MRect
import getImage
import getImageFromRes
import opencv.MatSearch
import opencv.toGray
import opencv.toMat
import org.opencv.core.Mat
import org.opencv.core.Rect
import org.opencv.core.Size
import org.opencv.imgproc.Imgproc
import java.awt.image.BufferedImage

object AY139Util {
    var anyueFolder = "${Config.platName}/tezheng/anyue/ay139"
    private fun path(name: String) = "${anyueFolder}/$name"
    private fun getImage(name: String): BufferedImage = getImageFromRes(path(name))
    val TOP_ICONS = listOf(//采集用的
        Rect(375 + 4, 206 + 4, 77 - 8, 77 - 8),  // 0: 实心斧头，圆形
        Rect(473 + 4, 168 + 4, 77 - 8, 77 - 8),  // 1: 实心法杖，方形
        Rect(571 + 4, 169 + 4, 77 - 8, 77 - 8),  // 2: 空心法杖，方形
        Rect(669 + 4, 208 + 4, 77 - 8, 77 - 8)   // 3: 空心法杖，圆形
    )
//    val TOP_ICONS = listOf(//真实判断时，略微大些也可以的
//        Rect(374, 207, 77, 77),  //
//        Rect(473, 168, 77, 77),  //
//        Rect(572, 168, 77, 77),  //
//        Rect(669, 208, 77, 77)   //
//    )

    val bottomSize = Size(104.0, 104.0)
    val TopAllRect = MRect.createWH(350, 150, 446, 173)

    enum class MatType(val futou: Int, val circle: Int, val fill: Int, val mat: Mat) {
        Mat1(1, 1, 1, getImage("m1.png").toMat().toGray()), Mat2(
            0,
            0,
            1,
            getImage("m2.png").toMat().toGray()
        ),
        Mat3(0, 0, 0, getImage("m3.png").toMat().toGray()), Mat4(
            0,
            1,
            0,
            getImage("m4.png").toMat().toGray()
        );
//        ,Mat5(1,0,0,getImage("m5.png").toMat().toGray().binary())
//        ,Mat6(1,0,1,getImage("m6.png").toMat().toGray().binary())
//        ,Mat7(1,1,0,getImage("m7.png").toMat().toGray().binary())
//        ,Mat8(1,1,1,getImage("m8.png").toMat().toGray().binary())

        fun toPString(): String {
            return "${if (futou == 1) "斧头" else "法杖"},${if (circle == 1) "圆" else "方形"},${if (fill == 1) "填充" else "描边"}"
        }
    }


    fun getTopMatTypes(img2: BufferedImage?=null): List<MatType>? {
        val img = img2?: getImage(TopAllRect)
        val list = arrayListOf<MatType>()
        TOP_ICONS.forEach {
            val mat = img.getSubimage(it.x, it.y, it.width, it.height).toMat().toGray()
            MatType.values().firstOrNull {
                MatSearch.templateFit(it.mat, mat)
            }?.apply {
                list.add(this)
            }
        }
        if (list.size == 4) {
            return list
        }

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

    /**
     * boss出过的不会再出，所以一开始list是find的4个，每次出一个就可以减少一个
     */
    fun getBottomRunningMat(mats: List<MatType>, img: BufferedImage? = null): MatType? {
        val rect = MRect.createWH(390, 290, 150, 150)
//        val rect = MRect.createWH(500, 300, 350, 132)
        val target = (img ?: getImage(rect)).toMat().toGray()

//500 300  130 130

        return mats.firstOrNull {
            var modelScale = Mat()
            Imgproc.resize(it.mat, modelScale, bottomSize, 0.5, 0.5, Imgproc.INTER_AREA)
            MatSearch.templateFit(modelScale, target, 0.7)
        }

    }
}